package com.ndson03.quanlykhoahoc.controller.student;

import com.ndson03.quanlykhoahoc.domain.dto.QuizSubmissionFormDTO;
import com.ndson03.quanlykhoahoc.domain.entity.*;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentDetailsService;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentFileSubmissionService;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentService;
import com.ndson03.quanlykhoahoc.service.course.CourseService;
import com.ndson03.quanlykhoahoc.service.course.LessonService;
import com.ndson03.quanlykhoahoc.service.course.StudentCourseDetailsService;
import com.ndson03.quanlykhoahoc.service.quiz.*;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
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
import java.time.ZoneId;
import java.util.*;

@Controller
@RequestMapping("/student")
public class StudentAssignmentController {

    @Value("${file.upload.directory}")
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
    private LessonService lessonService;

    @Autowired
    private StudentCourseDetailsService studentCourseDetailsService;

    @Autowired
    private AssignmentFileSubmissionService fileSubmissionService;

    @GetMapping("/{studentId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/345")
    public String viewAssignmentDetails(@PathVariable("studentId") int studentId,
                                        @PathVariable("courseId") int courseId,
                                        @PathVariable("lessonId") int lessonId,
                                        @PathVariable("assignmentId") int assignmentId,
                                        Model model) {
        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        Lesson lesson = lessonService.findById(lessonId);
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
                List<AssignmentFileSubmission> assignmentFileSubmissions = fileSubmissionService.findByAssignmentDetailsId(assignmentDetails.getId());
                model.addAttribute("fileSubmissions", assignmentFileSubmissions);
            }
        }

        return "student/student-assignment-detail";
    }

    @GetMapping("/{studentId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}")
    public String viewAssignment(@PathVariable("studentId") int studentId,
                                 @PathVariable("courseId") int courseId,
                                 @PathVariable("lessonId") int lessonId,
                                 @PathVariable("assignmentId") int assignmentId) {

        Assignment assignment = assignmentService.findById(assignmentId);

        if (assignment.isQuiz()) {
            return "redirect:/student/" + studentId + "/course/" + courseId +
                    "/lesson/" + lessonId + "/assignment/" + assignmentId + "/quiz";
        } else {
            return "redirect:/student/" + studentId + "/course/" + courseId +
                    "/lesson/" + lessonId + "/assignment/" + assignmentId + "/file";
        }
    }

    @GetMapping("/{studentId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/quiz")
    public String viewQuizAssignment(@PathVariable("studentId") int studentId,
                                     @PathVariable("courseId") int courseId,
                                     @PathVariable("lessonId") int lessonId,
                                     @PathVariable("assignmentId") int assignmentId,
                                     Model model) {
        // Load common data
        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        Lesson lesson = lessonService.findById(lessonId);
        Assignment assignment = assignmentService.findById(assignmentId);
        List<Course> courses = student.getCourses();

        // Get student course details
        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);
        AssignmentDetails assignmentDetails = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(
                assignmentId, studentCourseDetails.getId());

        // Add basic attributes
        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("lesson", lesson);
        model.addAttribute("assignment", assignment);
        model.addAttribute("assignmentDetails", assignmentDetails);
        model.addAttribute("courseId", courseId);

        // Quiz-specific data
        QuizSubmission existingSubmission = quizSubmissionService.findByAssignmentAndStudentCourseDetailsId(
                assignmentId, studentCourseDetails.getId());

        model.addAttribute("existingSubmission", existingSubmission);

        if (existingSubmission != null) {
            model.addAttribute("score", existingSubmission.getScore());
            model.addAttribute("endTime", existingSubmission.getSubmissionDate());
        }

        return "student/quiz-assignment";
    }

    @GetMapping("/{studentId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/file")
    public String viewFileSubmissionAssignment(@PathVariable("studentId") int studentId,
                                               @PathVariable("courseId") int courseId,
                                               @PathVariable("lessonId") int lessonId,
                                               @PathVariable("assignmentId") int assignmentId,
                                               Model model) {
        // Load common data
        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        Lesson lesson = lessonService.findById(lessonId);
        Assignment assignment = assignmentService.findById(assignmentId);
        List<Course> courses = student.getCourses();

        // Get student course details
        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);
        AssignmentDetails assignmentDetails = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(
                assignmentId, studentCourseDetails.getId());

        // Add basic attributes
        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("lesson", lesson);
        model.addAttribute("assignment", assignment);
        model.addAttribute("assignmentDetails", assignmentDetails);
        model.addAttribute("courseId", courseId);

        // File submission specific data
        if (assignmentDetails != null) {
            List<AssignmentFileSubmission> assignmentFileSubmissions = fileSubmissionService.findByAssignmentDetailsId(assignmentDetails.getId());
            model.addAttribute("assignmentFileSubmissions", assignmentFileSubmissions);
        }

        return "student/file-assignment";
    }



    @PostMapping("/{studentId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/submit-file")
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

    @GetMapping("/{studentId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/delete-file/{fileId}")
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

    @GetMapping("/{studentId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/quiz/take")
    public String takeQuiz(@PathVariable("studentId") int studentId,
                           @PathVariable("courseId") int courseId,
                           @PathVariable("lessonId") int lessonId,
                           @PathVariable("assignmentId") int assignmentId,
                           Model model) {

        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        Lesson lesson = lessonService.findById(lessonId);
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

        QuizSubmissionFormDTO formData = new QuizSubmissionFormDTO();
        formData.setAssignmentId(assignmentId);
        formData.setStudentCourseDetailsId(studentCourseDetails.getId());

        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("lesson", lesson);
        model.addAttribute("assignment", assignment);
        model.addAttribute("questions", questions);
        model.addAttribute("formData", formData);

        return "student/quiz-take-form";
    }

    @PostMapping("/{studentId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/quiz/submit")
    public String submitQuiz(@ModelAttribute("formData") QuizSubmissionFormDTO formData,
                             @PathVariable("studentId") int studentId,
                             @PathVariable("courseId") int courseId,
                             @PathVariable("lessonId") int lessonId,
                             @PathVariable("assignmentId") int assignmentId,
                             Model model) {

        Student student = studentService.findByStudentId(studentId);
        Assignment assignment = assignmentService.findById(assignmentId);
        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);

        if (assignment == null || !assignment.isQuiz() || studentCourseDetails == null) {
            return "redirect:/student/" + studentId + "/courses/" + courseId;
        }

        System.out.println("Selected options: " + formData.getSelectedOptions());

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

                int selectedOption = formData.getSelectedOptions().get(i);
                int correctOption = questions.get(i).getCorrectOptionId();

                System.out.println("Question " + (i+1) + ": Selected=" + selectedOption
                        + ", Correct=" + correctOption);
                // Check if answer is correct
                if (Objects.equals(questions.get(i).getCorrectOptionId(), formData.getSelectedOptions().get(i))) {
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


        assignmentDetails.setIsDone(1); // Mark as completed
        assignmentDetails.setScore(score);
        Date date = submission.getSubmissionDate();
        LocalDateTime localDateTime = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        assignmentDetails.setSubmitTime(localDateTime);
        assignmentDetailsService.save(assignmentDetails);

        return "redirect:/student/" + studentId + "/course/" + courseId + "/lesson/" + lessonId + "/assignment/" + assignmentId + "/quiz/result";
    }

    @GetMapping("/{studentId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/quiz/result")
    public String viewQuizResult(@PathVariable("studentId") int studentId,
                                 @PathVariable("courseId") int courseId,
                                 @PathVariable("lessonId") int lessonId,
                                 @PathVariable("assignmentId") int assignmentId,
                                 Model model) {

        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        Assignment assignment = assignmentService.findById(assignmentId);
        List<Course> courses = student.getCourses();

        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);

        if (studentCourseDetails == null || assignment == null || !assignment.isQuiz()) {
            return "redirect:/student/" + studentId + "/course/" + courseId;
        }

        QuizSubmission submission = quizSubmissionService.findByAssignmentAndStudentCourseDetailsId(
                assignmentId, studentCourseDetails.getId());

        if (submission == null) {
            return "redirect:/student/" + studentId + "/course/" + courseId + "/assignment/" + assignmentId;
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

        Lesson lesson = lessonService.findById(lessonId);
        model.addAttribute("lesson", lesson);
        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("assignment", assignment);
        model.addAttribute("submission", submission);
        model.addAttribute("questions", questions);
        model.addAttribute("selectedAnswers", selectedAnswers);

        return "student/quiz-result";
    }

}