package com.ndson03.quanlykhoahoc.controller;

import com.ndson03.quanlykhoahoc.entity.Course;
import com.ndson03.quanlykhoahoc.entity.Lesson;
import com.ndson03.quanlykhoahoc.entity.Teacher;
import com.ndson03.quanlykhoahoc.service.CourseService;
import com.ndson03.quanlykhoahoc.service.LessonService;
import com.ndson03.quanlykhoahoc.service.TeacherService;
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
    private TeacherService teacherService;

    @Autowired
    public LessonController(LessonService lessonService, CourseService courseService, TeacherService teacherService) {
        this.lessonService = lessonService;
        this.courseService = courseService;
        this.teacherService = teacherService;
    }

    // Hiển thị form thêm bài học mới
    @GetMapping("/{teacherId}/showFormForAdd/{courseId}")
    public String showFormForAdd(@PathVariable("teacherId") int teacherId, @PathVariable("courseId") int courseId, Model model) {
        Course course = courseService.findCourseById(courseId);
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setOrderNumber(lessonService.getNextOrderNumber(courseId));

        model.addAttribute("lesson", lesson);
        model.addAttribute("course", course);
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses);

        return "teacher/lesson-form";
    }

    // Hiển thị form cập nhật bài học
    @GetMapping("/{teacherId}/showFormForUpdate/{lessonId}")
    public String showFormForUpdate(@PathVariable("teacherId") int teacherId, @PathVariable("lessonId") int id, Model model) {
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();
        Lesson lesson = lessonService.findById(id);
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", lesson.getCourse());
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses);

        return "teacher/lesson-form";
    }

    // Lưu bài học
    @PostMapping("{teacherId}/save")
    public String saveLesson(@Valid @ModelAttribute("lesson") Lesson lesson,
                             BindingResult bindingResult,
                             @RequestParam("courseId") int courseId,
                             @PathVariable("teacherId") int teacherId,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("course", courseService.findCourseById(courseId));
            return "teacher/lesson-form";
        }

        // Thiết lập khóa học cho bài học
        if (lesson.getCourse() == null) {
            Course course = courseService.findCourseById(courseId);
            lesson.setCourse(course);
        }

        // Lưu bài học
        lessonService.save(lesson);

        return "redirect:/teacher/" + teacherId + "/courses/" + courseId;


    }

    @GetMapping("/{teacherId}/delete")
    public String delete(@RequestParam("lessonId") int id, @PathVariable("teacherId") int teacherId, Model model) {
        Lesson lesson = lessonService.findById(id);
        int courseId = lesson.getCourse().getId();

        lessonService.deleteById(id);

        return "redirect:/teacher/" + teacherId + "/courses/" + courseId;
    }
}