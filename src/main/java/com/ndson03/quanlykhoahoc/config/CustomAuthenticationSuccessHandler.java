package com.ndson03.quanlykhoahoc.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.domain.entity.Teacher;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
import com.ndson03.quanlykhoahoc.service.user.TeacherService;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private TeacherService teacherService;


	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication auth) throws IOException, ServletException {

		String role = auth.getAuthorities().iterator().next().toString();
		String userName = auth.getName();

		// Sau khi xác thực thành công, Spring đã migrate session => gọi lại để lấy session mới
		HttpSession session = request.getSession(); // session mới

		if(role.equals("ROLE_STUDENT")) {
			Student theStudent = studentService.findByStudentName(userName);
			session.setAttribute("user", theStudent);
			response.sendRedirect(request.getContextPath() + "/student/" + theStudent.getId() + "/courses");

		} else if(role.equals("ROLE_TEACHER")) {
			Teacher theTeacher = teacherService.findByTeacherName(userName);
			session.setAttribute("user", theTeacher);
			response.sendRedirect(request.getContextPath() + "/teacher/" + theTeacher.getId() + "/courses");

		} else { // ROLE_ADMIN
			response.sendRedirect(request.getContextPath() + "/admin/adminPanel");
		}
	}


}

