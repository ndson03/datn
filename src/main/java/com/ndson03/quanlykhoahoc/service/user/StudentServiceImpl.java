package com.ndson03.quanlykhoahoc.service.user;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndson03.quanlykhoahoc.repository.user.RoleRepository;
import com.ndson03.quanlykhoahoc.repository.user.StudentRepository;
import com.ndson03.quanlykhoahoc.domain.entity.Role;
import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.domain.dto.UserDTO;

@Service
public class StudentServiceImpl implements StudentService {
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired 
	private RoleRepository roleRepository;
	

	@Override
	@Transactional
	public Student findByStudentName(String studentName) {
		return studentRepository.findByStudentName(studentName);
	}
	
	@Override
	@Transactional
	public Student findByStudentId(int id) {
		return studentRepository.findByStudentId(id);
	}

	@Override
	@Transactional
	public void save(UserDTO userDto) {
		Student student = new Student();
		student.setUserName(userDto.getUserName());
		student.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
		student.setFirstName(userDto.getFirstName());
		student.setLastName(userDto.getLastName());
		student.setEmail(userDto.getEmail());		
		student.setRole(userDto.getRole());	
		
		studentRepository.save(student);
	}
	
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Student student = studentRepository.findByStudentName(username);
		if (student == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		Collection<Role> role = new ArrayList<>();
		role.add(student.getRole());
		return new org.springframework.security.core.userdetails.User(student.getUserName(), student.getPassword(),
				mapRolesToAuthorities(role));
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public List<Student> findAllStudents() {
		return studentRepository.findAllStudents();
	}

	@Override
	@Transactional
	public void save(Student student) {
		studentRepository.save(student);
		
	}

	@Override
	@Transactional
	public void deleteById(int id) {
		studentRepository.deleteById(id);
	}

}
