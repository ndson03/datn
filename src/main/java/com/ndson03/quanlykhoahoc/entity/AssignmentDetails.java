package com.ndson03.quanlykhoahoc.entity;

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

	@Column(name="assignment_id")
	private int assignmentId;

	@Column(name="student_course_details_id")
	private int studentCourseDetailsId;

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

	public AssignmentDetails(int id, int assignmentId, int studentCourseDetailsId, int isDone) {
		this.id = id;
		this.assignmentId = assignmentId;
		this.studentCourseDetailsId = studentCourseDetailsId;
		this.isDone = isDone;
	}

	public AssignmentDetails(int id, int assignmentId, int studentCourseDetailsId, int isDone,
							 double score, LocalDateTime startTime, LocalDateTime submitTime, int timeSpent) {
		this.id = id;
		this.assignmentId = assignmentId;
		this.studentCourseDetailsId = studentCourseDetailsId;
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
}