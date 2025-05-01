package com.ndson03.quanlykhoahoc.service.assignment;

import com.ndson03.quanlykhoahoc.domain.entity.AssignmentFileSubmission;

import java.util.List;

public interface AssignmentFileSubmissionService {

    AssignmentFileSubmission save(AssignmentFileSubmission assignmentFileSubmission);

    AssignmentFileSubmission findById(int id);

    List<AssignmentFileSubmission> findByAssignmentDetailsId(int assignmentDetailsId);

    void deleteById(int id);
}