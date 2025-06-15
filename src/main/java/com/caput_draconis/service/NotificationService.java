package com.caput_draconis.service;

import com.caput_draconis.domain.domain.Notification;
import com.caput_draconis.domain.entity.NotificationEntity;

import java.util.List;

public interface NotificationService {
    Notification createNotification(Notification notification);
    void deleteNotification(Notification notification);
    List<Notification> getAllNotificationsByUsername(String username);
    List<Notification> getAllNotificationsByUsernameAndTypes(String username , List<String> type);
    NotificationEntity convertNotificationToNotificationEntity(Notification notification);

    Notification convertNotificationEntityToNotification(NotificationEntity notificationEntity);
}
