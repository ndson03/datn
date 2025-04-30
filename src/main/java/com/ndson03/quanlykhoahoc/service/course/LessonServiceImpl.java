package com.ndson03.quanlykhoahoc.service.course;

import com.ndson03.quanlykhoahoc.domain.entity.Assignment;
import com.ndson03.quanlykhoahoc.domain.entity.Lesson;
import com.ndson03.quanlykhoahoc.repository.assignment.AssignmentRepository;
import com.ndson03.quanlykhoahoc.repository.course.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonServiceImpl implements LessonService {

    private LessonRepository lessonRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    @Override
    public Lesson findById(int id) {
        Optional<Lesson> result = lessonRepository.findById(id);

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
        return lessonRepository.findByCourseIdOrderByOrderNumberAsc(courseId);
    }

    @Override
    public void save(Lesson lesson) {
        lessonRepository.save(lesson);
    }

    @Override
    public void deleteById(int id) {
        lessonRepository.deleteById(id);
    }

    @Override
    public int getNextOrderNumber(int courseId) {
        Integer maxOrderNumber = lessonRepository.findMaxOrderNumberByCourseId(courseId);
        return (maxOrderNumber != null) ? maxOrderNumber + 1 : 1;
    }

    @Override
    public List<Assignment> findByLessonId(int lessonId) {
        return assignmentRepository.findByLessonId(lessonId);
    }

}