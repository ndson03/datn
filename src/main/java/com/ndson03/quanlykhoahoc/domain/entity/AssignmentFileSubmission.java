package com.ndson03.quanlykhoahoc.domain.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_file_submission")
public class AssignmentFileSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "assignment_details_id")
    private AssignmentDetails assignmentDetails;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Column(name = "submission_comment")
    private String submissionComment;

    public AssignmentFileSubmission() {
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AssignmentDetails getAssignmentDetails() {
        return assignmentDetails;
    }

    public void setAssignmentDetails(AssignmentDetails assignmentDetails) {
        this.assignmentDetails = assignmentDetails;
    }

    public int getAssignmentDetailsId() {
        return (assignmentDetails != null) ? assignmentDetails.getId() : 0;
    }

    public void setAssignmentDetailsId(int assignmentDetailsId) {
        // This is a helper method that would need corresponding logic
        // to set the actual assignmentDetails object
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getSubmissionComment() {
        return submissionComment;
    }

    public void setSubmissionComment(String submissionComment) {
        this.submissionComment = submissionComment;
    }
}