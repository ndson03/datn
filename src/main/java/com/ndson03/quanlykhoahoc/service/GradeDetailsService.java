package com.ndson03.quanlykhoahoc.service;

import com.ndson03.quanlykhoahoc.entity.GradeDetails;

public interface GradeDetailsService {
	
	public void save(GradeDetails gradeDetails);
	
	public GradeDetails findById(int id);
	
	public void deleteById(int id);
}
