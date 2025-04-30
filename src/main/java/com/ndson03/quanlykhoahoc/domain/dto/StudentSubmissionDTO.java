package com.ndson03.quanlykhoahoc.domain.dto;

import com.ndson03.quanlykhoahoc.domain.entity.AssignmentFileSubmission;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentSubmissionDTO {
    private int studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime completionDate;
    private List<AssignmentFileSubmission> files;
    private int fileCount;

    // Default constructor
    public StudentSubmissionDTO() {
    }

    // Getters and setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public List<AssignmentFileSubmission> getFiles() {
        return files;
    }

    public void setFiles(List<AssignmentFileSubmission> files) {
        this.files = files;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFormattedStartTime() {
        if (startTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return startTime.format(formatter);
    }

    public String getFormattedCompletionDate() {
        if (completionDate == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return completionDate.format(formatter);
    }

    public boolean hasSubmittedFiles() {
        return files != null && !files.isEmpty();
    }
}