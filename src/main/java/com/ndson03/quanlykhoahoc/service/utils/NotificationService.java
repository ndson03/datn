package com.ndson03.quanlykhoahoc.service.utils;

import java.util.List;

import com.ndson03.quanlykhoahoc.domain.entity.Notification;
import com.ndson03.quanlykhoahoc.domain.entity.Student;

public interface NotificationService {

    List<Notification> findAllByStudent(Student student);

    List<Notification> findUnreadByStudent(Student student);

    int countUnreadByStudent(Student student);

    Notification findById(int id);

    void save(Notification notification);

    void markAsRead(int notificationId);

    void markAllAsRead(Student student);

    void delete(int id);
}