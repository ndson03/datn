package com.ndson03.quanlykhoahoc.service.course;

import com.ndson03.quanlykhoahoc.domain.entity.Content;
import com.ndson03.quanlykhoahoc.repository.course.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContentServiceImpl implements ContentService {

    private ContentRepository contentRepository;

    @Autowired
    public ContentServiceImpl(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Override
    public List<Content> findAll() {
        return contentRepository.findAll();
    }

    @Override
    public Content findById(int id) {
        Optional<Content> result = contentRepository.findById(id);

        Content content = null;
        if (result.isPresent()) {
            content = result.get();
        } else {
            throw new RuntimeException("Could not find content with id: " + id);
        }

        return content;
    }

    @Override
    public List<Content> findByLessonId(int lessonId) {
        return contentRepository.findByLessonIdOrderByOrderNumberAsc(lessonId);
    }

    @Override
    public void save(Content content) {
        contentRepository.save(content);
    }

    @Override
    public void deleteById(int id) {
        contentRepository.deleteById(id);
    }

    @Override
    public int getNextOrderNumber(int lessonId) {
        Integer maxOrderNumber = contentRepository.findMaxOrderNumberByLessonId(lessonId);
        return (maxOrderNumber != null) ? maxOrderNumber + 1 : 1;
    }

    @Override
    public Content findByContentData(String contentData) {
        return contentRepository.findByContentData(contentData);
    }
}