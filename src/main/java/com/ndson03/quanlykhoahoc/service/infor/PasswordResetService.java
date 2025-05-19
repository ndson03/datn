package com.ndson03.quanlykhoahoc.service.infor;

import com.ndson03.quanlykhoahoc.domain.entity.PasswordResetToken;
import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.domain.entity.Teacher;
import com.ndson03.quanlykhoahoc.repository.infor.PasswordResetTokenRepository;
import com.ndson03.quanlykhoahoc.repository.user.StudentRepository;
import com.ndson03.quanlykhoahoc.repository.user.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final int EXPIRATION_MINUTES = 60 * 24; // 24 giờ

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Tìm kiếm người dùng bằng email (có thể là Teacher hoặc Student)
    public Object findUserByEmail(String email) {
        // Tìm trong Teacher trước
        Teacher teacher = teacherRepository.findByEmail(email);
        if (teacher != null) {
            return teacher;
        }

        // Nếu không tìm thấy, tìm trong Student
        Student student = studentRepository.findByEmail(email);
        return student;
    }

    // Xác định loại tài khoản (Teacher hoặc Student)
    private String determineAccountType(Object user) {
        if (user instanceof Teacher) {
            return "TEACHER";
        } else if (user instanceof Student) {
            return "STUDENT";
        }
        System.out.println("account class = " + (user == null ? "null" : user.getClass().getName()));

        throw new IllegalArgumentException("Unknown user type");
    }

    // Tạo token đặt lại mật khẩu cho người dùng
    public void createPasswordResetTokenForUser(Object user) {
        String accountType = determineAccountType(user);
        int accountId;
        String email;
        String fullName;

        if (accountType.equals("TEACHER")) {
            Teacher teacher = (Teacher) user;
            accountId = teacher.getId();
            email = teacher.getEmail();
            fullName = teacher.getName();
        } else {
            Student student = (Student) user;
            accountId = student.getId();
            email = student.getEmail();
            fullName = student.getFirstName() + " " + student.getLastName();
        }

        // Xóa token cũ nếu có
        PasswordResetToken existingToken = tokenRepository.findByAccountTypeAndAccountId(accountType, accountId);
        if (existingToken != null) {
            tokenRepository.delete(existingToken);
        }

        // Tạo token mới
        String token = UUID.randomUUID().toString();
        PasswordResetToken newToken = new PasswordResetToken(token, accountType, accountId, calculateExpiryDate());
        tokenRepository.save(newToken);

        try {
            emailService.sendPasswordResetEmail(email, token, fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra tính hợp lệ của token
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passToken = tokenRepository.findByToken(token);

        if (passToken == null) {
            return "invalidToken";
        }

        if (passToken.isExpired()) {
            tokenRepository.delete(passToken);
            return "expired";
        }

        return null; // Token hợp lệ
    }

    // Lấy thông tin người dùng từ token
    public Object getUserByPasswordResetToken(String token) {
        PasswordResetToken passToken = tokenRepository.findByToken(token);

        if (passToken.getAccountType().equals("TEACHER")) {
            return teacherRepository.findById(passToken.getAccountId()).orElse(null);
        } else {
            return studentRepository.findById(passToken.getAccountId()).orElse(null);
        }
    }

    // Thay đổi mật khẩu của người dùng
    public void changeUserPassword(Object user, String newPassword) {
        String accountType = determineAccountType(user);
        int accountId;

        if (accountType.equals("TEACHER")) {
            Teacher teacher = (Teacher) user;
            teacher.setPassword(passwordEncoder.encode(newPassword));
            teacherRepository.save(teacher);
            accountId = teacher.getId();
        } else {
            Student student = (Student) user;
            student.setPassword(passwordEncoder.encode(newPassword));
            studentRepository.save(student);
            accountId = student.getId();
        }

        // Xóa token sau khi đổi mật khẩu thành công
        PasswordResetToken token = tokenRepository.findByAccountTypeAndAccountId(accountType, accountId);
        if (token != null) {
            tokenRepository.delete(token);
        }
    }

    private Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, EXPIRATION_MINUTES);
        return cal.getTime();
    }
}
