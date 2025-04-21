package com.ndson03.quanlykhoahoc.service;

import java.util.List;

import com.ndson03.quanlykhoahoc.entity.Course;

public interface CourseService {
	
	public void save(Course course);
	
	public List<Course> findAllCourses();
	
	public Course findCourseById(int id);
	
	public void deleteCourseById(int id);
}
