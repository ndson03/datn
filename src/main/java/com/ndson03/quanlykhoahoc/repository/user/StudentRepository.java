package com.ndson03.quanlykhoahoc.repository.user;

import com.ndson03.quanlykhoahoc.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

	Student findByUserName(String userName);

	Student findByEmail(String email);

	// Các hàm như save(), findById(), deleteById(), findAll() có sẵn trong JpaRepository
}
