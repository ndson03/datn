package com.ndson03.quanlykhoahoc.repository.user;

import java.util.List;

import com.ndson03.quanlykhoahoc.domain.entity.Teacher;

public interface TeacherRepository {
	
	public Teacher findByTeacherName(String theTeacherName);
	
	public Teacher findByTeacherId(int id);
	
	public void save(Teacher teacher);
	
	public List<Teacher> findAllTeachers();
	
	public void deleteTeacherById(int id);
	
}
