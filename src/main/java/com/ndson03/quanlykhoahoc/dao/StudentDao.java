package com.ndson03.quanlykhoahoc.dao;

import java.util.List;

import com.ndson03.quanlykhoahoc.entity.Student;

public interface StudentDao {
	
	public Student findByStudentName(String theStudentName);
	
	public void save(Student student);
	
	
	public Student findByStudentId(int id);
	
	public List<Student> findAllStudents();
	
	public void deleteById(int id);
}
