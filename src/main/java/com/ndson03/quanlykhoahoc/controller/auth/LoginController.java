package com.ndson03.quanlykhoahoc.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping("/")
	public String homeRedirect() {
		return "redirect:/login/";
	}

	@GetMapping("/login")
	public String showLoginPage() {
		return "login/login-form";
	}
	
	//authenticateTheUser is automatically done by spring boot
	
	@GetMapping("/access-denied")
	public String showAccessDenied() {
		return "access-denied";
	}
}
