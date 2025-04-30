package com.ndson03.quanlykhoahoc.domain.dto;

import java.util.ArrayList;
import java.util.List;

public class QuizSubmissionFormDTO {
    private int assignmentId;
    private int studentCourseDetailsId;
    private List<Integer> selectedOptions = new ArrayList<>();

    public QuizSubmissionFormDTO() {
        // Default constructor 
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getStudentCourseDetailsId() {
        return studentCourseDetailsId;
    }

    public void setStudentCourseDetailsId(int studentCourseDetailsId) {
        this.studentCourseDetailsId = studentCourseDetailsId;
    }

    public List<Integer> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<Integer> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }
}