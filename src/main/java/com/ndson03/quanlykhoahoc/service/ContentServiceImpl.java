package com.ndson03.quanlykhoahoc.service;

import com.ndson03.quanlykhoahoc.entity.Content;
import com.ndson03.quanlykhoahoc.repository.ContentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContentServiceImpl implements ContentService {

    private ContentDao contentDao;

    @Autowired
    public ContentServiceImpl(ContentDao contentDao) {
        this.contentDao = contentDao;
    }

    @Override
    public List<Content> findAll() {
        return contentDao.findAll();
    }

    @Override
    public Content findById(int id) {
        Optional<Content> result = contentDao.findById(id);

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
        return contentDao.findByLessonIdOrderByOrderNumberAsc(lessonId);
    }

    @Override
    public void save(Content content) {
        contentDao.save(content);
    }

    @Override
    public void deleteById(int id) {
        contentDao.deleteById(id);
    }

    @Override
    public int getNextOrderNumber(int lessonId) {
        Integer maxOrderNumber = contentDao.findMaxOrderNumberByLessonId(lessonId);
        return (maxOrderNumber != null) ? maxOrderNumber + 1 : 1;
    }
}