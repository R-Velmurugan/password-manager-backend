package com.caput_draconis.query;

import com.caput_draconis.domain.domain.InputPassword;
import com.caput_draconis.domain.domain.Notification;
import com.caput_draconis.domain.domain.Password;
import com.caput_draconis.service.NotificationService;
import com.caput_draconis.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class Query {
    private final PasswordService passwordService;
    private final NotificationService notificationService;

    @Autowired
    public Query(PasswordService passwordService , NotificationService notificationService) {
        this.passwordService = passwordService;
        this.notificationService = notificationService;
    }

    @QueryMapping
    public List<Password> passwords(@Argument("isActive") Boolean isActive , @Argument("username") String username , @Argument("masterPassword") String masterPassword) {
        return passwordService.getAllActiveOrTrashPasswords(isActive , username, masterPassword);
    }

    @QueryMapping
    public List<Password> multiplePasswords(@Argument("uuids") List<String> uuids , @Argument("username") String username) {
        return uuids.stream()
                .map(uuid -> passwordService.getPasswordByUuid(uuid , username, null))
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<Notification> notifications(@Argument("type") List<String> types , @Argument("username") String username){
        return notificationService.getAllNotificationsByUsernameAndTypes(username, types);
    }

    @MutationMapping
    public Password insertPassword(@Argument("passwordInput") InputPassword passwordInput){
        return passwordService.savePassword(passwordInput);
    }

    @MutationMapping
    public Boolean deletePassword(@Argument("uuid") String uuid){
        return passwordService.movePasswordToTrash(uuid);
    }
    @QueryMapping
    public Password password(@Argument("uuid") String uuid , @Argument("username") String username , @Argument("masterPassword") String masterPassword){
        return passwordService.getPasswordByUuid(uuid , username, masterPassword);
    }

    @MutationMapping
    public Boolean updatePassword(@Argument("uuid") String uuid , @Argument("password") String password){
        return passwordService.updatePassword(uuid , password);
    }

    @MutationMapping
    public Boolean restorePassword(@Argument("uuid") String uuid){
        return passwordService.restorePassword(uuid);
    }
}
