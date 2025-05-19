package com.ndson03.quanlykhoahoc.repository.user;

import com.ndson03.quanlykhoahoc.domain.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

	Teacher findByUserName(String userName);

	Teacher findByEmail(String email);

	// Các method như save(), deleteById(), findAll()... đã có sẵn từ JpaRepository
}
