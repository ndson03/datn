package com.ndson03.quanlykhoahoc.service;

import com.ndson03.quanlykhoahoc.entity.Lesson;
import com.ndson03.quanlykhoahoc.repository.LessonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonServiceImpl implements LessonService {

    private LessonDao lessonDao;

    @Autowired
    public LessonServiceImpl(LessonDao lessonDao) {
        this.lessonDao = lessonDao;
    }

    @Override
    public List<Lesson> findAll() {
        return lessonDao.findAll();
    }

    @Override
    public Lesson findById(int id) {
        Optional<Lesson> result = lessonDao.findById(id);

        Lesson lesson = null;
        if (result.isPresent()) {
            lesson = result.get();
        } else {
            throw new RuntimeException("Could not find lesson with id: " + id);
        }

        return lesson;
    }

    @Override
    public List<Lesson> findByCourseId(int courseId) {
        return lessonDao.findByCourseIdOrderByOrderNumberAsc(courseId);
    }

    @Override
    public void save(Lesson lesson) {
        lessonDao.save(lesson);
    }

    @Override
    public void deleteById(int id) {
        lessonDao.deleteById(id);
    }

    @Override
    public int getNextOrderNumber(int courseId) {
        Integer maxOrderNumber = lessonDao.findMaxOrderNumberByCourseId(courseId);
        return (maxOrderNumber != null) ? maxOrderNumber + 1 : 1;
    }
}