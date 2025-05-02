package com.ndson03.quanlykhoahoc.controller.admin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.ndson03.quanlykhoahoc.domain.entity.Assignment;
import com.ndson03.quanlykhoahoc.domain.entity.Course;
import com.ndson03.quanlykhoahoc.domain.entity.GradeDetails;
import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.domain.entity.StudentCourseDetails;
import com.ndson03.quanlykhoahoc.domain.entity.Teacher;
import com.ndson03.quanlykhoahoc.service.course.CourseService;
import com.ndson03.quanlykhoahoc.service.course.GradeDetailsService;
import com.ndson03.quanlykhoahoc.service.course.StudentCourseDetailsService;
import com.ndson03.quanlykhoahoc.service.user.StudentService;
import com.ndson03.quanlykhoahoc.service.user.TeacherService;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private TeacherService teacherService;
	
	@Autowired
	private StudentService studentService;
	
	
	@Autowired
	private StudentCourseDetailsService studentCourseDetailsService;
	
	@Autowired
	private GradeDetailsService gradeDetailsService;

	@Value("${file.upload.directory}")
	private String uploadDir;
	
	private int teacherDeleteErrorValue; //used for deleting teacher, 0 means the teacher has not any assigned courses, 1 means it has
	
	@GetMapping("/adminPanel")
	public String showAdminPanel(Model theModel) {
		int courseSize = courseService.findAllCourses().size();
		theModel.addAttribute("courseSize", courseSize);
		int studentSize = studentService.findAllStudents().size();
		theModel.addAttribute("studentSize", studentSize);
		int teacherSize = teacherService.findAllTeachers().size();
		theModel.addAttribute("teacherSize", teacherSize);
		return "admin/admin-panel";
	}
	
	@GetMapping("/adminInfo")
	public String showAdminInfo(Model theModel) {
		int courseSize = courseService.findAllCourses().size();
		theModel.addAttribute("courseSize", courseSize);
		int studentSize = studentService.findAllStudents().size();
		theModel.addAttribute("studentSize", studentSize);
		int teacherSize = teacherService.findAllTeachers().size();
		theModel.addAttribute("teacherSize", teacherSize);
		return "admin/admin-info";
	}
	
	@GetMapping("/students")
	public String showStudentList(Model theModel) {
		theModel.addAttribute("students", studentService.findAllStudents());
		
		return "admin/student-list"; 
	}
	
	@RequestMapping("/students/delete")
	public String deleteStudent(@RequestParam("studentId") int studentId) {
		List<StudentCourseDetails> list = studentCourseDetailsService.findByStudentId(studentId);
		for(StudentCourseDetails scd : list) { //deleting the student grades before deleting the student 
			int gradeId = scd.getGradeDetails().getId();
			studentCourseDetailsService.deleteByStudentId(studentId);
			gradeDetailsService.deleteById(gradeId);
		}
		studentService.deleteById(studentId);
		
		return "redirect:/admin/students";
	}

	@GetMapping("/students/{studentId}/courses")
	public String editCoursesForStudent(@PathVariable("studentId") int studentId, Model theModel) {
		Student student = studentService.findByStudentId(studentId);
		List<Course> enrolledCourses = student.getCourses();

		// Get available courses (courses that the student hasn't enrolled in yet)
		List<Course> allCourses = courseService.findAllCourses();
		List<Course> availableCourses = new ArrayList<>(allCourses);
		availableCourses.removeAll(enrolledCourses);

		theModel.addAttribute("student", student);
		theModel.addAttribute("courses", enrolledCourses);
		theModel.addAttribute("availableCourses", availableCourses);

		return "admin/student-course-list";
	}

	@GetMapping("/teacher/{teacherId}/courses")
	public String listCoursesForTeacher(@PathVariable("teacherId") int teacherId, Model theModel) {
		Teacher teacher = teacherService.findByTeacherId(teacherId);
		List<Course> courses = teacher.getCourses();

		theModel.addAttribute("teacher", teacher);
		theModel.addAttribute("courses", courses);

		return "admin/teacher-course-list";
	}

	@RequestMapping("/students/{studentId}/addCourse/save")
	public String saveCourseToStudent(@PathVariable("studentId") int studentId, @RequestParam("courseId") int courseId) {
		
		StudentCourseDetails sc = new StudentCourseDetails(studentId, courseId, new ArrayList<Assignment>() ,new GradeDetails());
		studentCourseDetailsService.save(sc);
			
		return "redirect:/admin/students/" + studentId + "/courses";
	}
	
	
	@GetMapping("/students/{studentId}/courses/delete/{courseId}")
	public String deleteCourseFromStudent(@PathVariable("studentId") int studentId, @PathVariable("courseId") int courseId) {
		StudentCourseDetails scd = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);
		int gradeId = scd.getGradeDetails().getId();
		
		//operations for removing the student from the course
		studentCourseDetailsService.deleteByStudentAndCourseId(studentId, courseId);
		gradeDetailsService.deleteById(gradeId);
		
		return "redirect:/admin/students/" + studentId + "/courses";
	}
	
	
	@GetMapping("/teachers")
	public String showTeacherList(Model theModel) {
		theModel.addAttribute("teachers", teacherService.findAllTeachers());
		theModel.addAttribute("error", teacherDeleteErrorValue); 
		teacherDeleteErrorValue = 0; //0 means the teacher has not any assigned courses, 1 means it has
		return "admin/teacher-list";
	}
	
	@GetMapping("/teachers/delete")
	public String deleteTeacher(@RequestParam("teacherId") int teacherId) {
		Teacher teacher = teacherService.findByTeacherId(teacherId);
		if(teacher.getCourses().size() == 0) { //if the teacher has courses assigned, the teacher cannot be deleted
			teacherService.deleteTeacherById(teacherId);
			teacherDeleteErrorValue = 0;
		} else {
			teacherDeleteErrorValue = 1; 
		}
		
		return "redirect:/admin/teachers";
	}
	
	
	@GetMapping("/addCourse")
	public String addCourse(Model theModel) {
		//add course form has a select teacher field where all teachers registered are showed as drop-down list
		List<Teacher> teachers = teacherService.findAllTeachers(); 
		
		theModel.addAttribute("course", new Course());
		theModel.addAttribute("teachers", teachers);
		
		return "admin/course-form";
	}

	@PostMapping("/saveCourse")
	public String saveCourse(
			@Valid @ModelAttribute("course") Course theCourse,
			BindingResult theBindingResult,
			@RequestParam("teacherId") int teacherId,
			@RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
			Model theModel) {

		if (theBindingResult.hasErrors()) {
			// course form has data validation rules. If fields are not properly filled out, form is showed again
			List<Teacher> teachers = teacherService.findAllTeachers();
			theModel.addAttribute("teachers", teachers);
			return "admin/course-form";
		}

		// Lấy giảng viên từ teacherId và gán vào khóa học
		theCourse.setTeacher(teacherService.findByTeacherId(teacherId)); // setTeacher method also sets the teacher's course as this

		// Xử lý tải lên hình ảnh nếu có
		if (imageFile != null && !imageFile.isEmpty()) {
			try {
				// Tạo thư mục upload nếu chưa tồn tại
				File directory = new File(uploadDir);
				if (!directory.exists()) {
					directory.mkdirs();
				}

				// Tạo tên file duy nhất để tránh trùng lặp
				String originalFilename = imageFile.getOriginalFilename();
				String fileExtension = "";
				if (originalFilename != null && originalFilename.contains(".")) {
					fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
				}
				String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

				// Đường dẫn đầy đủ đến file
				Path targetLocation = Paths.get(uploadDir + "/course_image/").resolve(uniqueFilename);

				// Lưu file
				Files.copy(imageFile.getInputStream(), targetLocation);

				// Lưu đường dẫn vào entity
				theCourse.setImagePath("/uploads/course_image/" + uniqueFilename);

			} catch (IOException ex) {
				// Xử lý ngoại lệ khi tải file lên
				System.err.println("Error uploading file: " + ex.getMessage());
			}
		}

		// Lưu khóa học
		courseService.save(theCourse);

		return "redirect:/admin/adminPanel";
	}
	
	@GetMapping("/courses")
	public String showCourses(Model theModel) {
		theModel.addAttribute("courses", courseService.findAllCourses());	
		
		return "admin/course-list";
	}


	@GetMapping("/courses/delete")
	public String deleteCourse(@RequestParam("courseId") int courseId) {
		Course course = courseService.findCourseById(courseId);
		List<Student> students = course.getStudents();

		// Xóa các liên kết sinh viên và điểm trước khi xóa khóa học
		for(Student student : students) {
			StudentCourseDetails scd = studentCourseDetailsService.findByStudentAndCourseId(student.getId(), courseId);
			int gradeId = scd.getGradeDetails().getId();
			studentCourseDetailsService.deleteByStudentAndCourseId(student.getId(), courseId);
			gradeDetailsService.deleteById(gradeId);
		}

		// Xóa file ảnh nếu tồn tại
		if (course.getImagePath() != null && !course.getImagePath().isEmpty()) {
			try {
				String filename = course.getImagePath().substring(course.getImagePath().lastIndexOf("/") + 1);
				Path filePath = Paths.get(uploadDir).resolve(filename);
				Files.deleteIfExists(filePath);
			} catch (IOException ex) {
				System.err.println("Error deleting image file: " + ex.getMessage());
			}
		}

		// Xóa khóa học
		courseService.deleteCourseById(courseId);

		return "redirect:/admin/courses";
	}

	@GetMapping("/courses/{courseId}/students")
	public String showStudentsInCourse(@PathVariable("courseId") int courseId, Model theModel) {
		// Get course, students, and teacher info
		Course course = courseService.findCourseById(courseId);
		List<Student> students = course.getStudents();
		Teacher teacher = course.getTeacher();

		// Get all students not already in this course for the add student form
		List<Student> allStudents = studentService.findAllStudents();
		List<Student> availableStudents = new ArrayList<>(allStudents);

		// Remove students who are already enrolled in the course
		availableStudents.removeAll(students);

		// Add attributes to the model
		theModel.addAttribute("students", students);
		theModel.addAttribute("course", course);
		theModel.addAttribute("teacher", teacher);
		theModel.addAttribute("availableStudents", availableStudents);

		// Return the view that includes both the student list and add student form
		return "admin/course-student-list";
	}

	@GetMapping("/courses/{courseId}/students/addStudent/save")
	public String saveStudentToCourse(@RequestParam("studentId") int studentId,
									  @PathVariable("courseId") int courseId) {
		// Create and save the student-course relationship
		StudentCourseDetails sc = new StudentCourseDetails(
				studentId,
				courseId,
				new ArrayList<Assignment>(),
				new GradeDetails()
		);
		studentCourseDetailsService.save(sc);

		// Redirect back to the course students page
		return "redirect:/admin/courses/" + courseId + "/students";
	}
	
	
	
	@GetMapping("/courses/{courseId}/students/delete")
	public String deleteStudentFromCourse(@PathVariable("courseId") int courseId, @RequestParam("studentId") int studentId) {
		StudentCourseDetails scd = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);
		int gradeId = scd.getGradeDetails().getId();
		
		studentCourseDetailsService.deleteByStudentAndCourseId(studentId, courseId);
		gradeDetailsService.deleteById(gradeId);
		
		return "redirect:/admin/courses/" + courseId + "/students";
	}
}
