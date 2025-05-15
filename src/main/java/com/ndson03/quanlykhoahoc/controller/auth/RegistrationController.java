package com.ndson03.quanlykhoahoc.controller.auth;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ndson03.quanlykhoahoc.repository.user.RoleRepository;
import com.ndson03.quanlykhoahoc.domain.entity.Role;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
import com.ndson03.quanlykhoahoc.service.user.TeacherService;
import com.ndson03.quanlykhoahoc.domain.dto.UserDTO;

@Controller
@RequestMapping("/register")
public class RegistrationController {

	@Autowired
	private StudentService studentService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private RoleRepository roleRepository;

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}

	@GetMapping("/showRegistrationForm")
	public String showRegistrationForm(Model theModel) {
		theModel.addAttribute("userDto", new UserDTO());
		return "registration/registration-form";
	}

	@PostMapping("/processRegistrationForm")
	public String processRegistrationForm(@Valid @ModelAttribute("userDto") UserDTO user,
										  BindingResult theBindingResult, @RequestParam(value="role") String roleName, Model theModel) {

		// Kiểm tra lỗi validation
		if (theBindingResult.hasErrors()) {
			return "registration/registration-form";
		}

		try {
			if (roleName.equals("ROLE_STUDENT")) {
				// Kiểm tra username đã tồn tại chưa
				if (studentService.findByStudentName(user.getUserName()) != null) {
					theModel.addAttribute("registrationError", "Tên người dùng đã tồn tại!");
					return "registration/registration-form";
				}

				Role role = roleRepository.findRoleByName(roleName);
				user.setRole(role);
				studentService.save(user);
			} else { // ROLE_TEACHER
				// Kiểm tra username đã tồn tại chưa
				if (teacherService.findByTeacherName(user.getUserName()) != null) {
					theModel.addAttribute("registrationError", "Tên người dùng đã tồn tại!");
					return "registration/registration-form";
				}

				Role role = roleRepository.findRoleByName(roleName);
				user.setRole(role);
				teacherService.save(user);
			}
		} catch (Exception e) {
			theModel.addAttribute("registrationError", "Đăng ký thất bại! Vui lòng thử lại.");
			return "registration/registration-form";
		}

		// Redirect to login page with a success parameter
		return "redirect:/showLoginPage?registrationSuccess";
	}
}