package com.ndson03.quanlykhoahoc.controller.teacher;

import com.ndson03.quanlykhoahoc.domain.entity.Course;
import com.ndson03.quanlykhoahoc.domain.entity.Lesson;
import com.ndson03.quanlykhoahoc.domain.entity.Teacher;
import com.ndson03.quanlykhoahoc.service.course.CourseService;
import com.ndson03.quanlykhoahoc.service.course.LessonService;
import com.ndson03.quanlykhoahoc.service.user.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/teacher")
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
    @GetMapping("/{teacherId}/course/{courseId}/lesson/addNewLesson")
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
    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/updateLesson")
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
    @PostMapping("/{teacherId}/course/{courseId}/lesson/saveLesson")
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

    @GetMapping("/{teacherId}/course/{courseId}/lesson/deleteLesson")
    public String delete(@RequestParam("lessonId") int id, @PathVariable("teacherId") int teacherId) {
        Lesson lesson = lessonService.findById(id);
        int courseId = lesson.getCourse().getId();

        lessonService.deleteById(id);

        return "redirect:/teacher/" + teacherId + "/courses/" + courseId;
    }
}