package com.ndson03.quanlykhoahoc.controller;

import com.ndson03.quanlykhoahoc.dto.QuizFormData;
import com.ndson03.quanlykhoahoc.entity.*;
import com.ndson03.quanlykhoahoc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentCourseDetailsService studentCourseDetailsService;

    @Autowired
    private AssignmentDetailsService assignmentDetailsService;

    @GetMapping("/{teacherId}/courses/{courseId}/addNewAssignment")
    public String addNewAssignment(@PathVariable("teacherId") int teacherId, @PathVariable("courseId") int courseId, Model theModel) {
        Assignment assignment = new Assignment();
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();

        theModel.addAttribute("assignment", assignment);
        theModel.addAttribute("teacher", teacher);
        theModel.addAttribute("course", courseService.findCourseById(courseId));
        theModel.addAttribute("courses", courses);

        return "teacher/assignment-form";
    }

    @PostMapping("/{teacherId}/courses/{courseId}/addNewAssignment/save")
    public String saveAssignment(@Valid @ModelAttribute("assignment") Assignment assignment, BindingResult theBindingResult,
                                 @PathVariable("teacherId") int teacherId, @PathVariable("courseId") int courseId, Model theModel) {

        Teacher teacher = teacherService.findByTeacherId(teacherId);
        List<Course> courses = teacher.getCourses();

        if (theBindingResult.hasErrors()) {
            theModel.addAttribute("teacher", teacher);
            theModel.addAttribute("courses", courses);
            theModel.addAttribute("course", courseService.findCourseById(courseId));
            return "teacher/assignment-form";
        }
        assignment.setDaysRemaining(findDayDifference(assignment));

        assignmentService.save(assignment);

        Course course = courseService.findCourseById(courseId);
        List<Student> students = course.getStudents();

        for(Student student : students) {
            StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(student.getId(), courseId);
            AssignmentDetails assignmentDetail = new AssignmentDetails();
            assignmentDetail.setAssignmentId(assignment.getId());
            assignmentDetail.setStudentCourseDetailsId(studentCourseDetails.getId());
            assignmentDetail.setIsDone(0);
            assignmentDetailsService.save(assignmentDetail);
        }

        if (assignment.isQuiz()) {
            return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/assignment/" + assignment.getId() + "/quiz/create";
        }

        theModel.addAttribute("teacher", teacher);

        return "redirect:/teacher/" + teacherId + "/courses/" + courseId;
    }

    @GetMapping("/{teacherId}/courses/{courseId}/assignments/{assignmentId}")
    public String showAssignmentDetails(@PathVariable("teacherId") int teacherId,
                                        @PathVariable("courseId") int courseId,
                                        @PathVariable("assignmentId") int assignmentId,
                                        Model theModel) {
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        Course course = courseService.findCourseById(courseId);
        List<Student> students = course.getStudents();
        List<Course> courses = teacher.getCourses();

        List<AssignmentDetails> studentCourseAssignmentDetails = new ArrayList<>();

        // Create student - status map
        HashMap<Student, String> list = new HashMap<>();

        for (Student student : students) {
            StudentCourseDetails scd = studentCourseDetailsService.findByStudentAndCourseId(student.getId(), courseId);
            AssignmentDetails ad = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(assignmentId, scd.getId());
            studentCourseAssignmentDetails.add(ad);
            if (ad.getIsDone() == 0) {
                list.put(student, "Chưa hoàn thành");
            } else {
                list.put(student, "Đã hoàn thành");
            }
        }

        theModel.addAttribute("list", list);
        theModel.addAttribute("assignmentDetails", studentCourseAssignmentDetails);
        theModel.addAttribute("students", students);
        theModel.addAttribute("courses", courses);
        theModel.addAttribute("teacher", teacher);

        return "teacher/teacher-assignment-status";
    }

    @GetMapping("/{teacherId}/courses/{courseId}/assignments/{assignmentId}/delete")
    public String deleteAssignment(@PathVariable("teacherId") int teacherId, @PathVariable("courseId") int courseId,
                                   @PathVariable("assignmentId") int assignmentId) {
        assignmentService.deleteAssignmentById(assignmentId);

        return "redirect:/teacher/" + teacherId + "/courses/" + courseId;
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("assignmentId") int theId, Model theModel) {
        Assignment assignment = assignmentService.findById(theId);
        theModel.addAttribute("assignment", assignment);
        return "assignment-form";
    }

    // Display quiz creation form
    @GetMapping("/{teacherId}/course/{courseId}/assignment/{assignmentId}/quiz/create")
    public String showQuizCreationForm(@PathVariable("assignmentId") int assignmentId,
                                       @PathVariable("teacherId") int teacherId,
                                       @PathVariable("courseId") int courseId,
                                       Model model) {
        Assignment assignment = assignmentService.findById(assignmentId);
        if (assignment == null) {
            return "redirect:/teacher/" + teacherId + "/courses/" + courseId;
        }

        Teacher teacher = teacherService.findByTeacherId(teacherId);
        Course course = courseService.findCourseById(courseId);

        QuizFormData quizFormData = new QuizFormData();
        quizFormData.setAssignment(assignment);

        // Load existing questions if any
        List<QuizQuestion> existingQuestions = quizQuestionService.findByAssignmentId(assignmentId);
        if (!existingQuestions.isEmpty()) {
            quizFormData.setQuestions(existingQuestions);
        } else {
            quizFormData.setQuestions(new ArrayList<>());
        }

        model.addAttribute("quizFormData", quizFormData);
        model.addAttribute("teacher", teacher);
        model.addAttribute("course", course);

        return "teacher/quiz-create-form";
    }

    // Add a new question
    @PostMapping("/{teacherId}/course/{courseId}/assignment/{assignmentId}/quiz/addQuestion")
    public String addQuestion(@PathVariable("assignmentId") int assignmentId,
                              @PathVariable("teacherId") int teacherId,
                              @PathVariable("courseId") int courseId,
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

        return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/assignment/" + assignmentId + "/quiz/create";
    }

    // Remove a question
    @GetMapping("/{teacherId}/course/{courseId}/assignment/{assignmentId}/quiz/removeQuestion")
    public String removeQuestion(@PathVariable("assignmentId") int assignmentId,
                                 @PathVariable("teacherId") int teacherId,
                                 @PathVariable("courseId") int courseId,
                                 @RequestParam("questionId") int questionId) {

        quizQuestionService.deleteById(questionId);

        return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/assignment/" + assignmentId + "/quiz/create";
    }

    // Save the quiz
    @PostMapping("/{teacherId}/course/{courseId}/assignment/{assignmentId}/quiz/save")
    public String saveQuiz(@RequestParam("assignmentId") int assignmentId,
                           @PathVariable("teacherId") int teacherId,
                           @PathVariable("courseId") int courseId) {

        Assignment assignment = assignmentService.findById(assignmentId);
        assignment.setQuiz(true);
        assignmentService.save(assignment);

        return "redirect:/teacher/" + teacherId + "/courses/" + courseId;
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