package com.caput_draconis.service;

import com.caput_draconis.domain.domain.InputPassword;
import com.caput_draconis.domain.domain.Password;

import java.util.List;

public interface PasswordService {
    List<Password> getAllActiveOrTrashPasswords(Boolean isActive);
    Password savePassword(InputPassword password);
    Password getPasswordByUuid(String uuid);
    Boolean movePasswordToTrash(String uuid);
    Boolean updatePassword(String uuid , String password);
}
