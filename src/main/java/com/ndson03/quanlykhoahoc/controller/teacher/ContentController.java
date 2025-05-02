package com.ndson03.quanlykhoahoc.controller.teacher;

import com.ndson03.quanlykhoahoc.domain.entity.*;
import com.ndson03.quanlykhoahoc.service.course.ContentService;
import com.ndson03.quanlykhoahoc.service.course.CourseService;
import com.ndson03.quanlykhoahoc.service.course.LessonService;
import com.ndson03.quanlykhoahoc.service.user.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/teacher")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private TeacherService teacherService;


    @Value("${file.upload.directory}")
    private String uploadDirectory;

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}")
    public String viewLessonContents(@PathVariable("lessonId") int lessonId,
                                     @PathVariable("teacherId") int teacherId,
                                     @PathVariable("courseId") int courseId,
                                     Model model) {
        // Lấy thông tin để hiển thị menu và breadcrumb
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        Lesson lesson = lessonService.findById(lessonId);
        Course course = lesson.getCourse();
        List<Assignment> assignments = lessonService.findByLessonId(lessonId);
        List<Course> courses = teacher.getCourses();
        List<Content> contents = contentService.findByLessonId(lessonId);

        model.addAttribute("teacher", teacher);
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("contents", contents);
        model.addAttribute("courseId", course.getId());
        model.addAttribute("assignments", assignments);
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        model.addAttribute("lessons", lessons);

        return "teacher/teacher-view-lesson";
    }

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/content/showFormForAdd")
    public String showAddContentForm(@PathVariable("lessonId") int lessonId,
                                     @PathVariable("teacherId") int teacherId,
                                     @PathVariable("courseId") int courseId,
                                     Model model) {
        // Lấy thông tin để hiển thị menu và breadcrumb
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        Lesson lesson = lessonService.findById(lessonId);
        Course course = lesson.getCourse();
        List<Course> courses = teacher.getCourses();

        // Tạo đối tượng Content mới
        Content content = new Content();
        content.setId(0); // Đánh dấu là nội dung mới
        content.setOrderNumber(contentService.getNextOrderNumber(lessonId)); // Lấy số thứ tự tiếp theo

        model.addAttribute("teacher", teacher);
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("content", content);
        model.addAttribute("courseId", course.getId());
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        model.addAttribute("lessons", lessons);
        List<Assignment> assignments = lesson.getAssignments();
        model.addAttribute("assignments", assignments);
        List<Content> contents = contentService.findByLessonId(lessonId);
        model.addAttribute("contents", contents);

        return "teacher/content-form";
    }

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/content/{contentId}/showFormForUpdate")
    public String showEditContentForm(@PathVariable("contentId") int contentId,
                                      @PathVariable("teacherId") int teacherId,
                                      @PathVariable("lessonId") int lessonId,
                                      @PathVariable("courseId") int courseId,
                                      Model model) {
        // Lấy thông tin để hiển thị menu và breadcrumb
        Content content = contentService.findById(contentId);
        Lesson lesson = content.getLesson();
        Course course = lesson.getCourse();
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();

        model.addAttribute("teacher", teacher);
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("content", content);
        model.addAttribute("courseId", course.getId());
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        model.addAttribute("lessons", lessons);
        List<Assignment> assignments = lesson.getAssignments();
        model.addAttribute("assignments", assignments);
        List<Content> contents = contentService.findByLessonId(lessonId);
        model.addAttribute("contents", contents);


        return "teacher/content-form";
    }

    @PostMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/content/save")
    public String saveContent(@PathVariable("teacherId") int teacherId,
                              @PathVariable("courseId") int courseId,
                              @Valid @ModelAttribute Content content,
                              BindingResult bindingResult,
                              @RequestParam("lessonId") int lessonId,
                              @RequestParam(value = "fileUpload", required = false) MultipartFile file,
                              @RequestParam(value = "keepExistingFile", required = false) Boolean keepExistingFile,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        final String uploadDirectory = "uploads/content_file" + "/content_" + content.getId();

        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            Teacher teacher = teacherService.findByTeacherId(teacherId);
            Lesson lesson = lessonService.findById(lessonId);
            Course course = lesson.getCourse();
            List<Course> courses = teacher.getCourses();

            model.addAttribute("teacher", teacher);
            model.addAttribute("lesson", lesson);
            model.addAttribute("course", course);
            model.addAttribute("courses", courses);
            model.addAttribute("courseId", course.getId());

            return "teacher/content-form";
        }

        // Lấy thông tin lesson
        Lesson lesson = lessonService.findById(lessonId);
        content.setLesson(lesson);

        // Lấy content hiện tại nếu đang cập nhật
        String originalFilePath = null;
        String originalFilename = null;
        if (content.getId() != 0) {
            Content existingContent = contentService.findById(content.getId());
            if (existingContent != null) {
                originalFilePath = existingContent.getContentData();
                originalFilename = existingContent.getFilename();
            }
        }

        // Xử lý upload file cho các loại nội dung là FILE, IMAGE, VIDEO
        if (content.getContentType().equals("FILE") ||
                content.getContentType().equals("IMAGE") ||
                content.getContentType().equals("VIDEO")) {

            // Nếu có file mới được upload hoặc không giữ lại file cũ
            if ((file != null && !file.isEmpty()) ||
                    (keepExistingFile == null && content.getId() != 0)) {

                // Xử lý upload file mới
                if (file != null && !file.isEmpty()) {
                    try {
                        // Tạo thư mục nếu chưa tồn tại
                        File directory = new File(uploadDirectory);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }

                        // Xóa file cũ nếu đang cập nhật và có file cũ
                        if (content.getId() != 0 && originalFilePath != null && !originalFilePath.isEmpty()) {
                            try {
                                Path oldFile = Paths.get(uploadDirectory, originalFilePath);
                                Files.deleteIfExists(oldFile);
                            } catch (IOException e) {
                                // Ghi log nếu không xóa được file cũ
                                System.err.println("Không thể xóa file cũ: " + e.getMessage());
                            }
                        }

                        // Tạo tên file duy nhất để tránh trùng lặp
                        originalFilename = file.getOriginalFilename();
                        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        String newFilename = UUID.randomUUID().toString() + extension;

                        // Lưu file vào thư mục đã cấu hình
                        Path fileNameAndPath = Paths.get(uploadDirectory, newFilename);
                        Files.write(fileNameAndPath, file.getBytes());

                        // Lưu đường dẫn file và tên file gốc vào nội dung
                        content.setContentData(newFilename);
                        content.setFilename(originalFilename);

                    } catch (IOException e) {
                        e.printStackTrace();
                        redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải lên file: " + e.getMessage());
                        return "redirect:/contents/lesson/" + lessonId + "/" + teacherId;
                    }
                } else if (keepExistingFile == null && content.getId() != 0) {
                    // Nếu không có file mới và không giữ file cũ, xóa file cũ
                    try {
                        if (originalFilePath != null && !originalFilePath.isEmpty()) {
                            Path oldFile = Paths.get(uploadDirectory, originalFilePath);
                            Files.deleteIfExists(oldFile);
                        }

                        // Reset contentData và filename vì không có file nào
                        content.setContentData("");
                        content.setFilename("");
                    } catch (IOException e) {
                        System.err.println("Không thể xóa file cũ: " + e.getMessage());
                    }
                }
            } else if (keepExistingFile != null && keepExistingFile && content.getId() != 0) {
                // Giữ nguyên file cũ và tên file gốc
                content.setContentData(originalFilePath);
                content.setFilename(originalFilename);
            }
        }

        // Lưu nội dung
        contentService.save(content);

        redirectAttributes.addFlashAttribute("successMessage",
                content.getId() == 0 ? "Thêm nội dung mới thành công!" : "Cập nhật nội dung thành công!");

        return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId;
    }

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/content/{contentId}/delete")
    public String deleteContent(@PathVariable("contentId") int contentId,
                                @PathVariable("teacherId") int teacherId,
                                @PathVariable("courseId") int courseId,
                                RedirectAttributes redirectAttributes) {

        Content content = contentService.findById(contentId);
        int lessonId = content.getLesson().getId();

        // Xóa file nếu là loại nội dung có file
        if ((content.getContentType().equals("FILE") ||
                content.getContentType().equals("IMAGE") ||
                content.getContentType().equals("VIDEO")) &&
                content.getContentData() != null && !content.getContentData().isEmpty()) {

            try {
                Path filePath = Paths.get(uploadDirectory, content.getContentData());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Không thể xóa file: " + e.getMessage());
            }
        }

        // Xóa nội dung khỏi cơ sở dữ liệu
        contentService.deleteById(contentId);

        redirectAttributes.addFlashAttribute("successMessage", "Xóa nội dung thành công!");

        return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId;
    }



    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/content/{contentId}")
    public String viewContent(@PathVariable int contentId,
                              @PathVariable("teacherId") int teacherId,
                              @PathVariable("courseId") int courseId,
                              @PathVariable("lessonId") int lessonId,
                              Model model) {
        Content content = contentService.findById(contentId);
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();

        model.addAttribute("content", content);
        model.addAttribute("lesson", content.getLesson());
        model.addAttribute("course", content.getLesson().getCourse());
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses);
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        model.addAttribute("lessons", lessons);
        List<Assignment> assignments = content.getLesson().getAssignments();
        model.addAttribute("assignments", assignments);
        List<Content> contents = contentService.findByLessonId(lessonId);
        model.addAttribute("contents", contents);

        return "teacher/view-content";
    }
}