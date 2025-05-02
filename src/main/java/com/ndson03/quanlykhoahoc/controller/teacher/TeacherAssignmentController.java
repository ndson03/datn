package com.ndson03.quanlykhoahoc.controller.teacher;

import com.ndson03.quanlykhoahoc.domain.dto.QuizFormDTO;
import com.ndson03.quanlykhoahoc.domain.dto.StudentAssignmentResultDTO;
import com.ndson03.quanlykhoahoc.domain.entity.*;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentDetailsService;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentFileSubmissionService;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentService;
import com.ndson03.quanlykhoahoc.service.course.ContentService;
import com.ndson03.quanlykhoahoc.service.course.CourseService;
import com.ndson03.quanlykhoahoc.service.course.LessonService;
import com.ndson03.quanlykhoahoc.service.quiz.QuizOptionService;
import com.ndson03.quanlykhoahoc.service.quiz.QuizQuestionService;
import com.ndson03.quanlykhoahoc.service.quiz.QuizSubmissionService;
import com.ndson03.quanlykhoahoc.service.course.StudentCourseDetailsService;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
import com.ndson03.quanlykhoahoc.service.user.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private AssignmentFileSubmissionService assignmentFileSubmissionService;

    @Autowired
    private StudentCourseDetailsService studentCourseDetailsService;

    @Autowired
    private AssignmentDetailsService assignmentDetailsService;

    @Autowired
    private ContentService contentService;

    @Value("${file.upload.directory}")
    private String uploadDirectory;

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/addNewAssignment")
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
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        theModel.addAttribute("lessons", lessons);
        List<Assignment> assignments = lesson.getAssignments();
        theModel.addAttribute("assignments", assignments);
        List<Content> contents = contentService.findByLessonId(lessonId);
        theModel.addAttribute("contents", contents);

        return "teacher/assignment-form";
    }

    @PostMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/save")
    public String saveAssignment(@Valid @ModelAttribute("assignment") Assignment assignment,
                                 BindingResult theBindingResult,
                                 @RequestParam(value = "assignmentFile", required = false) MultipartFile assignmentFile,
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

        // Handle file upload if it's a regular assignment and file is provided
        if (!assignment.isQuiz() && assignmentFile != null && !assignmentFile.isEmpty()) {
            try {
                // Create directory structure if it doesn't exist
                String uploadDir = "uploads/assignment_file" + "/assignment_" + assignment.getId();
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generate a unique filename to avoid conflicts
                String originalFilename = StringUtils.cleanPath(assignmentFile.getOriginalFilename());
                String fileExtension = "";
                if (originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                // Save the file
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(assignmentFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Set file information in the assignment
                assignment.setFilePath(uploadDir + "/" + uniqueFilename);
                assignment.setFileName(originalFilename);

            } catch (IOException e) {
                // Handle file upload exception
                e.printStackTrace();
            }
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

        return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId;
    }

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/quiz/result/student/{studentId}")
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
            return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId;
        }

        QuizSubmission submission = quizSubmissionService.findByAssignmentAndStudentCourseDetailsId(
                assignmentId, studentCourseDetails.getId());

        if (submission == null) {
            return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId + "/assignment/" + assignmentId;
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
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        model.addAttribute("lessons", lessons);
        List<Assignment> assignments = lesson.getAssignments();
        model.addAttribute("assignments", assignments);
        List<Content> contents = contentService.findByLessonId(lessonId);
        model.addAttribute("contents", contents);

        return "teacher/student-quiz-result";
    }

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/file/result/student/{studentId}")
    public String viewFileSubmission(@PathVariable("teacherId") int teacherId,
                                     @PathVariable("courseId") int courseId,
                                     @PathVariable("lessonId") int lessonId,
                                     @PathVariable("assignmentId") int assignmentId,
                                     @PathVariable("studentId") int studentId,
                                     Model model) {
        // Load common data
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        Student student = studentService.findByStudentId(studentId);
        Course course = courseService.findCourseById(courseId);
        Lesson lesson = lessonService.findById(lessonId);
        Assignment assignment = assignmentService.findById(assignmentId);
        List<Course> courses = teacher.getCourses();

        // Get student course details
        StudentCourseDetails studentCourseDetails =
                studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);

        if (studentCourseDetails == null || assignment == null) {
            return "redirect:/teacher/" + teacherId + "/courses/" + courseId;
        }

        // Get assignment details for this student
        AssignmentDetails assignmentDetails =
                assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(
                        assignmentId, studentCourseDetails.getId());

        if (assignmentDetails == null) {
            return "redirect:/teacher/" + teacherId + "/courses/" + courseId + "/assignments/" + assignmentId + "/submissions";
        }

        // Get file submissions
        List<AssignmentFileSubmission> assignmentFileSubmissions =
                assignmentFileSubmissionService.findByAssignmentDetailsId(assignmentDetails.getId());

        // Add attributes to model
        model.addAttribute("teacher", teacher);
        model.addAttribute("student", student);
        model.addAttribute("course", course);
        model.addAttribute("courses", courses);
        model.addAttribute("lesson", lesson);
        model.addAttribute("assignment", assignment);
        model.addAttribute("assignmentDetails", assignmentDetails);
        model.addAttribute("assignmentFileSubmissions", assignmentFileSubmissions);
        model.addAttribute("courseId", courseId);
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        model.addAttribute("lessons", lessons);
        List<Assignment> assignments = lesson.getAssignments();
        model.addAttribute("assignments", assignments);
        List<Content> contents = contentService.findByLessonId(lessonId);
        model.addAttribute("contents", contents);

        return "teacher/student-file-submission";
    }

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/delete")
    public String deleteAssignment(@PathVariable("teacherId") int teacherId,
                                   @PathVariable("courseId") int courseId,
                                   @PathVariable("lessonId") int lessonId,
                                   @PathVariable("assignmentId") int assignmentId) {
        assignmentService.deleteAssignmentById(assignmentId);

        return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId;
    }

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/student/{studentId}/reset")
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

        return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId + "/assignment/" + assignmentId + "/quiz";
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
            return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId;
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

        model.addAttribute("quizFormDTO", quizFormDTO);
        model.addAttribute("teacher", teacher);
        model.addAttribute("course", course);
        model.addAttribute("lesson", lesson);
        List<Course> courses = teacher.getCourses();
        model.addAttribute("courses", courses);
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        model.addAttribute("lessons", lessons);
        List<Assignment> assignments = lesson.getAssignments();
        model.addAttribute("assignments", assignments);
        List<Content> contents = contentService.findByLessonId(lessonId);
        model.addAttribute("contents", contents);

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

        return "redirect:/teacher/" + teacherId + "/course/" + courseId + "/lesson/" + lessonId;
    }

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}")
    public String viewAssignment(@PathVariable("teacherId") int studentId,
                                 @PathVariable("courseId") int courseId,
                                 @PathVariable("lessonId") int lessonId,
                                 @PathVariable("assignmentId") int assignmentId) {

        Assignment assignment = assignmentService.findById(assignmentId);

        if (assignment.isQuiz()) {
            return "redirect:/teacher/" + studentId + "/course/" + courseId +
                    "/lesson/" + lessonId + "/assignment/" + assignmentId + "/quiz";
        } else {
            return "redirect:/teacher/" + studentId + "/course/" + courseId +
                    "/lesson/" + lessonId + "/assignment/" + assignmentId + "/file";
        }
    }

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/quiz")
    public String showQuizAssignmentDetails(@PathVariable("teacherId") int teacherId,
                                        @PathVariable("courseId") int courseId,
                                        @PathVariable("lessonId") int lessonId,
                                        @PathVariable("assignmentId") int assignmentId,
                                        Model theModel) {
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        Course course = courseService.findCourseById(courseId);
        List<Student> students = course.getStudents();
        List<Course> courses = teacher.getCourses();
        Assignment assignment = assignmentService.findById(assignmentId);

        // Create student - status map
        Map<StudentAssignmentResultDTO, String> list = new HashMap<>();

        for (Student student : students) {
            StudentCourseDetails scd = studentCourseDetailsService.findByStudentAndCourseId(student.getId(), courseId);
            AssignmentDetails ad = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(assignmentId, scd.getId());
            QuizSubmission quizSubmission = quizSubmissionService.findByAssignmentAndStudentCourseDetailsId(assignmentId, scd.getId());
            // Create a StudentDTO to store additional display info
            StudentAssignmentResultDTO studentDTO = new StudentAssignmentResultDTO();
            studentDTO.setId(student.getId());
            studentDTO.setFirstName(student.getFirstName());
            studentDTO.setLastName(student.getLastName());
            studentDTO.setEmail(student.getEmail());

            // Add submission details if available
            if (ad.getIsDone() == 1) {
                studentDTO.setScore(ad.getScore());
                studentDTO.setSubmissionTime(formatTime(quizSubmission.getSubmissionDate()));
                studentDTO.setSubmissionDate(formatDate(quizSubmission.getSubmissionDate()));
            }

            // Set status
            if (ad.getIsDone() == 0) {
                list.put(studentDTO, "Chưa hoàn thành");
            } else {
                list.put(studentDTO, "Đã hoàn thành");
            }
        }

        // If this is a quiz assignment, load quiz questions and answers
        if (assignment.isQuiz()) {
            List<QuizQuestion> questions = quizQuestionService.findByAssignmentId(assignmentId);

            // Load options for each question
            for (QuizQuestion question : questions) {
                List<QuizOption> options = quizOptionService.findByQuestionId(question.getId());
                question.setOptions(options);
            }

            theModel.addAttribute("questions", questions);
        }

        Lesson lesson = lessonService.findById(lessonId);
        theModel.addAttribute("lesson", lesson);

        theModel.addAttribute("list", list);
        theModel.addAttribute("course", course);
        theModel.addAttribute("assignmentId", assignmentId);
        theModel.addAttribute("courses", courses);
        theModel.addAttribute("teacher", teacher);
        theModel.addAttribute("assignment", assignment);
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        theModel.addAttribute("lessons", lessons);
        List<Assignment> assignments = lesson.getAssignments();
        theModel.addAttribute("assignments", assignments);
        List<Content> contents = contentService.findByLessonId(lessonId);
        theModel.addAttribute("contents", contents);

        return "teacher/quiz-assignment-status";
    }

    @GetMapping("/{teacherId}/course/{courseId}/lesson/{lessonId}/assignment/{assignmentId}/file")
    public String showFileAssignmentDetails(@PathVariable("teacherId") int teacherId,
                                            @PathVariable("courseId") int courseId,
                                            @PathVariable("lessonId") int lessonId,
                                            @PathVariable("assignmentId") int assignmentId,
                                            Model theModel) {
        Teacher teacher = teacherService.findByTeacherId(teacherId);
        Course course = courseService.findCourseById(courseId);
        List<Student> students = course.getStudents();
        List<Course> courses = teacher.getCourses();
        Assignment assignment = assignmentService.findById(assignmentId);

        // Create student - status map
        Map<StudentAssignmentResultDTO, String> list = new HashMap<>();

        for (Student student : students) {
            StudentCourseDetails scd = studentCourseDetailsService.findByStudentAndCourseId(student.getId(), courseId);
            AssignmentDetails ad = assignmentDetailsService.findByAssignmentAndStudentCourseDetailsId(assignmentId, scd.getId());
            // Create a StudentDTO to store additional display info
            StudentAssignmentResultDTO studentDTO = new StudentAssignmentResultDTO();
            studentDTO.setId(student.getId());
            studentDTO.setFirstName(student.getFirstName());
            studentDTO.setLastName(student.getLastName());
            studentDTO.setEmail(student.getEmail());

            // Add submission details if available
            if (ad.getIsDone() == 1) {
                studentDTO.setScore(ad.getScore());
                studentDTO.setSubmissionTime(formatTime(ad.getSubmitTime()));
                studentDTO.setSubmissionDate(formatDate(ad.getSubmitTime()));
            }

            // Set status
            if (ad.getIsDone() == 0) {
                list.put(studentDTO, "Chưa hoàn thành");
            } else {
                list.put(studentDTO, "Đã hoàn thành");
            }
        }

        Lesson lesson = lessonService.findById(lessonId);
        theModel.addAttribute("lesson", lesson);

        theModel.addAttribute("list", list);
        theModel.addAttribute("course", course);
        theModel.addAttribute("assignmentId", assignmentId);
        theModel.addAttribute("courses", courses);
        theModel.addAttribute("teacher", teacher);
        theModel.addAttribute("assignment", assignment);
        List<Lesson> lessons = lessonService.findByCourseId(courseId);
        theModel.addAttribute("lessons", lessons);
        List<Assignment> assignments = lesson.getAssignments();
        theModel.addAttribute("assignments", assignments);
        List<Content> contents = contentService.findByLessonId(lessonId);
        theModel.addAttribute("contents", contents);

        return "teacher/file-assignment-status";
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

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    private String formatTime(Date date) {
        if (date == null) {
            return "";
        }
        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss");
        return timeFormat.format(date);
    }

    private String formatTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private String formatDate(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}