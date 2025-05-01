package com.ndson03.quanlykhoahoc.service.assignment;

import com.ndson03.quanlykhoahoc.domain.entity.AssignmentFileSubmission;
import com.ndson03.quanlykhoahoc.repository.assignment.AssignmentFileSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentFileSubmissionServiceImpl implements AssignmentFileSubmissionService {

    @Autowired
    private AssignmentFileSubmissionRepository fileSubmissionRepository;

    @Override
    public AssignmentFileSubmission save(AssignmentFileSubmission assignmentFileSubmission) {
        return fileSubmissionRepository.save(assignmentFileSubmission);
    }

    @Override
    public AssignmentFileSubmission findById(int id) {
        Optional<AssignmentFileSubmission> result = fileSubmissionRepository.findById(id);
        return result.orElse(null);
    }

    @Override
    public List<AssignmentFileSubmission> findByAssignmentDetailsId(int assignmentDetailsId) {
        return fileSubmissionRepository.findByAssignmentDetails_Id(assignmentDetailsId);
    }

    @Override
    public void deleteById(int id) {
        fileSubmissionRepository.deleteById(id);
    }
}