package com.ndson03.quanlykhoahoc.service.assignment;

import com.ndson03.quanlykhoahoc.repository.assignment.AssignmentDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndson03.quanlykhoahoc.domain.entity.AssignmentDetails;

import java.util.Optional;

@Service
public class AssignmentDetailsServiceImpl implements AssignmentDetailsService {

    @Autowired
    private AssignmentDetailsRepository assignmentDetailsRepository;

    @Override
    @Transactional
    public AssignmentDetails findByAssignmentAndStudentCourseDetailsId(int assignmentId, int studentCourseDetailsId) {
        return assignmentDetailsRepository.findByAssignment_IdAndStudentCourseDetails_Id(assignmentId, studentCourseDetailsId);
    }

    @Override
    @Transactional
    public AssignmentDetails save(AssignmentDetails assignmentDetails) {
        return assignmentDetailsRepository.save(assignmentDetails);
    }

    @Override
    public Optional<AssignmentDetails> findById(int assignmentDetailsId) {
        return assignmentDetailsRepository.findById(assignmentDetailsId);
    }

}