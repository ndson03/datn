package com.ndson03.quanlykhoahoc.service;

import com.ndson03.quanlykhoahoc.dao.AssignmentDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndson03.quanlykhoahoc.entity.AssignmentDetails;

@Service
public class AssignmentDetailsServiceImpl implements AssignmentDetailsService {

    @Autowired
    private AssignmentDetailsRepository assignmentDetailsRepository;

    @Override
    @Transactional
    public AssignmentDetails findByAssignmentAndStudentCourseDetailsId(int assignmentId, int studentCourseDetailsId) {
        return assignmentDetailsRepository.findByAssignmentIdAndStudentCourseDetailsId(assignmentId, studentCourseDetailsId);
    }

    @Override
    @Transactional
    public void save(AssignmentDetails assignmentDetails) {
        assignmentDetailsRepository.save(assignmentDetails);
    }

}