package com.ndson03.quanlykhoahoc.service.course;

import com.ndson03.quanlykhoahoc.domain.entity.Content;
import java.util.List;

public interface ContentService {

    List<Content> findAll();

    Content findById(int id);

    List<Content> findByLessonId(int lessonId);

    void save(Content content);

    void deleteById(int id);

    int getNextOrderNumber(int lessonId);

    Content findByContentData(String contentData);
}