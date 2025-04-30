package com.ndson03.quanlykhoahoc.service;

import com.ndson03.quanlykhoahoc.entity.AssignmentFileSubmission;
import com.ndson03.quanlykhoahoc.repository.AssignmentFileSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentFileSubmissionServiceImpl implements AssignmentFileSubmissionService {

    @Autowired
    private AssignmentFileSubmissionRepository fileSubmissionRepository;

    @Override
    public AssignmentFileSubmission save(AssignmentFileSubmission fileSubmission) {
        return fileSubmissionRepository.save(fileSubmission);
    }

    @Override
    public AssignmentFileSubmission findById(int id) {
        Optional<AssignmentFileSubmission> result = fileSubmissionRepository.findById(id);
        return result.orElse(null);
    }

    @Override
    public List<AssignmentFileSubmission> findByAssignmentDetailsId(int assignmentDetailsId) {
        return fileSubmissionRepository.findByAssignmentDetailsId(assignmentDetailsId);
    }

    @Override
    public void deleteById(int id) {
        fileSubmissionRepository.deleteById(id);
    }
}