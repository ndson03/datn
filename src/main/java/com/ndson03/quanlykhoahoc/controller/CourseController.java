package com.ndson03.quanlykhoahoc.controller;

import com.ndson03.quanlykhoahoc.entity.Course;
import com.ndson03.quanlykhoahoc.service.CourseService;
import com.ndson03.quanlykhoahoc.service.CourseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {
    private final CourseServiceImpl courseServiceImpl;

    public CourseController(CourseServiceImpl courseServiceImpl) {
        this.courseServiceImpl = courseServiceImpl;
    }

    // Các phương thức hiện có...

    // Hiển thị trang chi tiết khóa học với danh sách bài học
    @GetMapping("/details/{courseId}")
    public String viewCourseDetails(@PathVariable int courseId, Model model) {
        Course course = courseServiceImpl.findCourseById(courseId);
        model.addAttribute("course", course);

        return "course/course-details";
    }

    // ...
}