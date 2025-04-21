package com.ndson03.quanlykhoahoc.dao;

import java.util.List;

import com.ndson03.quanlykhoahoc.entity.Course;

public interface CourseDao {
	
	public void saveCourse(Course course);
	
	public List<Course> findAllCourses();
	
	public Course findCourseById(int id);
	
	public void deleteCourseById(int id);
}
