package com.ndson03.quanlykhoahoc.controller.teacher;

import com.ndson03.quanlykhoahoc.domain.entity.Course;
import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.domain.entity.Teacher;
import com.ndson03.quanlykhoahoc.service.user.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherProfileController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/{teacherId}/profile")
    public String showTeacherProfile(@PathVariable("teacherId") int teacherId, Model theModel) {
        Teacher teacher = teacherService.findByTeacherId(teacherId); //accessing student logged in
        List<Course> courses = teacher.getCourses();

        theModel.addAttribute("teacher", teacher);
        theModel.addAttribute("courses", courses);
        return "teacher/teacher-profile";
    }
}
