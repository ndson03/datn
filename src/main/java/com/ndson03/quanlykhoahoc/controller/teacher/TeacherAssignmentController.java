package com.ndson03.quanlykhoahoc.controller.teacher;

import com.ndson03.quanlykhoahoc.domain.dto.QuizFormDTO;
import com.ndson03.quanlykhoahoc.domain.entity.*;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentDetailsService;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentService;
import com.ndson03.quanlykhoahoc.service.course.CourseService;
import com.ndson03.quanlykhoahoc.service.course.LessonService;
import com.ndson03.quanlykhoahoc.service.quiz.QuizOptionService;
import com.ndson03.quanlykhoahoc.service.quiz.QuizQuestionService;
import com.ndson03.quanlykhoahoc.service.quiz.QuizSubmissionService;
import com.ndson03.quanlykhoahoc.service.course.StudentCourseDetailsService;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
import com.ndson03.quanlykhoahoc.service.user.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/teacher")
public class TeacherAssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private QuizQuestionService quizQuestionService;

    @Autowired
    private QuizOptionService quizOptionService;

    @Autowired
    private QuizSubmissionService quizSubmissionService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private StudentCourseDetailsService studentCourseDetailsService;

    @Autowired
    private AssignmentDetailsService assignmentDetailsService;

    @GetMapping("/{teacherId}/courses/{courseId}/lessons/{lessonId}/addNewAssignment")
    public String addNewAssignment(@PathVariable("teacherId") int teacherId,
                                   @PathVariable("courseId") int courseId,
                                   @PathVariable("lessonId") int lessonId,
                                   Model theModel) {
        Assignment assignment = new Assignment();
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();
        Course course = courseService.findCourseById(courseId);
        Lesson lesson = lessonService.findById(lessonId);

        theModel.addAttribute("assignment", assignment);
        theModel.addAttribute("teacher", teacher);
        theModel.addAttribute("course", course);
        theModel.addAttribute("lesson", lesson);
        theModel.addAttribute("courses", courses);

        return "teacher/assignment-form";
    }

    @PostMapping("/{teacherId}/courses/{courseId}/lessons/{lessonId}/addNewAssignment/save")
    public String saveAssignment(@Valid @ModelAttribute("assignment") Assignment assignment,
                                 BindingResult theBindingResult,
                                 @PathVariable("teacherId") int teacherId,
                                 @PathVariable("courseId") int courseId,
                                 @PathVariable("lessonId") int lessonId,
                                 Model theModel) {

        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();
        Course course = courseService.findCourseById(courseId);
        Lesson lesson = lessonService.findById(lessonId);

        if (theBindingResult.hasErrors()) {
            theModel.addAttribute("teacher", teacher);
            theModel.addAttribute("courses", courses);
            theModel.addAttribute("course", course);
            theModel.addAttribute("lesson", lesson);
            return "teacher/assignment-form";
        }
        assignment.setDaysRemaining(findDayDifference(assignment));
        assignment.setLesson(lesson); // Set the lesson for this assignment

        assignmentService.save(assignment);

        // Get students enrolled in the course
        List<Student> students = course.getStudents();

        for(Student student : students) {
            StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(student.getId(), courseId);
            AssignmentDetails assignmentDetail = new AssignmentDetails();
            assignmentDetail.setAssignment(assignment);
            assignmentDetail.setStudentCourseDetails(studentCourseDetails);
            assignmentDetail.setIsDone(0);
            assignmentDetailsService.save(assignmentDetail);
        }

        if (assignment.isQuiz()) {
            return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId + "/assignment/" + assignment.getId() + "/quiz/create";
        }

        theModel.addAttribute("teacher", teacher);

        return "redirect:/teacher/" + teacherId + "/courses/" + courseId + "/lessons/" + lessonId;
    }

    @GetMapping("/{teacherId}/courses/{courseId}/lessons/{lessonId}/assignmentDetails/{studentId}/{assignmentId}")
    public String viewStudentQuizSubmission(@PathVariable("teacherId") int teacherId,
                                            @PathVariable("courseId") int courseId,
                                            @PathVariable("lessonId") int lessonId,
                                            @PathVariable("studentId") int studentId,
                                            @PathVariable("assignmentId") int assignmentId,
                                            Model model) {
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        Lesson lesson = lessonService.findById(lessonId);
        Assignment assignment = assignmentService.findById(assignmentId);
        List<Course> courses = teacher.getCourses();

        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);

        if (studentCourseDetails == null || assignment == null) {
            return "redirect:/teacher/" + teacherId + "/courses/" + courseId + "/lessons/" + lessonId;
        }

        QuizSubmission submission = quizSubmissionService.findByAssignmentAndStudentCourseDetailsId(
                assignmentId, studentCourseDetails.getId());

        if (submission == null) {
            return "redirect:/teacher/" + teacherId + "/courses/" + courseId + "/lessons/" + lessonId + "/assignments/" + assignmentId;
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

        model.addAttribute("teacher", teacher);
        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("lesson", lesson);
        model.addAttribute("courses", courses);
        model.addAttribute("assignment", assignment);
        model.addAttribute("submission", submission);
        model.addAttribute("questions", questions);
        model.addAttribute("selectedAnswers", selectedAnswers);

        return "teacher/student-quiz-result";
    }

    @GetMapping("/{teacherId}/courses/{courseId}/lessons/{lessonId}/assignments/{assignmentId}/delete")
    public String deleteAssignment(@PathVariable("teacherId") int teacherId,
                                   @PathVariable("courseId") int courseId,
                                   @PathVariable("lessonId") int lessonId,
                                   @PathVariable("assignmentId") int assignmentId) {
        assignmentService.deleteAssignmentById(assignmentId);

        return "redirect:/teacher/" + teacherId + "/courses/" + courseId + "/lessons/" + lessonId;
    }

    @GetMapping("/{teacherId}/courses/{courseId}/lessons/{lessonId}/assignments/{assignmentId}/student/{studentId}/reset")
    public String resetAssignment(@PathVariable("teacherId") int teacherId,
                                  @PathVariable("courseId") int courseId,
                                  @PathVariable("lessonId") int lessonId,
                                  @PathVariable("assignmentId") int assignmentId,
                                  @PathVariable("studentId") int studentId) {
        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);
        QuizSubmission quizSubmission = quizSubmissionService.findByAssignmentAndStudentCourseDetailsId(assignmentId, studentCourseDetails.getId());
        quizSubmissionService.deleteSubmissionById(quizSubmission.getId());
        AssignmentDetails assignmentDetails = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(assignmentId, studentCourseDetails.getId());
        assignmentDetails.setIsDone(0);
        assignmentDetailsService.save(assignmentDetails);

        return "redirect:/teacher/" + teacherId + "/courses/" + courseId + "/lessons/" + lessonId + "/assignments/" + assignmentId;
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("assignmentId") int theId, Model theModel) {
        Assignment assignment = assignmentService.findById(theId);
        theModel.addAttribute("assignment", assignment);
        return "assignment-form";
    }

    // Display quiz creation form
    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/quiz/create")
    public String showQuizCreationForm(@PathVariable("assignmentId") int assignmentId,
                                       @PathVariable("teacherId") int teacherId,
                                       @PathVariable("courseId") int courseId,
                                       @PathVariable("lessonId") int lessonId,
                                       Model model) {
        Assignment assignment = assignmentService.findById(assignmentId);
        if (assignment == null) {
            return "redirect:/teacher/" + teacherId + "/courses/" + courseId + "/lessons/" + lessonId;
        }

        Teacher teacher = teacherService.findByTeacherId(teacherId);
        Course course = courseService.findCourseById(courseId);
        Lesson lesson = lessonService.findById(lessonId);

        QuizFormDTO quizFormDTO = new QuizFormDTO();
        quizFormDTO.setAssignment(assignment);

        // Load existing questions if any
        List<QuizQuestion> existingQuestions = quizQuestionService.findByAssignmentId(assignmentId);
        if (!existingQuestions.isEmpty()) {
            quizFormDTO.setQuestions(existingQuestions);
        } else {
            quizFormDTO.setQuestions(new ArrayList<>());
        }

        model.addAttribute("quizFormData", quizFormDTO);
        model.addAttribute("teacher", teacher);
        model.addAttribute("course", course);
        model.addAttribute("lesson", lesson);

        return "teacher/quiz-create-form";
    }

    // Add a new question
    @PostMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/quiz/addQuestion")
    public String addQuestion(@PathVariable("assignmentId") int assignmentId,
                              @PathVariable("teacherId") int teacherId,
                              @PathVariable("courseId") int courseId,
                              @PathVariable("lessonId") int lessonId,
                              @RequestParam("questionText") String questionText,
                              @RequestParam("optionText0") String optionText0,
                              @RequestParam("optionText1") String optionText1,
                              @RequestParam("optionText2") String optionText2,
                              @RequestParam("optionText3") String optionText3,
                              @RequestParam("correctOptionIndex") int correctOptionIndex) {

        Assignment assignment = assignmentService.findById(assignmentId);

        // Create and save the question
        QuizQuestion question = new QuizQuestion();
        question.setQuestionText(questionText);
        question.setAssignment(assignment);
        quizQuestionService.save(question);

        // Create and save the options
        List<QuizOption> options = new ArrayList<>();

        QuizOption option1 = new QuizOption();
        option1.setOptionText(optionText0);
        option1.setQuestion(question);
        quizOptionService.save(option1);
        options.add(option1);

        QuizOption option2 = new QuizOption();
        option2.setOptionText(optionText1);
        option2.setQuestion(question);
        quizOptionService.save(option2);
        options.add(option2);

        QuizOption option3 = new QuizOption();
        option3.setOptionText(optionText2);
        option3.setQuestion(question);
        quizOptionService.save(option3);
        options.add(option3);

        QuizOption option4 = new QuizOption();
        option4.setOptionText(optionText3);
        option4.setQuestion(question);
        quizOptionService.save(option4);
        options.add(option4);

        // Set the correct option
        question.setCorrectOptionId(options.get(correctOptionIndex).getId());
        quizQuestionService.save(question);

        return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId + "/assignment/" + assignmentId + "/quiz/create";
    }

    // Remove a question
    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/quiz/removeQuestion")
    public String removeQuestion(@PathVariable("assignmentId") int assignmentId,
                                 @PathVariable("teacherId") int teacherId,
                                 @PathVariable("courseId") int courseId,
                                 @PathVariable("lessonId") int lessonId,
                                 @RequestParam("questionId") int questionId) {

        quizQuestionService.deleteById(questionId);

        return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId + "/assignment/" + assignmentId + "/quiz/create";
    }

    // Save the quiz
    @PostMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/quiz/save")
    public String saveQuiz(@RequestParam("assignmentId") int assignmentId,
                           @PathVariable("teacherId") int teacherId,
                           @PathVariable("courseId") int courseId,
                           @PathVariable("lessonId") int lessonId) {

        Assignment assignment = assignmentService.findById(assignmentId);
        assignment.setQuiz(true);
        assignmentService.save(assignment);

        return "redirect:/teacher/" + teacherId + "/courses/" + courseId + "/lessons/" + lessonId;
    }

    private int findDayDifference(Assignment assignment) {
        String dateString = assignment.getDueDate();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate dueDate = LocalDate.parse(dateString, dtf);
            LocalDate today = LocalDate.now();
            int dayDiff = (int) Duration.between(today.atStartOfDay(), dueDate.atStartOfDay()).toDays();

            return dayDiff;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}