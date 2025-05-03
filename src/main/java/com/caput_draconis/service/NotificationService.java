package com.caput_draconis.service;

import com.caput_draconis.domain.domain.Notification;

import java.util.List;

public interface NotificationService {
    Notification createNotification(Notification notification);
    void deleteNotification(Notification notification);
    List<Notification> getAllNotificationsByUsername(String username);
}
