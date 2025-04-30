package com.ndson03.quanlykhoahoc.repository.assignment;

import com.ndson03.quanlykhoahoc.domain.entity.AssignmentFileSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentFileSubmissionRepository extends JpaRepository<AssignmentFileSubmission, Integer> {

    List<AssignmentFileSubmission> findByAssignmentDetails_Id(int assignmentDetailsId);

}