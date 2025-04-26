package com.ndson03.quanlykhoahoc.controller;

import com.ndson03.quanlykhoahoc.entity.Content;
import com.ndson03.quanlykhoahoc.entity.Lesson;
import com.ndson03.quanlykhoahoc.service.ContentService;
import com.ndson03.quanlykhoahoc.service.LessonService;
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

    @Autowired
    public ContentController(ContentService contentService, LessonService lessonService) {
        this.contentService = contentService;
        this.lessonService = lessonService;
    }

    // Hiển thị danh sách nội dung của bài học
    @GetMapping("/lesson/{lessonId}")
    public String listContentsByLesson(@PathVariable int lessonId, Model model) {
        // Lấy thông tin bài học
        Lesson lesson = lessonService.findById(lessonId);
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", lesson.getCourse());

        // Lấy danh sách nội dung
        List<Content> contents = contentService.findByLessonId(lessonId);
        model.addAttribute("contents", contents);

        return "content/list-content";
    }

    // Hiển thị form thêm nội dung mới
    @GetMapping("/showFormForAdd")
    public String showFormForAdd(@RequestParam("lessonId") int lessonId, Model model) {
        Lesson lesson = lessonService.findById(lessonId);

        Content content = new Content();
        content.setLesson(lesson);
        content.setOrderNumber(contentService.getNextOrderNumber(lessonId));

        model.addAttribute("content", content);
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", lesson.getCourse());

        return "content/content-form";
    }

    // Hiển thị form cập nhật nội dung
    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("contentId") int id, Model model) {
        // Lấy thông tin nội dung
        Content content = contentService.findById(id);
        model.addAttribute("content", content);
        model.addAttribute("lesson", content.getLesson());
        model.addAttribute("course", content.getLesson().getCourse());

        return "content/content-form";
    }

    // Lưu nội dung
    @PostMapping("/save")
    public String saveContent(@Valid @ModelAttribute("content") Content content,
                              BindingResult bindingResult,
                              @RequestParam("lessonId") int lessonId,
                              Model model) {

        if (bindingResult.hasErrors()) {
            Lesson lesson = lessonService.findById(lessonId);
            model.addAttribute("lesson", lesson);
            model.addAttribute("course", lesson.getCourse());
            return "content/content-form";
        }

        // Thiết lập bài học cho nội dung
        if (content.getLesson() == null) {
            Lesson lesson = lessonService.findById(lessonId);
            content.setLesson(lesson);
        }

        // Lưu nội dung
        contentService.save(content);

        return "redirect:/contents/lesson/" + lessonId;
    }

    // Xóa nội dung
    @GetMapping("/delete")
    public String delete(@RequestParam("contentId") int id) {
        Content content = contentService.findById(id);
        int lessonId = content.getLesson().getId();

        contentService.deleteById(id);

        return "redirect:/contents/lesson/" + lessonId;
    }

    // Hiển thị chi tiết nội dung
    @GetMapping("/view/{contentId}")
    public String viewContent(@PathVariable int contentId, Model model) {
        Content content = contentService.findById(contentId);
        model.addAttribute("content", content);
        model.addAttribute("lesson", content.getLesson());
        model.addAttribute("course", content.getLesson().getCourse());

        return "content/view-content";
    }


}