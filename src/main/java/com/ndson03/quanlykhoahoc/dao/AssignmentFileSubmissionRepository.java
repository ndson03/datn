package com.ndson03.quanlykhoahoc.dao;

import com.ndson03.quanlykhoahoc.entity.AssignmentFileSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentFileSubmissionRepository extends JpaRepository<AssignmentFileSubmission, Integer> {

    List<AssignmentFileSubmission> findByAssignmentDetailsId(int assignmentDetailsId);
}