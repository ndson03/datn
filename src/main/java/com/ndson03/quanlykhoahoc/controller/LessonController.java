package com.ndson03.quanlykhoahoc.controller;

import com.ndson03.quanlykhoahoc.entity.Course;
import com.ndson03.quanlykhoahoc.entity.Lesson;
import com.ndson03.quanlykhoahoc.service.CourseService;
import com.ndson03.quanlykhoahoc.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/lessons")
public class LessonController {

    private LessonService lessonService;
    private CourseService courseService;

    @Autowired
    public LessonController(LessonService lessonService, CourseService courseService) {
        this.lessonService = lessonService;
        this.courseService = courseService;
    }

    // Hiển thị danh sách bài học của khóa học
    @GetMapping("/course/{courseId}")
    public String listLessonsByCourse(@PathVariable int courseId, Model model) {
        // Lấy thông tin khóa học
        Course course = courseService.findCourseById(courseId);
        model.addAttribute("course", course);

        // Lấy danh sách bài học
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        model.addAttribute("lessons", lessons);

        return "lesson/list-lesson";
    }

    // Hiển thị form thêm bài học mới
    @GetMapping("/showFormForAdd")
    public String showFormForAdd(@RequestParam("courseId") int courseId, Model model) {
        Course course = courseService.findCourseById(courseId);

        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setOrderNumber(lessonService.getNextOrderNumber(courseId));

        model.addAttribute("lesson", lesson);
        model.addAttribute("course", course);

        return "lesson/lesson-form";
    }

    // Hiển thị form cập nhật bài học
    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("lessonId") int id, Model model) {
        // Lấy thông tin bài học
        Lesson lesson = lessonService.findById(id);
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", lesson.getCourse());

        return "lesson/lesson-form";
    }

    // Lưu bài học
    @PostMapping("/save")
    public String saveLesson(@Valid @ModelAttribute("lesson") Lesson lesson,
                             BindingResult bindingResult,
                             @RequestParam("courseId") int courseId,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("course", courseService.findCourseById(courseId));
            return "lessons/lesson-form";
        }

        // Thiết lập khóa học cho bài học
        if (lesson.getCourse() == null) {
            Course course = courseService.findCourseById(courseId);
            lesson.setCourse(course);
        }

        // Lưu bài học
        lessonService.save(lesson);

        return "redirect:/lessons/course/" + courseId;
    }

    // Xóa bài học
    @GetMapping("/delete")
    public String delete(@RequestParam("lessonId") int id) {
        Lesson lesson = lessonService.findById(id);
        int courseId = lesson.getCourse().getId();

        lessonService.deleteById(id);

        return "redirect:/lessons/course/" + courseId;
    }
}