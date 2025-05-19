package com.ndson03.quanlykhoahoc.controller;

import com.ndson03.quanlykhoahoc.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // Hiển thị form quên mật khẩu
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "infor/forgot-password";
    }

    // Xử lý request quên mật khẩu
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String userEmail,
                                        RedirectAttributes redirectAttributes) {
        Object user = passwordResetService.findUserByEmail(userEmail);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản với email này");
            return "redirect:/forgot-password";
        }

        passwordResetService.createPasswordResetTokenForUser(user);
        redirectAttributes.addFlashAttribute("message",
                "Đã gửi email hướng dẫn đặt lại mật khẩu đến địa chỉ email của bạn");

        return "redirect:/login";
    }

    // Hiển thị form đặt lại mật khẩu
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        String result = passwordResetService.validatePasswordResetToken(token);

        if (result != null) {
            model.addAttribute("error",
                    result.equals("invalidToken") ? "Token không hợp lệ" : "Token đã hết hạn");
            return "error";
        }

        model.addAttribute("token", token);
        return "infor/reset-password";
    }

    // Xử lý đặt lại mật khẩu
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {

        String result = passwordResetService.validatePasswordResetToken(token);

        if (result != null) {
            redirectAttributes.addFlashAttribute("error",
                    result.equals("invalidToken") ? "Token không hợp lệ" : "Token đã hết hạn");
            return "redirect:/login";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu không khớp");
            return "redirect:/reset-password?token=" + token;
        }

        Object user = passwordResetService.getUserByPasswordResetToken(token);
        passwordResetService.changeUserPassword(user, password);

        redirectAttributes.addFlashAttribute("message", "Mật khẩu đã được thay đổi thành công");
        return "redirect:/login";
    }
}