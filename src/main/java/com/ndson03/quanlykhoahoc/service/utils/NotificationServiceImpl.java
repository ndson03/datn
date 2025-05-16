package com.ndson03.quanlykhoahoc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndson03.quanlykhoahoc.domain.entity.Notification;
import com.ndson03.quanlykhoahoc.domain.entity.Student;
import com.ndson03.quanlykhoahoc.repository.notification.NotificationRepository;
import com.ndson03.quanlykhoahoc.service.utils.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<Notification> findAllByStudent(Student student) {
        return notificationRepository.findByStudentOrderByCreatedAtDesc(student);
    }

    @Override
    public List<Notification> findUnreadByStudent(Student student) {
        return notificationRepository.findByStudentAndIsReadOrderByCreatedAtDesc(student, false);
    }

    @Override
    public int countUnreadByStudent(Student student) {
        return notificationRepository.countByStudentAndIsRead(student, false);
    }

    @Override
    public Notification findById(int id) {
        return notificationRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAsRead(int notificationId) {
        Notification notification = findById(notificationId);
        if (notification != null) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Student student) {
        List<Notification> unreadNotifications = findUnreadByStudent(student);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional
    public void delete(int id) {
        notificationRepository.deleteById(id);
    }
}