package com.ndson03.quanlykhoahoc.controller.file;

import com.ndson03.quanlykhoahoc.domain.entity.AssignmentDetails;
import com.ndson03.quanlykhoahoc.domain.entity.AssignmentFileSubmission;
import com.ndson03.quanlykhoahoc.domain.entity.StudentCourseDetails;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentDetailsService;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentFileSubmissionService;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentService;
import com.ndson03.quanlykhoahoc.service.course.ContentService;
import com.ndson03.quanlykhoahoc.service.course.CourseService;
import com.ndson03.quanlykhoahoc.service.course.LessonService;
import com.ndson03.quanlykhoahoc.service.course.StudentCourseDetailsService;
import com.ndson03.quanlykhoahoc.service.quiz.QuizAnswerService;
import com.ndson03.quanlykhoahoc.service.quiz.QuizOptionService;
import com.ndson03.quanlykhoahoc.service.quiz.QuizQuestionService;
import com.ndson03.quanlykhoahoc.service.quiz.QuizSubmissionService;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class AssignmentFileSubmissionController {

    @Autowired
    AssignmentFileSubmissionService assignmentFileSubmissionService;

    @Value("${file.upload.directory}")
    private String uploadDir;

    @Autowired
    private AssignmentDetailsService assignmentDetailsService;


    @Autowired
    private StudentCourseDetailsService studentCourseDetailsService;

    @Autowired
    private AssignmentFileSubmissionService fileSubmissionService;

    @GetMapping("/download-assignment-file-submission/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") int fileId) {
        AssignmentFileSubmission assignmentFileSubmission = assignmentFileSubmissionService.findById(fileId);

        if (assignmentFileSubmission != null) {
            try {
                Path filePath = Paths.get(assignmentFileSubmission.getFilePath());
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists() || resource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + assignmentFileSubmission.getOriginalFileName() + "\"")
                            .body(resource);
                } else {
                    throw new RuntimeException("Could not read the file!");
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("Error: " + e.getMessage());
            }
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/student/{studentId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/submit-file")
    public String submitFile(@PathVariable("studentId") int studentId,
                             @PathVariable("courseId") int courseId,
                             @PathVariable("lessonId") int lessonId,
                             @PathVariable("assignmentId") int assignmentId,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam(value = "comment", required = false) String comment,
                             RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Vui lòng chọn file để tải lên");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/student/" + studentId + "/course/" + courseId + "/lesson/" + lessonId + "/assignment/" + assignmentId + "/file";
        }


        try {
            // Get or create assignment details
            StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);
            AssignmentDetails assignmentDetails = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(
                    assignmentId, studentCourseDetails.getId());

            // Create directory for this student's assignment if it doesn't exist
            String studentAssignmentDir = uploadDir + "/student_" + studentId + "/course_" + courseId+ "/lesson_" + lessonId + "/assignment_" + assignmentId;
            Path uploadPath = Paths.get(studentAssignmentDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename to avoid overwriting
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // Save file to disk
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);

            // Save file details to database
            AssignmentFileSubmission assignmentFileSubmission = new AssignmentFileSubmission();
            assignmentFileSubmission.setAssignmentDetails(assignmentDetails);
            assignmentFileSubmission.setFileName(uniqueFilename);
            assignmentFileSubmission.setOriginalFileName(originalFilename);
            assignmentFileSubmission.setFilePath(filePath.toString());
            assignmentFileSubmission.setFileSize(file.getSize());
            assignmentFileSubmission.setContentType(file.getContentType());
            assignmentFileSubmission.setUploadDate(LocalDateTime.now());
            assignmentFileSubmission.setSubmissionComment(comment);

            fileSubmissionService.save(assignmentFileSubmission);

            // Mark assignment as completed (or keep as in-progress based on your requirements)
            if (assignmentDetails.getIsDone() == 0) {
                assignmentDetails.setIsDone(1);
                assignmentDetails.setSubmitTime(LocalDateTime.now());
                assignmentDetailsService.save(assignmentDetails);
            }

            redirectAttributes.addFlashAttribute("message", "File đã được tải lên thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }

        return "redirect:/student/" + studentId + "/course/" + courseId + "/lesson/" + lessonId + "/assignment/" + assignmentId;
    }

    @GetMapping("/student/{studentId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/delete-file/{fileId}")
    public String deleteFile(@PathVariable("studentId") int studentId,
                             @PathVariable("courseId") int courseId,
                             @PathVariable("lessonId") int lessonId,
                             @PathVariable("assignmentId") int assignmentId,
                             @PathVariable("fileId") int fileId,
                             RedirectAttributes redirectAttributes) {

        AssignmentFileSubmission assignmentFileSubmission = fileSubmissionService.findById(fileId);

        if (assignmentFileSubmission != null) {
            try {
                // Delete file from disk
                Path filePath = Paths.get(assignmentFileSubmission.getFilePath());
                Files.deleteIfExists(filePath);

                // Delete record from database
                fileSubmissionService.deleteById(fileId);

                StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);

                // Check if this was the last file
                AssignmentDetails assignmentDetails = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(assignmentId, studentCourseDetails.getId());
                List<AssignmentFileSubmission> remainingFiles = fileSubmissionService.findByAssignmentDetailsId(assignmentDetails.getId());

                if (remainingFiles.isEmpty()) {
                    // No files left, mark as incomplete if your business logic requires it
                    assignmentDetails.setIsDone(0);
                    assignmentDetailsService.save(assignmentDetails);
                }

                redirectAttributes.addFlashAttribute("message", "File đã được xóa thành công!");
                redirectAttributes.addFlashAttribute("alertClass", "alert-success");

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy file!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }

        return "redirect:/student/" + studentId + "/course/" + courseId + "/lesson/" + lessonId + "/assignment/" + assignmentId;
    }

}
