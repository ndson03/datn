package com.ndson03.quanlykhoahoc.dao;

import com.ndson03.quanlykhoahoc.entity.GradeDetails;

public interface GradeDetailsDao {
	
	public void save(GradeDetails gradeDetails);
	
	public GradeDetails findById(int id);
	
	public void deleteById(int id);
}
