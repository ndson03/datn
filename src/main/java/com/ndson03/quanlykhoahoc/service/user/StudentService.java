package com.ndson03.quanlykhoahoc.service.user;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.domain.dto.UserDTO;

public interface StudentService extends UserDetailsService {
	
	public Student findByStudentName(String userName);

	public void save(UserDTO userDto);
	
	public void save(Student student);
	
	public Student findByStudentId(int id);
	
	public List<Student> findAllStudents();
	
	public void deleteById(int id);
}
