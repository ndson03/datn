package com.ndson03.quanlykhoahoc.controller;

import com.ndson03.quanlykhoahoc.entity.Content;
import com.ndson03.quanlykhoahoc.entity.Course;
import com.ndson03.quanlykhoahoc.entity.Lesson;
import com.ndson03.quanlykhoahoc.entity.Teacher;
import com.ndson03.quanlykhoahoc.service.ContentService;
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
@RequestMapping("/contents")
public class ContentController {

    private ContentService contentService;
    private LessonService lessonService;
    private TeacherService teacherService;

    @Autowired
    public ContentController(ContentService contentService, LessonService lessonService, TeacherService teacherService) {
        this.contentService = contentService;
        this.lessonService = lessonService;
        this.teacherService = teacherService;
    }

    // Hiển thị danh sách nội dung của bài học
    @GetMapping("/lesson/{lessonId}/{teacherId}")
    public String listContentsByLesson(@PathVariable int lessonId, @PathVariable("teacherId") int teacherId, Model model) {

        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();
        // Lấy thông tin bài học
        Lesson lesson = lessonService.findById(lessonId);
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", lesson.getCourse());
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses);

        // Lấy danh sách nội dung
        List<Content> contents = contentService.findByLessonId(lessonId);
        model.addAttribute("contents", contents);

        return "teacher/list-content";
    }

    // Hiển thị form thêm nội dung mới
    @GetMapping("/showFormForAdd/{teacherId}")
    public String showFormForAdd(@RequestParam("lessonId") int lessonId, @PathVariable("teacherId") int teacherId, Model model) {
        Lesson lesson = lessonService.findById(lessonId);
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();
        Content content = new Content();
        content.setLesson(lesson);
        content.setOrderNumber(contentService.getNextOrderNumber(lessonId));

        model.addAttribute("content", content);
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", lesson.getCourse());
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses);


        return "teacher/content-form";
    }

    // Hiển thị form cập nhật nội dung
    @GetMapping("/showFormForUpdate/{teacherId}")
    public String showFormForUpdate(@RequestParam("contentId") int id,  @PathVariable("teacherId") int teacherId, Model model) {
        // Lấy thông tin nội dung
        Content content = contentService.findById(id);
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();
        model.addAttribute("content", content);
        model.addAttribute("lesson", content.getLesson());
        model.addAttribute("course", content.getLesson().getCourse());
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses);

        return "teacher/content-form";
    }

    // Lưu nội dung
    @PostMapping("/save/{teacherId}")
    public String saveContent(@Valid @ModelAttribute("content") Content content,
                              BindingResult bindingResult,
                              @RequestParam("lessonId") int lessonId,
                              @PathVariable("teacherId") int teacherId,
                              Model model) {

        if (bindingResult.hasErrors()) {
            Lesson lesson = lessonService.findById(lessonId);
            model.addAttribute("lesson", lesson);
            model.addAttribute("course", lesson.getCourse());
            return "teacher/content-form";
        }

        // Thiết lập bài học cho nội dung
        if (content.getLesson() == null) {
            Lesson lesson = lessonService.findById(lessonId);
            content.setLesson(lesson);
        }

        // Lưu nội dung
        contentService.save(content);

        return "redirect:/contents/" + "lesson/" + lessonId  + "/" + teacherId;
    }

    // Xóa nội dung
    @GetMapping("/delete/{teacherId}")
    public String delete(@RequestParam("contentId") int id, @PathVariable("teacherId") int teacherId) {
        Content content = contentService.findById(id);
        int lessonId = content.getLesson().getId();

        contentService.deleteById(id);

        return "redirect:/contents/" + "lesson/" + lessonId  + "/" + teacherId;
    }

    // Hiển thị chi tiết nội dung
    @GetMapping("/view/{contentId}/{teacherId}")
    public String viewContent(@PathVariable int contentId,  @PathVariable("teacherId") int teacherId, Model model) {
        Content content = contentService.findById(contentId);
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();
        model.addAttribute("content", content);
        model.addAttribute("lesson", content.getLesson());
        model.addAttribute("course", content.getLesson().getCourse());
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses);

        return "teacher/view-content";
    }


}