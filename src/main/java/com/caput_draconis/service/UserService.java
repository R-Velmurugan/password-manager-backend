package com.caput_draconis.service;

import com.caput_draconis.domain.domain.User;

public interface UserService {
    User findUserByUsername(String username);
    Boolean registerUser(User user);
}
