package com.caput_draconis.service;

import com.caput_draconis.domain.domain.InputPassword;
import com.caput_draconis.domain.domain.Password;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PasswordService {
    List<Password> getAllPasswords();
    Password savePassword(InputPassword password);
    Password getPasswordByUuid(String uuid);
    void deletePasswordByUuid(String uuid);
    Boolean updatePassword(String uuid , String password);
}
