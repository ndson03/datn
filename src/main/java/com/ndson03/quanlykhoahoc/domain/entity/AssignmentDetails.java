package com.ndson03.quanlykhoahoc.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="assignment_details")
public class AssignmentDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;

	@ManyToOne
	@JoinColumn(name="assignment_id")
	private Assignment assignment;

	@ManyToOne
	@JoinColumn(name="student_course_details_id")
	private StudentCourseDetails studentCourseDetails;

	@Column(name="is_done")
	private int isDone;

	@Column(name="score")
	private double score;

	@Column(name="start_time")
	private LocalDateTime startTime;

	@Column(name="submit_time")
	private LocalDateTime submitTime;

	@Column(name="time_spent")
	private int timeSpent; // thời gian sử dụng để làm bài (phút)

	public AssignmentDetails() {
	}

	public AssignmentDetails(int id, Assignment assignment, StudentCourseDetails studentCourseDetails, int isDone) {
		this.id = id;
		this.assignment = assignment;
		this.studentCourseDetails = studentCourseDetails;
		this.isDone = isDone;
	}

	public AssignmentDetails(int id, Assignment assignment, StudentCourseDetails studentCourseDetails, int isDone,
							 double score, LocalDateTime startTime, LocalDateTime submitTime, int timeSpent) {
		this.id = id;
		this.assignment = assignment;
		this.studentCourseDetails = studentCourseDetails;
		this.isDone = isDone;
		this.score = score;
		this.startTime = startTime;
		this.submitTime = submitTime;
		this.timeSpent = timeSpent;
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

	public int getIsDone() {
		return isDone;
	}

	public void setIsDone(int isDone) {
		this.isDone = isDone;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(LocalDateTime submitTime) {
		this.submitTime = submitTime;
	}

	public int getTimeSpent() {
		return timeSpent;
	}

	public void setTimeSpent(int timeSpent) {
		this.timeSpent = timeSpent;
	}

	// Helper method to get assignment ID
	public int getAssignmentId() {
		return (assignment != null) ? assignment.getId() : 0;
	}

	// Helper method to get student course details ID
	public int getStudentCourseDetailsId() {
		return (studentCourseDetails != null) ? studentCourseDetails.getId() : 0;
	}
}