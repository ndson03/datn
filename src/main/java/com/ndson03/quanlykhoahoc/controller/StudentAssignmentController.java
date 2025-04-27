package com.ndson03.quanlykhoahoc.controller;

import com.ndson03.quanlykhoahoc.dto.QuizSubmissionFormData;
import com.ndson03.quanlykhoahoc.entity.*;
import com.ndson03.quanlykhoahoc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@RequestMapping("/student")
public class StudentAssignmentController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentDetailsService assignmentDetailsService;

    @Autowired
    private QuizQuestionService quizQuestionService;

    @Autowired
    private QuizOptionService quizOptionService;

    @Autowired
    private QuizSubmissionService quizSubmissionService;

    @Autowired
    private QuizAnswerService quizAnswerService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentCourseDetailsService studentCourseDetailsService;

    @Autowired
    private AssignmentFileSubmissionService fileSubmissionService;

    @GetMapping("/{studentId}/courses/{courseId}/assignments/{assignmentId}")
    public String viewAssignmentDetails(@PathVariable("studentId") int studentId,
                                        @PathVariable("courseId") int courseId,
                                        @PathVariable("assignmentId") int assignmentId,
                                        Model model) {
        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        Assignment assignment = assignmentService.findById(assignmentId);
        List<Course> courses = student.getCourses();

        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);
        AssignmentDetails assignmentDetails = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(
                assignmentId, studentCourseDetails.getId());

        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("assignment", assignment);
        model.addAttribute("assignmentDetails", assignmentDetails);

        // Check if this is a quiz assignment
        if (assignment.isQuiz()) {
            // Check if student has already taken this quiz
            QuizSubmission existingSubmission = quizSubmissionService.findByAssignmentAndStudentCourseDetailsId(
                    assignmentId, studentCourseDetails.getId());

            model.addAttribute("isQuiz", assignment.isQuiz());
            model.addAttribute("existingSubmission", existingSubmission);

            if (existingSubmission != null) {
                model.addAttribute("score", existingSubmission.getScore());
                model.addAttribute("endTime", existingSubmission.getSubmissionDate());
            }
        } else {
            // For file submissions, load existing files
            if (assignmentDetails != null) {
                List<AssignmentFileSubmission> fileSubmissions = fileSubmissionService.findByAssignmentDetailsId(assignmentDetails.getId());
                model.addAttribute("fileSubmissions", fileSubmissions);
            }
        }

        return "student/student-assignment-detail";
    }

    @PostMapping("/{studentId}/courses/{courseId}/assignments/{assignmentId}/submit-file")
    public String submitFile(@PathVariable("studentId") int studentId,
                             @PathVariable("courseId") int courseId,
                             @PathVariable("assignmentId") int assignmentId,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam(value = "comment", required = false) String comment,
                             RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Vui lòng chọn file để tải lên");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/student/" + studentId + "/courses/" + courseId + "/assignments/" + assignmentId;
        }

        try {
            // Get or create assignment details
            StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);
            AssignmentDetails assignmentDetails = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(
                    assignmentId, studentCourseDetails.getId());

            if (assignmentDetails == null) {
                assignmentDetails = new AssignmentDetails();
                assignmentDetails.setAssignmentId(assignmentId);
                assignmentDetails.setStudentCourseDetailsId(studentCourseDetails.getId());
                assignmentDetails.setIsDone(0);
                assignmentDetails = assignmentDetailsService.save(assignmentDetails);
            }

            // Create directory for this student's assignment if it doesn't exist
            String studentAssignmentDir = uploadDir + "/student_" + studentId + "/assignment_" + assignmentId;
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
            AssignmentFileSubmission fileSubmission = new AssignmentFileSubmission();
            fileSubmission.setAssignmentDetailsId(assignmentDetails.getId());
            fileSubmission.setFileName(uniqueFilename);
            fileSubmission.setOriginalFileName(originalFilename);
            fileSubmission.setFilePath(filePath.toString());
            fileSubmission.setFileSize(file.getSize());
            fileSubmission.setContentType(file.getContentType());
            fileSubmission.setUploadDate(LocalDateTime.now());
            fileSubmission.setSubmissionComment(comment);

            fileSubmissionService.save(fileSubmission);

            // Mark assignment as completed (or keep as in-progress based on your requirements)
            if (assignmentDetails.getIsDone() == 0) {
                assignmentDetails.setIsDone(1);
                assignmentDetails.setSubmitTime(LocalDateTime.now());

                // Calculate time spent if start time exists
                if (assignmentDetails.getStartTime() != null) {
                    long minutesSpent = assignmentDetails.getStartTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);
                    assignmentDetails.setTimeSpent((int) minutesSpent);
                }

                assignmentDetailsService.save(assignmentDetails);
            }

            redirectAttributes.addFlashAttribute("message", "File đã được tải lên thành công!");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }

        return "redirect:/student/" + studentId + "/courses/" + courseId + "/assignments/" + assignmentId;
    }

    @GetMapping("/{studentId}/courses/{courseId}/assignments/{assignmentId}/download-file/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") int fileId) {
        AssignmentFileSubmission fileSubmission = fileSubmissionService.findById(fileId);

        if (fileSubmission != null) {
            try {
                Path filePath = Paths.get(fileSubmission.getFilePath());
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists() || resource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileSubmission.getOriginalFileName() + "\"")
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

    @GetMapping("/{studentId}/courses/{courseId}/assignments/{assignmentId}/delete-file/{fileId}")
    public String deleteFile(@PathVariable("studentId") int studentId,
                             @PathVariable("courseId") int courseId,
                             @PathVariable("assignmentId") int assignmentId,
                             @PathVariable("fileId") int fileId,
                             RedirectAttributes redirectAttributes) {

        AssignmentFileSubmission fileSubmission = fileSubmissionService.findById(fileId);

        if (fileSubmission != null) {
            try {
                // Delete file from disk
                Path filePath = Paths.get(fileSubmission.getFilePath());
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

        return "redirect:/student/" + studentId + "/courses/" + courseId + "/assignments/" + assignmentId;
    }

    @GetMapping("/{studentId}/courses/{courseId}/assignments/{assignmentId}/start")
    public String startAssignment(@PathVariable("studentId") int studentId,
                                  @PathVariable("courseId") int courseId,
                                  @PathVariable("assignmentId") int assignmentId) {

        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);

        if (studentCourseDetails != null) {
            AssignmentDetails assignmentDetails = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(
                    assignmentId, studentCourseDetails.getId());

            if (assignmentDetails == null) {
                assignmentDetails = new AssignmentDetails();
                assignmentDetails.setAssignmentId(assignmentId);
                assignmentDetails.setStudentCourseDetailsId(studentCourseDetails.getId());
                assignmentDetails.setIsDone(0);
            }

            // Only set start time if it hasn't been set yet
            if (assignmentDetails.getStartTime() == null) {
                assignmentDetails.setStartTime(LocalDateTime.now());
            }

            assignmentDetailsService.save(assignmentDetails);
        }

        return "redirect:/student/" + studentId + "/courses/" + courseId + "/assignments/" + assignmentId;
    }


    @GetMapping("/{studentId}/courses/{courseId}/assignments/{assignmentId}/quiz/take")
    public String takeQuiz(@PathVariable("studentId") int studentId,
                           @PathVariable("courseId") int courseId,
                           @PathVariable("assignmentId") int assignmentId,
                           Model model) {

        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        Assignment assignment = assignmentService.findById(assignmentId);
        List<Course> courses = student.getCourses();

        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);

        if (assignment == null || !assignment.isQuiz() || studentCourseDetails == null) {
            return "redirect:/student/" + studentId + "/courses/" + courseId;
        }

        // Check if student has already taken this quiz
        QuizSubmission existingSubmission = quizSubmissionService.findByAssignmentAndStudentCourseDetailsId(
                assignmentId, studentCourseDetails.getId());

        if (existingSubmission != null) {
            return "redirect:/student/" + studentId + "/courses/" + courseId + "/assignments/" + assignmentId + "/quiz/result";
        }

        List<QuizQuestion> questions = quizQuestionService.findByAssignmentId(assignmentId);

        // Load options for each question
        for (QuizQuestion question : questions) {
            List<QuizOption> options = quizOptionService.findByQuestionId(question.getId());
            question.setOptions(options);
        }

        QuizSubmissionFormData formData = new QuizSubmissionFormData();
        formData.setAssignmentId(assignmentId);
        formData.setStudentCourseDetailsId(studentCourseDetails.getId());

        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("assignment", assignment);
        model.addAttribute("questions", questions);
        model.addAttribute("formData", formData);

        return "student/quiz-take-form";
    }

    @PostMapping("/{studentId}/courses/{courseId}/assignments/{assignmentId}/quiz/submit")
    public String submitQuiz(@ModelAttribute("formData") QuizSubmissionFormData formData,
                             @PathVariable("studentId") int studentId,
                             @PathVariable("courseId") int courseId,
                             @PathVariable("assignmentId") int assignmentId,
                             Model model) {

        Student student = studentService.findByStudentId(studentId);
        Assignment assignment = assignmentService.findById(assignmentId);
        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);

        if (assignment == null || !assignment.isQuiz() || studentCourseDetails == null) {
            return "redirect:/student/" + studentId + "/courses/" + courseId;
        }

        // Create new submission
        QuizSubmission submission = new QuizSubmission();
        submission.setAssignment(assignment);
        submission.setStudentCourseDetails(studentCourseDetails);
        submission.setSubmissionDate(new Date());

        // Save submission first to get ID
        quizSubmissionService.save(submission);

        // Get questions list
        List<QuizQuestion> questions = quizQuestionService.findByAssignmentId(assignment.getId());
        List<QuizAnswer> answers = new ArrayList<>();

        // Save answers
        int correctAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (i < formData.getSelectedOptions().size()) {
                QuizAnswer answer = new QuizAnswer();
                answer.setQuizSubmission(submission);
                answer.setQuestion(questions.get(i));
                answer.setSelectedOptionId(formData.getSelectedOptions().get(i));

                // Check if answer is correct
                if (questions.get(i).getCorrectOptionId() == formData.getSelectedOptions().get(i)) {
                    correctAnswers++;
                }

                answers.add(answer);
                quizAnswerService.save(answer);
            }
        }

        // Calculate score
        double score = questions.size() > 0 ? ((double) correctAnswers / questions.size()) * 100 : 0;
        submission.setScore(score);
        submission.setAnswers(answers);
        quizSubmissionService.save(submission);

        // Update completion status in AssignmentDetails
        AssignmentDetails assignmentDetails = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(
                assignment.getId(), studentCourseDetails.getId());

        if (assignmentDetails == null) {
            assignmentDetails = new AssignmentDetails();
            assignmentDetails.setAssignmentId(assignment.getId());
            assignmentDetails.setStudentCourseDetailsId(studentCourseDetails.getId());
        }

        assignmentDetails.setIsDone(1); // Mark as completed
        assignmentDetailsService.save(assignmentDetails);

        return "redirect:/student/" + studentId + "/courses/" + courseId + "/assignments/" + assignmentId + "/quiz/result";
    }

    @GetMapping("/{studentId}/courses/{courseId}/assignments/{assignmentId}/quiz/result")
    public String viewQuizResult(@PathVariable("studentId") int studentId,
                                 @PathVariable("courseId") int courseId,
                                 @PathVariable("assignmentId") int assignmentId,
                                 Model model) {

        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        Assignment assignment = assignmentService.findById(assignmentId);
        List<Course> courses = student.getCourses();

        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);

        if (studentCourseDetails == null || assignment == null || !assignment.isQuiz()) {
            return "redirect:/student/" + studentId + "/courses/" + courseId;
        }

        QuizSubmission submission = quizSubmissionService.findByAssignmentAndStudentCourseDetailsId(
                assignmentId, studentCourseDetails.getId());

        if (submission == null) {
            return "redirect:/student/" + studentId + "/courses/" + courseId + "/assignments/" + assignmentId;
        }

        // Load questions and answers for the quiz
        List<QuizQuestion> questions = quizQuestionService.findByAssignmentId(assignmentId);
        List<QuizAnswer> answers = submission.getAnswers();

        // Create map of question id to selected answer
        Map<Integer, Integer> selectedAnswers = new HashMap<>();
        for (QuizAnswer answer : answers) {
            selectedAnswers.put(answer.getQuestion().getId(), answer.getSelectedOptionId());
        }

        // Load options for each question
        for (QuizQuestion question : questions) {
            List<QuizOption> options = quizOptionService.findByQuestionId(question.getId());
            question.setOptions(options);
        }

        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("assignment", assignment);
        model.addAttribute("submission", submission);
        model.addAttribute("questions", questions);
        model.addAttribute("selectedAnswers", selectedAnswers);

        return "student/quiz-result";
    }

    @GetMapping("/{studentId}/courses/{courseId}/markAsCompleted/{assignmentId}")
    public String markAssignmentAsCompleted(@PathVariable("studentId") int studentId,
                                            @PathVariable("courseId") int courseId,
                                            @PathVariable("assignmentId") int assignmentId) {
        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);

        if (studentCourseDetails != null) {
            AssignmentDetails assignmentDetails = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(
                    assignmentId, studentCourseDetails.getId());

            if (assignmentDetails != null) {
                assignmentDetails.setIsDone(1);
                assignmentDetailsService.save(assignmentDetails);
            }
        }

        return "redirect:/student/" + studentId + "/courses/" + courseId + "/assignments/" + assignmentId;
    }
}