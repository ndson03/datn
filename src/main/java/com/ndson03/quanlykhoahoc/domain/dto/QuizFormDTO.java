package com.ndson03.quanlykhoahoc.domain.dto;

import com.ndson03.quanlykhoahoc.domain.entity.Assignment;
import com.ndson03.quanlykhoahoc.domain.entity.QuizQuestion;

import java.util.ArrayList;
import java.util.List;

public class QuizFormDTO {
    private Assignment assignment;
    private List<QuizQuestion> questions = new ArrayList<>();

    public QuizFormDTO() {
        // Default constructor
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestion> questions) {
        this.questions = questions;
    }

    // Helper methods
    public void addQuestion(QuizQuestion question) {
        this.questions.add(question);
    }
}