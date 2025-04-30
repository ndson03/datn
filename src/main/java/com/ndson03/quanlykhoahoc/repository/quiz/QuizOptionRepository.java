package com.ndson03.quanlykhoahoc.repository.quiz;

import com.ndson03.quanlykhoahoc.domain.entity.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, Integer> {
    List<QuizOption> findByQuestionId(int questionId);
}
