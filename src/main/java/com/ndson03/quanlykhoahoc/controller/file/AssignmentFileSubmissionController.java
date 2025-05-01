package com.ndson03.quanlykhoahoc.controller.file;

import com.ndson03.quanlykhoahoc.domain.entity.AssignmentFileSubmission;
import com.ndson03.quanlykhoahoc.service.assignment.AssignmentFileSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AssignmentFileSubmissionController {

    @Autowired
    AssignmentFileSubmissionService assignmentFileSubmissionService;


    @GetMapping("/download-assignment-file-submission/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") int fileId) {
        AssignmentFileSubmission assignmentFileSubmission = assignmentFileSubmissionService.findById(fileId);

        if (assignmentFileSubmission != null) {
            try {
                Path filePath = Paths.get(assignmentFileSubmission.getFilePath());
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists() || resource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + assignmentFileSubmission.getOriginalFileName() + "\"")
                            .body(resource);
                } else {
                    throw new RuntimeException("Could not read the file!");
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("Error: " + e.getMessage());
            }
        }

        return ResponseEntity.notFound().build();
    }

}
