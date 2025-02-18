package com.caput_draconis.service.impl;

import com.caput_draconis.domain.domain.User;
import com.caput_draconis.domain.entity.UserEntity;
import com.caput_draconis.repository.UserRepository;
import com.caput_draconis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository , PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public User findUserByUsername(String username) {
        List<UserEntity> userEntityList = userRepository.findByUsername(username);

        if (userEntityList.isEmpty()) return null;
        return convertUserEntityToUser(userEntityList.get(0));
    }

    @Override
    public Boolean registerUser(User user) {
        user.setHashedPassword(passwordEncoder.encode(user.getPassword()));
        UserEntity userEntity = convertUserToUserEntity(user);

        if(userRepository.existsById(user.getUsername())) return false;

        userRepository.save(userEntity);
        return true;
    }

    private User convertUserEntityToUser(UserEntity userEntity){
        return User.builder()
                .username(userEntity.getUsername())
                .hashedPassword(userEntity.getPassword())
                .email(userEntity.getEmail())
                .createdAt(userEntity.getCreated_at())
                .updatedAt(userEntity.getUpdated_at())
                .build();
    }

    private UserEntity convertUserToUserEntity(User user){
        return UserEntity.builder()
                .username(user.getUsername())
                .password(user.getHashedPassword())
                .email(user.getEmail())
                .created_at(user.getCreatedAt())
                .updated_at(user.getUpdatedAt())
                .build();
    }
}
