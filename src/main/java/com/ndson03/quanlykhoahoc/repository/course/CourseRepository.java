package com.ndson03.quanlykhoahoc.repository.course;

import java.util.List;

import com.ndson03.quanlykhoahoc.domain.entity.Course;

public interface CourseRepository {
	
	public void saveCourse(Course course);
	
	public List<Course> findAllCourses();
	
	public Course findCourseById(int id);
	
	public void deleteCourseById(int id);
}
