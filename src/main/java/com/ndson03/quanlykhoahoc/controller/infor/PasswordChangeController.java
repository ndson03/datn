package com.ndson03.quanlykhoahoc.controller.infor;

import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.domain.entity.Teacher;
import com.ndson03.quanlykhoahoc.domain.dto.PasswordChangeDTO;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
import com.ndson03.quanlykhoahoc.service.user.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/change-password")
public class PasswordChangeController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordChangeDTO", new PasswordChangeDTO());
        return "infor/change-password";
    }

    @PostMapping
    public String changePassword(@Valid @ModelAttribute PasswordChangeDTO passwordChangeDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session,
                                 Model model) {

        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            return "infor/change-password";
        }

        // Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.passwordChangeDTO",
                    "Mật khẩu xác nhận không khớp");
            return "infor/change-password";
        }

        // Lấy thông tin người dùng hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().toString();
        String userName = auth.getName();

        try {
            boolean isSuccess = false;
            String redirectUrl = "";

            if (role.equals("ROLE_STUDENT")) {
                Student student = studentService.findByStudentName(userName);

                // Kiểm tra mật khẩu cũ
                if (!passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), student.getPassword())) {
                    bindingResult.rejectValue("currentPassword", "error.passwordChangeDTO",
                            "Mật khẩu hiện tại không đúng");
                    return "infor/change-password";
                }

                // Cập nhật mật khẩu mới
                student.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
                studentService.save(student);
                isSuccess = true;
                redirectUrl = "/student/" + student.getId() + "/profile";

            } else if (role.equals("ROLE_TEACHER")) {
                Teacher teacher = teacherService.findByTeacherName(userName);

                // Kiểm tra mật khẩu cũ
                if (!passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), teacher.getPassword())) {
                    bindingResult.rejectValue("currentPassword", "error.passwordChangeDTO",
                            "Mật khẩu hiện tại không đúng");
                    return "infor/change-password";
                }

                // Cập nhật mật khẩu mới
                teacher.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
                teacherService.save(teacher);
                isSuccess = true;
                redirectUrl = "/teacher/" + teacher.getId() + "/profile";

            } else if (role.equals("ROLE_ADMIN")) {
                // Admin không thể đổi mật khẩu qua giao diện web vì được lưu in-memory
                redirectAttributes.addFlashAttribute("error",
                        "Admin không thể đổi mật khẩu qua giao diện này");
                return "redirect:/infor/adminPanel";
            }

            if (isSuccess) {
                redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
                return "redirect:" + redirectUrl;
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đổi mật khẩu: " + e.getMessage());
        }

        return "infor/change-password";
    }
}