package com.ndson03.quanlykhoahoc.domain.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "quiz_submission")
public class QuizSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "student_course_details_id")
    private StudentCourseDetails studentCourseDetails;

    @Column(name = "submission_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date submissionDate;

    @Column(name = "score")
    private double score;

    @OneToMany(mappedBy = "quizSubmission", cascade = CascadeType.ALL)
    private List<QuizAnswer> answers;


    public QuizSubmission() {
        this.submissionDate = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public StudentCourseDetails getStudentCourseDetails() {
        return studentCourseDetails;
    }

    public void setStudentCourseDetails(StudentCourseDetails studentCourseDetails) {
        this.studentCourseDetails = studentCourseDetails;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<QuizAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuizAnswer> answers) {
        this.answers = answers;
    }

    // Helper method to get the lesson from the assignment
    public Lesson getLesson() {
        return (assignment != null) ? assignment.getLesson() : null;
    }

    // Calculate score based on answers
    public void calculateScore() {
        if (assignment == null || answers == null || answers.isEmpty()) {
            this.score = 0;
            return;
        }

        double pointsPerQuestion = assignment.getPointsPerQuestion();
        double totalScore = 0;

        for (QuizAnswer answer : answers) {
            if (answer.getQuestion().isCorrectAnswer(answer.getSelectedOptionId())) {
                totalScore += pointsPerQuestion;
            }
        }

        this.score = totalScore;
    }
}