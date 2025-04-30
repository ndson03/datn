package com.ndson03.quanlykhoahoc.repository.user;

import java.util.List;

import com.ndson03.quanlykhoahoc.domain.entity.Student;

public interface StudentRepository {
	
	public Student findByStudentName(String theStudentName);
	
	public void save(Student student);
	
	
	public Student findByStudentId(int id);
	
	public List<Student> findAllStudents();
	
	public void deleteById(int id);
}
