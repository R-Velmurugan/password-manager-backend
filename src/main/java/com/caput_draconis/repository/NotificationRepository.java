package com.caput_draconis.repository;

import com.caput_draconis.domain.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {
    Optional<NotificationEntity> findNotificationByUuid(String uuid);
}
