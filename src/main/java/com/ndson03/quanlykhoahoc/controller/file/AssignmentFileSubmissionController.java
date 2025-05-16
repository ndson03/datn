package com.ndson03.quanlykhoahoc.controller.file;

import com.ndson03.quanlykhoahoc.domain.entity.*;
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
import java.util.Date;
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



}
