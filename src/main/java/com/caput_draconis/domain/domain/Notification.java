package com.caput_draconis.domain.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    String uuid;
    NotificationType type;
    Map<String, Object> description;
    String username;

    public enum NotificationType {
        PASSWORD_EXPIRED("password_expired");

        public String getNotificationType() {
            return notification;
        }
        private final String notification;
        NotificationType(String notification){
            this.notification = notification;
        }

        private static Map<String, NotificationType> notificationLookup = new HashMap<>();
        static{
            notificationLookup.put("password_expired", NotificationType.PASSWORD_EXPIRED);
        }

        public static NotificationType getNotificationType(String notificationType) {
            return notificationLookup.get(notificationType);
        }
    }
}