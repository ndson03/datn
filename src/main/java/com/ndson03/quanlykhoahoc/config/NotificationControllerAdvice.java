package com.ndson03.quanlykhoahoc.config;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ndson03.quanlykhoahoc.domain.entity.Notification;
import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.service.utils.NotificationService;

@ControllerAdvice(
        basePackages = {"com.ndson03.quanlykhoahoc.controller.student"}
)

public class NotificationControllerAdvice {

    @Autowired
    private NotificationService notificationService;

    @ModelAttribute("notifications")
    public List<Notification> getNotifications(HttpSession session) {
        // Lấy student từ session thay vì từ model attribute
        System.out.println("Session ID: " + session.getId());
        System.out.println("Session user attribute: " + session.getAttribute("user"));
        System.out.println("Đọc session ID: " + session.getId());

        Student student = (Student) session.getAttribute("user");
        System.out.println("student" + student);
        if (student != null && student.getId() > 0) {
            // Chỉ lấy 5 thông báo mới nhất để hiển thị trong dropdown
            return notificationService.findAllByStudent(student).stream().limit(5).toList();
        }
        return List.of();
    }

    @ModelAttribute("unreadNotificationsCount")
    public int getUnreadNotificationsCount(HttpSession session) {
        // Lấy student từ session thay vì từ model attribute
        Student student = (Student) session.getAttribute("user");
        if (student != null && student.getId() > 0) {
            return notificationService.countUnreadByStudent(student);
        }
        return 0;
    }
}