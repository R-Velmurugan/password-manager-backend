package com.caput_draconis.service.impl;

import com.caput_draconis.domain.domain.Notification;
import com.caput_draconis.domain.entity.NotificationEntity;
import com.caput_draconis.repository.NotificationRepository;
import com.caput_draconis.repository.UserRepository;
import com.caput_draconis.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final UserRepository userRepository;
    private final ObjectMapper jacksonObjectMapper;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(UserRepository userRepository, ObjectMapper jacksonObjectMapper , NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.jacksonObjectMapper = jacksonObjectMapper;
        this.notificationRepository = notificationRepository;
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

    @Override
    public List<Notification> getAllNotificationsByUsernameAndTypes(String username, List<String> type) {
        return type.stream()
                .map(notificationType -> getNotificationByUsernameAndType(username , notificationType))
                .filter(Objects::nonNull)
                .toList();
    }

    private @Nullable Notification getNotificationByUsernameAndType(String username, String type) {
        Optional<NotificationEntity> notificationEntity = notificationRepository.findNotificationByUuid(type.concat("|").concat(username));
        return notificationEntity.map(this::convertNotificationEntityToNotification).orElse(null);
//        if(notificationEntity.isPresent()) {
//            return convertNotificationEntityToNotification(notificationEntity.get());
//        }
//        else return null;
    }

    @Override
    public @Nullable NotificationEntity convertNotificationToNotificationEntity(Notification notification) {
        try {
            return NotificationEntity.builder()
                    .uuid(notification.getUuid()) //return NE
                    .type(notification.getType().getNotificationType())
                    .descriptionAsJson(jacksonObjectMapper.writeValueAsString(notification.getDescription()))
                    .userEntity(userRepository.findByUsername(notification.getUsername()).get(0))
                    .build();
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public Notification convertNotificationEntityToNotification(NotificationEntity notificationEntity) {
        return Notification.builder()
                .uuid(notificationEntity.getUuid())
                .type(Notification.NotificationType.getNotificationType(notificationEntity.getType()))
                .description(notificationEntity.getDescription())
                .username(notificationEntity.getUserEntity().getUsername())
                .build();
    }
}
