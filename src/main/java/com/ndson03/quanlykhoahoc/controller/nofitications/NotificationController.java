package com.ndson03.quanlykhoahoc.controller.nofitications;

import java.util.List;

import com.ndson03.quanlykhoahoc.domain.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ndson03.quanlykhoahoc.domain.entity.Notification;
import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.service.utils.NotificationService;
import com.ndson03.quanlykhoahoc.service.user.StudentService;

@Controller
@RequestMapping("/student/{studentId}/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StudentService studentService;

    @GetMapping
    public String getAllNotifications(@PathVariable("studentId") int studentId, Model model) {
        Student student = studentService.findByStudentId(studentId);
        List<Notification> notifications = notificationService.findAllByStudent(student);
        List<Course> courses = student.getCourses();

        model.addAttribute("student", student);
        model.addAttribute("notifications", notifications);
        model.addAttribute("courses", courses);

        return "student/list-notification";
    }
}

@Controller
@RequestMapping("/api/notifications")
class NotificationApiController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/{notificationId}/mark-read")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@PathVariable("notificationId") int notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}