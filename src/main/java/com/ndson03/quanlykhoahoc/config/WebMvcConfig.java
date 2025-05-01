package com.ndson03.quanlykhoahoc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.directory}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Đăng ký đường dẫn ảo "/uploads/**" đến thư mục vật lý chứa hình ảnh
        Path uploadPath = Paths.get(uploadDir);
        String uploadPathString = uploadPath.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPathString + "/");
    }
}