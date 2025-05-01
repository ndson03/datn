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
		if (theBindingResult.hasErrors()) {
			return "registration/registration-form";
		}
		
		if(roleName.equals("ROLE_STUDENT")) {
			String userName = user.getUserName();
			
			//if username already exists in db
			if(studentService.findByStudentName(userName) != null) {
				theModel.addAttribute("userDto", new UserDTO());
				theModel.addAttribute("registrationError", "User name already exists!");
				return "registration/registration-form";
			}
					
			Role role = roleRepository.findRoleByName(roleName);
			user.setRole(role);
			studentService.save(user); //save() method converts UserDto to Student and saves it in db
		} else { //teacher role
			
			String userName = user.getUserName();
			
			//if username already exists in db
			if(teacherService.findByTeacherName(userName) != null) {
				theModel.addAttribute("userDto", new UserDTO());
				theModel.addAttribute("registrationError", "User name already exists!");
				return "registration/registration-form";
			}
					
			Role role = roleRepository.findRoleByName(roleName);
			user.setRole(role);
			teacherService.save(user);
		}
		
		
		return "login/login-form";
	}
}
