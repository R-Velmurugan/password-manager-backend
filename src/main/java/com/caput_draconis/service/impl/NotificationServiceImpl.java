package com.caput_draconis.service.impl;

import com.caput_draconis.domain.domain.Notification;
import com.caput_draconis.domain.entity.NotificationEntity;
import com.caput_draconis.repository.UserRepository;
import com.caput_draconis.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final UserRepository userRepository;

    @Autowired
    public NotificationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Notification createNotification(Notification notification) {
        return null;
    }

    private Notification updateNotification(Notification notification) {
        return null;
    }

    @Override
    public void deleteNotification(Notification notification) {

    }

    @Override
    public List<Notification> getAllNotificationsByUsername(String username) {
        return List.of();
    }

    private NotificationEntity convertNotificationToNotificationEntity(Notification notification) {
        return NotificationEntity.builder()
                .id(notification.getId())
                .type(notification.getType().getNotificationType())
                .description(notification.getDescription())
                .userEntity(userRepository.findByUsername(notification.getUsername()).get(0))
                .build();
    }

    private Notification convertNotificationEntityToNotification(NotificationEntity notificationEntity) {
        return Notification.builder()
                .id(notificationEntity.getId())
                .type(Notification.NotificationType.valueOf(notificationEntity.getType()))
                .description(notificationEntity.getDescription())
                .username(notificationEntity.getUserEntity().getUsername())
                .build();
    }
}
