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
import com.ndson03.quanlykhoahoc.repository.user.TeacherRepository;
import com.ndson03.quanlykhoahoc.domain.entity.Role;
import com.ndson03.quanlykhoahoc.domain.entity.Teacher;
import com.ndson03.quanlykhoahoc.domain.dto.UserDTO;

@Service
public class TeacherServiceImpl implements TeacherService {
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired 
	private RoleRepository roleRepository;
	
	
	@Override
	@Transactional
	public Teacher findByTeacherName(String teacherName) {
		return teacherRepository.findByTeacherName(teacherName);
	}

	@Override
	@Transactional
	public void save(UserDTO userDto) {
		Teacher teacher = new Teacher();
		teacher.setUserName(userDto.getUserName());
		teacher.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
		teacher.setFirstName(userDto.getFirstName());
		teacher.setLastName(userDto.getLastName());
		teacher.setEmail(userDto.getEmail());		
		teacher.setRole(userDto.getRole());	
		
		teacherRepository.save(teacher);
	}
	
	@Override
	@Transactional
	public void save(Teacher teacher) {
		teacherRepository.save(teacher);
	}
	
	
	@Override
	@Transactional
	public List<Teacher> findAllTeachers() {
		return teacherRepository.findAllTeachers();
	}
	
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Teacher teacher = teacherRepository.findByTeacherName(username);
		if (teacher == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		Collection<Role> role = new ArrayList<>();
		role.add(teacher.getRole());
		return new org.springframework.security.core.userdetails.User(teacher.getUserName(), teacher.getPassword(),
				mapRolesToAuthorities(role));
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Teacher findByTeacherId(int id) {
		return teacherRepository.findByTeacherId(id);
	}

	@Override
	@Transactional
	public void deleteTeacherById(int id) {
		teacherRepository.deleteTeacherById(id);
	}

	

	

}
