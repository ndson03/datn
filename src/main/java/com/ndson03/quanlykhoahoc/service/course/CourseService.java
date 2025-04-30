package com.ndson03.quanlykhoahoc.service.course;

import java.util.List;

import com.ndson03.quanlykhoahoc.domain.entity.Course;

public interface CourseService {
	
	public void save(Course course);
	
	public List<Course> findAllCourses();
	
	public Course findCourseById(int id);
	
	public void deleteCourseById(int id);
}
