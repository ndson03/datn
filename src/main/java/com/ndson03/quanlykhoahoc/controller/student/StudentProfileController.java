package com.ndson03.quanlykhoahoc.controller.student;

import com.ndson03.quanlykhoahoc.domain.entity.Course;
import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentProfileController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/{studentId}/profile")
    public String showStudentProfile(@PathVariable("studentId") int studentId, Model theModel) {
        Student student = studentService.findByStudentId(studentId); //accessing student logged in
        List<Course> courses = student.getCourses();

        theModel.addAttribute("student", student);
        theModel.addAttribute("courses", courses);
        return "student/student-profile";
    }
}
