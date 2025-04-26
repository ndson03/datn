package com.ndson03.quanlykhoahoc.service;

import com.ndson03.quanlykhoahoc.entity.Lesson;

import java.util.List;

public interface LessonService {

    List<Lesson> findAll();

    Lesson findById(int id);

    List<Lesson> findByCourseId(int courseId);

    void save(Lesson lesson);

    void deleteById(int id);

    int getNextOrderNumber(int courseId);
}