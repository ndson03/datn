package com.ndson03.quanlykhoahoc.controller.file;

import com.ndson03.quanlykhoahoc.domain.entity.Content;
import com.ndson03.quanlykhoahoc.service.course.ContentService;
import com.ndson03.quanlykhoahoc.service.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ContentFileController {

    @Autowired
    private ContentService contentService;

    @Value("${file.upload.directory}")
    private String uploadDirectory;

    @GetMapping("/download-content-file/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            final String uploadDirectory = "uploads/content_file";

            // Tìm nội dung dựa trên tên file đã lưu trong hệ thống
            Content content = contentService.findByContentData(filename);
            Path file = Paths.get(uploadDirectory).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Xác định content type dựa trên phần mở rộng của file
                String contentType = determineContentType(filename);

                // Xác định cách hiển thị file (inline để hiển thị trực tiếp, attachment để tải về)
                String contentDisposition = "inline";
                if (!contentType.startsWith("image/") && !contentType.startsWith("video/") && !contentType.startsWith("audio/")) {
                    contentDisposition = "attachment";
                }

                // Sử dụng tên file gốc nếu có, nếu không thì sử dụng tên file hiện tại
                String displayFilename = (content != null && content.getFilename() != null && !content.getFilename().isEmpty())
                        ? content.getFilename()
                        : resource.getFilename();

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition + "; filename=\"" + displayFilename + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Không thể đọc file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Lỗi: " + e.getMessage());
        }
    }

    // Phương thức xác định content type dựa trên phần mở rộng của file
    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/msword";
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "ppt":
            case "pptx":
                return "application/vnd.ms-powerpoint";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            case "mp4":
                return "video/mp4";
            case "webm":
                return "video/webm";
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            case "txt":
                return "text/plain";
            case "html":
            case "htm":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            default:
                return "application/octet-stream";
        }
    }
}
