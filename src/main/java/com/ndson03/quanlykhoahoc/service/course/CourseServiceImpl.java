package com.ndson03.quanlykhoahoc.service.course;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndson03.quanlykhoahoc.repository.course.CourseRepository;
import com.ndson03.quanlykhoahoc.domain.entity.Course;

@Service
public class CourseServiceImpl implements CourseService {
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Override
	@Transactional
	public void save(Course course) {
		courseRepository.saveCourse(course);
	}

	@Override
	@Transactional
	public List<Course> findAllCourses() {
		return courseRepository.findAllCourses();
	}

	@Override
	@Transactional
	public Course findCourseById(int id) {
		return courseRepository.findCourseById(id);
	}

	@Override
	@Transactional
	public void deleteCourseById(int id) {
		courseRepository.deleteCourseById(id);
	}

}
