package com.ndson03.quanlykhoahoc.controller;

import com.ndson03.quanlykhoahoc.entity.*;
import com.ndson03.quanlykhoahoc.service.*;
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
@RequestMapping("/contents")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @Value("${file.upload.directory}")
    private String uploadDirectory;

    @GetMapping("/lesson/{id}/{teacherId}")
    public String viewLessonContents(@PathVariable("id") int lessonId,
                                     @PathVariable("teacherId") int teacherId,
                                     Model model) {
        // Lấy thông tin để hiển thị menu và breadcrumb
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        Lesson lesson = lessonService.findById(lessonId);
        Course course = lesson.getCourse();
        List<Course> courses = teacher.getCourses();
        List<Content> contents = contentService.findByLessonId(lessonId);

        model.addAttribute("teacher", teacher);
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("contents", contents);
        model.addAttribute("courseId", course.getId());

        return "teacher/list-content";
    }

    @GetMapping("/add/{lessonId}/{teacherId}")
    public String showAddContentForm(@PathVariable("lessonId") int lessonId,
                                     @PathVariable("teacherId") int teacherId,
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

        return "teacher/content-form";
    }

    @GetMapping("/edit/{contentId}/{teacherId}")
    public String showEditContentForm(@PathVariable("contentId") int contentId,
                                      @PathVariable("teacherId") int teacherId,
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

        return "teacher/content-form";
    }

    @PostMapping("/save/{teacherId}")
    public String saveContent(@PathVariable("teacherId") int teacherId,
                              @Valid @ModelAttribute Content content,
                              BindingResult bindingResult,
                              @RequestParam("lessonId") int lessonId,
                              @RequestParam(value = "fileUpload", required = false) MultipartFile file,
                              @RequestParam(value = "keepExistingFile", required = false) Boolean keepExistingFile,
                              Model model,
                              RedirectAttributes redirectAttributes) {

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
        if (content.getId() != 0) {
            Content existingContent = contentService.findById(content.getId());
            originalFilePath = existingContent != null ? existingContent.getContentData() : null;
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
                        String originalFilename = file.getOriginalFilename();
                        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        String newFilename = UUID.randomUUID().toString() + extension;

                        // Lưu file vào thư mục đã cấu hình
                        Path fileNameAndPath = Paths.get(uploadDirectory, newFilename);
                        Files.write(fileNameAndPath, file.getBytes());

                        // Lưu đường dẫn file vào nội dung
                        content.setContentData(newFilename);

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

                        // Reset contentData vì không có file nào
                        content.setContentData("");
                    } catch (IOException e) {
                        System.err.println("Không thể xóa file cũ: " + e.getMessage());
                    }
                }
            } else if (keepExistingFile != null && keepExistingFile && content.getId() != 0) {
                // Giữ nguyên file cũ
                content.setContentData(originalFilePath);
            }
        }

        // Lưu nội dung
        contentService.save(content);

        redirectAttributes.addFlashAttribute("successMessage",
                content.getId() == 0 ? "Thêm nội dung mới thành công!" : "Cập nhật nội dung thành công!");

        return "redirect:/contents/lesson/" + lessonId + "/" + teacherId;
    }

    @GetMapping("/delete/{contentId}/{teacherId}")
    public String deleteContent(@PathVariable("contentId") int contentId,
                                @PathVariable("teacherId") int teacherId,
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

        return "redirect:/contents/lesson/" + lessonId + "/" + teacherId;
    }

    @GetMapping("/file/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get(uploadDirectory).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Xác định content type dựa trên phần mở rộng của file
                String contentType = determineContentType(filename);

                // Xác định cách hiển thị file (inline để hiển thị trực tiếp, attachment để tải về)
                String contentDisposition = "inline";
                if (!contentType.startsWith("image/") && !contentType.startsWith("video/") && !contentType.startsWith("audio/")) {
                    contentDisposition = "attachment";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition + "; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Không thể đọc file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/view/{contentId}/{teacherId}")
    public String viewContent(@PathVariable int contentId, @PathVariable("teacherId") int teacherId, Model model) {
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

    // Phương thức xác định content type dựa trên phần mở rộng của file
    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/msword";
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "ppt":
            case "pptx":
                return "application/vnd.ms-powerpoint";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            case "mp4":
                return "video/mp4";
            case "webm":
                return "video/webm";
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "txt":
                return "text/plain";
            case "html":
            case "htm":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            default:
                return "application/octet-stream";
        }
    }
}