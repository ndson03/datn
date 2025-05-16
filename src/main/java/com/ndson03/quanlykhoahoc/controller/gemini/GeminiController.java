package com.ndson03.quanlykhoahoc.controller.gemini;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ndson03.quanlykhoahoc.domain.entity.Course;
import com.ndson03.quanlykhoahoc.domain.entity.Notification;
import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.domain.entity.Teacher;
import com.ndson03.quanlykhoahoc.service.gemini.GeminiService;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
import com.ndson03.quanlykhoahoc.service.user.TeacherService;
import com.ndson03.quanlykhoahoc.service.utils.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class GeminiController {


    private final GeminiService geminiService;

    @Autowired
    private  StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private NotificationService notificationService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/gemini/ask")
    public ResponseEntity<String> askQuestion(@RequestBody Map<String, String> payload) {
        // Lấy câu hỏi từ payload
        String question = payload.get("question");

        // Giả sử bạn đã nhận được câu trả lời từ API (dạng JSON string)
        String answer = geminiService.getAnswer(question);

        // Parse JSON để lấy phần text
        JsonObject jsonObject = JsonParser.parseString(answer).getAsJsonObject();
        JsonArray candidates = jsonObject.getAsJsonArray("candidates");

        // Lấy phần text từ câu trả lời
        String text = candidates.get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();

        return ResponseEntity.ok(text); // Trả về phần text
    }

    @GetMapping("/student/{studentId}/chatbot")
    public String showChatbotByStudent(@PathVariable("studentId") int studentId, Model theModel) {
        Student student = studentService.findByStudentId(studentId); //accessing student logged in
        List<Course> courses = student.getCourses();

        theModel.addAttribute("student", student);
        theModel.addAttribute("courses", courses);
        List<Notification> notifications = notificationService.findAllByStudent(student).stream().limit(5).toList();
        theModel.addAttribute("notifications", notifications);
        int unreadNotificationsCount = notificationService.countUnreadByStudent(student);
        theModel.addAttribute("unreadNotificationsCount", unreadNotificationsCount);
        return "student/student-chatbot";
    }

    @GetMapping("/teacher/{teacherId}/chatbot")
    public String showChatbotByTeacher(@PathVariable("teacherId") int teacherId, Model theModel) {
        Teacher teacher = teacherService.findByTeacherId(teacherId); //accessing student logged in
        List<Course> courses = teacher.getCourses();

        theModel.addAttribute("teacher", teacher);
        theModel.addAttribute("courses", courses);
        return "teacher/teacher-chatbot";
    }

    @GetMapping("/admin/chatbot")
    public String qnaPage() {
        return "admin/admin-chatbot";
    }

}
