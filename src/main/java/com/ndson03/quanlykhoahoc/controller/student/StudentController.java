package com.ndson03.quanlykhoahoc.controller.student;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ndson03.quanlykhoahoc.domain.entity.*;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentDetailsService;
import com.ndson03.quanlykhoahoc.service.course.ContentService;
import com.ndson03.quanlykhoahoc.service.course.CourseService;
import com.ndson03.quanlykhoahoc.service.course.LessonService;
import com.ndson03.quanlykhoahoc.service.course.StudentCourseDetailsService;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ContentService contentService;


    @Autowired
    private StudentCourseDetailsService studentCourseDetailsService;

    @Autowired
    private AssignmentDetailsService assignmentDetailsService;

    @GetMapping("/{studentId}/courses")
    public String showStudentPanel(@PathVariable("studentId") int studentId, Model theModel) {
        Student student = studentService.findByStudentId(studentId); //accessing student logged in
        List<Course> courses = student.getCourses();

        theModel.addAttribute("student", student);
        theModel.addAttribute("courses", courses);
        return "student/student-courses";
    }

    @GetMapping("/{studentId}/courses/{courseId}")
    public String showStudentCourse(@PathVariable("studentId") int studentId, @PathVariable("courseId") int courseId, Model theModel) {
        Student student = studentService.findByStudentId(studentId);
        List<Course> courses = student.getCourses();
        Course course = courseService.findCourseById(courseId);
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);
        List<Assignment> assignments = studentCourseDetails.getAssignments();

        for(Assignment assignment : assignments) { //updating days remaining using helper method defined below
            int daysRemaining = findDayDifference(assignment);
            assignment.setDaysRemaining(daysRemaining);
        }


        GradeDetails gradeDetails = studentCourseDetails.getGradeDetails();

        theModel.addAttribute("assignments", assignments);
        theModel.addAttribute("course", course);
        theModel.addAttribute("courses", courses);
        theModel.addAttribute("student", student);
        theModel.addAttribute("gradeDetails", gradeDetails);
        theModel.addAttribute("lessons", lessons);

        return "student/student-course-detail";
    }

    //helper method to find day difference between assignment due date and today
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

    @GetMapping("{studentId}/course/{courseId}/lesson/{lessonId}")
    public String listContentsByLesson(@PathVariable("lessonId") int lessonId, @PathVariable("courseId") int courseId, @PathVariable("studentId") int studentId, Model model) {

        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        List<Lesson> lessons = lessonService.findByCourseId(courseId);

        List<Course> courses = student.getCourses();
        // Lấy thông tin bài học
        Lesson lesson = lessonService.findById(lessonId);
        model.addAttribute("lesson", lesson);
        model.addAttribute("lessons", lessons);
        model.addAttribute("course", course);
        model.addAttribute("student", student);
        model.addAttribute("courses", courses);

        List<Assignment> assignments = lesson.getAssignments();
        model.addAttribute("assignments", assignments);

        // Lấy danh sách nội dung
        List<Content> contents = contentService.findByLessonId(lessonId);
        model.addAttribute("contents", contents);

        return "student/student-lesson-list-content";
    }

    @GetMapping("/{studentId}/course/{courseId}/lesson/{lessonId}/content/{contentId}")
    public String viewContent(@PathVariable int contentId,  @PathVariable("studentId") int studentId, @PathVariable("courseId") int courseId,
                              @PathVariable("lessonId") int lessonId, Model model) {
        Content content = contentService.findById(contentId);
        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        Lesson lesson = lessonService.findById(lessonId);
        List<Course> courses = student.getCourses();
        List<Content> contents = contentService.findByLessonId(lessonId);
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        model.addAttribute("content", content);
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", course);
        model.addAttribute("student", student);
        model.addAttribute("courses", courses);
        model.addAttribute("lessons", lessons);
        model.addAttribute("contents", contents);
        List<Assignment> assignments = lesson.getAssignments();
        model.addAttribute("assignments", assignments);

        return "student/student-view-content";
    }
}