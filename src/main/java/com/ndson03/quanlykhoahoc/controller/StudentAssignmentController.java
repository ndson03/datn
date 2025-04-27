package com.ndson03.quanlykhoahoc.controller;

import com.ndson03.quanlykhoahoc.dto.QuizSubmissionFormData;
import com.ndson03.quanlykhoahoc.entity.*;
import com.ndson03.quanlykhoahoc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/student")
public class StudentAssignmentController {

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
            }
        }

        return "student/student-assignment-detail";
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