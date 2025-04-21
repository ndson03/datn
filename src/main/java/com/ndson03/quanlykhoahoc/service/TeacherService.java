package com.ndson03.quanlykhoahoc.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.ndson03.quanlykhoahoc.entity.Teacher;
import com.ndson03.quanlykhoahoc.user.UserDto;

public interface TeacherService extends UserDetailsService {
	
	public Teacher findByTeacherName(String userName);
	
	public Teacher findByTeacherId(int id);

	public void save(UserDto userDto);
	
	public void save(Teacher teacher);
	
	public List<Teacher> findAllTeachers();
	
	public void deleteTeacherById(int id);
}
