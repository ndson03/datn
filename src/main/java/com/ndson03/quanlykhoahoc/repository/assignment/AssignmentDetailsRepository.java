package com.ndson03.quanlykhoahoc.repository.assignment;

import com.ndson03.quanlykhoahoc.domain.entity.AssignmentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentDetailsRepository extends JpaRepository<AssignmentDetails, Integer> {

    AssignmentDetails findByAssignment_IdAndStudentCourseDetails_Id(int assignmentId, int studentCourseDetailsId);
}