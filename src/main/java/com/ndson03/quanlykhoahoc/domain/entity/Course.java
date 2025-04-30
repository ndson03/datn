package com.ndson03.quanlykhoahoc.domain.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "course")
public class Course {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@NotBlank(message = "is required")
	@Size(min = 1, message = "is required")
	@Column(name="code")
	private String code;
	
	@NotBlank(message = "is required")
	@Size(min = 1, message = "is required")
	@Column(name="name")
	private String name;
	
	@ManyToOne(cascade= {CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	@JoinColumn(name = "teacher_id")
	private Teacher teacher;
	
	@ManyToMany(cascade= {CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	@JoinTable(name="student_course_details",
				joinColumns = @JoinColumn(name="course_id"),
				inverseJoinColumns = @JoinColumn(name="student_id"))			
	private List<Student> students;


	@OneToMany(cascade = CascadeType.ALL,
			mappedBy = "course",
			fetch = FetchType.LAZY)
	private List<Lesson> lessons;

	public List<Lesson> getLessons() {
		return lessons;
	}

	public void setLessons(List<Lesson> lessons) {
		this.lessons = lessons;
	}

	// Thêm bài học vào khóa học
	public void addLesson(Lesson lesson) {
		if (lessons == null) {
			lessons = new ArrayList<>();
		}
		lessons.add(lesson);
		lesson.setCourse(this);
	}

	// Xóa bài học khỏi khóa học
	public void removeLesson(Lesson lesson) {
		if (lessons != null && lessons.contains(lesson)) {
			lessons.remove(lesson);
			lesson.setCourse(null);
		}
	}

	public Course() {
		
	}


	public Course(int id, String code, String name, Teacher teacher, List<Student> students) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.teacher = teacher;
		this.students = students;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	
	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public Teacher getTeacher() {
		return teacher;
	}



	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
		teacher.addCourse(this);
	}



	public List<Student> getStudents() {
		return students;
	}


	public void setStudents(List<Student> students) {
		this.students = students;
	}
	
	public int studentListSize() {
		return students.size();
	}
	
	public void addStudent(Student student) {
		if(students == null) {
			students = new ArrayList<>();
		}
		students.add(student);
	}
	
	public void removeStudent(Student student) {
		if(students.contains(student)) {
			students.remove(student);
		}
	}
	
	public boolean equals(Object comparedObject) {
	    if (this == comparedObject) {
	        return true;
	    }

	   if (!(comparedObject instanceof Course)) {
	        return false;
	    }

	    Course comparedCourse = (Course) comparedObject;

	    if (this.id == comparedCourse.id) {
	        return true;
	    }

	    return false;
	}
	
	
}