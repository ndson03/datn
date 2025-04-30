package com.ndson03.quanlykhoahoc.repository;

import com.ndson03.quanlykhoahoc.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByIsQuiz(boolean isQuiz);
}