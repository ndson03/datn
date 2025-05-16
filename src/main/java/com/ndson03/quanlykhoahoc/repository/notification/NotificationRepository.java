package com.ndson03.quanlykhoahoc.repository.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ndson03.quanlykhoahoc.domain.entity.Notification;
import com.ndson03.quanlykhoahoc.domain.entity.Student;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByStudentOrderByCreatedAtDesc(Student student);

    List<Notification> findByStudentAndIsReadOrderByCreatedAtDesc(Student student, boolean isRead);

    int countByStudentAndIsRead(Student student, boolean isRead);
}