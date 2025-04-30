package com.ndson03.quanlykhoahoc.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "quiz_answer")
public class QuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private QuizSubmission quizSubmission;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuizQuestion question;

    @Column(name = "selected_option_id")
    private int selectedOptionId;

    public QuizAnswer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QuizSubmission getQuizSubmission() {
        return quizSubmission;
    }

    public void setQuizSubmission(QuizSubmission quizSubmission) {
        this.quizSubmission = quizSubmission;
    }

    public QuizQuestion getQuestion() {
        return question;
    }

    public void setQuestion(QuizQuestion question) {
        this.question = question;
    }

    public int getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(int selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}