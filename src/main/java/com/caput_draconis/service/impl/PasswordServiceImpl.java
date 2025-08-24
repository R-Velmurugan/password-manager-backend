package com.caput_draconis.service.impl;

import com.caput_draconis.domain.domain.InputPassword;
import com.caput_draconis.domain.domain.Password;
import com.caput_draconis.domain.entity.PasswordEntity;
import com.caput_draconis.domain.entity.UserEntity;
import com.caput_draconis.repository.PasswordRepository;
import com.caput_draconis.repository.UserRepository;
import com.caput_draconis.service.PasswordService;
import com.caput_draconis.util.CryptoUtils;
import com.caput_draconis.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PasswordServiceImpl implements PasswordService {
    private final PasswordRepository passwordRepository;
    private final UserRepository userRepository;

    @Autowired
    public PasswordServiceImpl(PasswordRepository passwordRepository, UserRepository userRepository){
        this.passwordRepository = passwordRepository;
        this.userRepository = userRepository;
    }
    @Override
    public List<Password> getAllActiveOrTrashPasswords(Boolean isActive, String username, String masterPassword) {
        UserEntity userEntity = userRepository.findByUsername(username).get(0);
        List<PasswordEntity> allPasswords = passwordRepository.findAllActiveOrTrashPasswords(isActive , userEntity);

        return allPasswords.stream()
                .map(passwordEntity -> convertPasswordEntityToPasswordDto(passwordEntity, masterPassword, userEntity))
                .collect(Collectors.toList());
    }

    @Override
    public Password savePassword(InputPassword inputPassword){
        UserEntity userEntity = userRepository.findByUsername(inputPassword.getUname()).get(0);
        final String currentDateTime = Utils.getCurrentDateTime();
        Password password = Password.builder()
                .uuid(UUID.randomUUID().toString())
                .domain(inputPassword.getDomain())
                .url(inputPassword.getUrl())
                .email(inputPassword.getEmail())
                .username(inputPassword.getUsername())
                .password(inputPassword.getPassword())
                .creationDate(currentDateTime)
                .updationDate(currentDateTime)
                .notes(inputPassword.getNotes())
                .build();
        passwordRepository.save(convertPasswordToPasswordEntity(password , userEntity , inputPassword.getMasterPassword()));
        return password;
    }

    @Override
    public Password getPasswordByUuid(String uuid, String username, String masterPassword){
        UserEntity userEntity = userRepository.findByUsername(username).get(0);
        PasswordEntity passwordEntity = passwordRepository.getReferenceByUuidAndUname(uuid , userEntity);
        return convertPasswordEntityToPasswordDto(passwordEntity, masterPassword, userEntity);
    }
    @Override
    @Transactional
    public Boolean movePasswordToTrash(String uuid){
        return passwordRepository.movePasswordToTrash(uuid) == 1;
    }

    @Override
    @Transactional
    public Boolean updatePassword(String uuid , String password){
        return passwordRepository.updatePasswordEntityByUuid(uuid , password) == 1;
    }

    @Override
    @Transactional
    public Boolean restorePassword(String uuid){
        return passwordRepository.restorePassword(uuid) == 1;
    }

    private PasswordEntity convertPasswordToPasswordEntity(Password password, UserEntity userEntity, String masterPassword){
        final byte [] salt = userEntity.getSalt();
        final SecretKey key = CryptoUtils.deriveKeyForEncryption(masterPassword , salt);
        final String encryptedPassword = CryptoUtils.encryptPassword(password.getPassword(), key);

        return PasswordEntity.builder()
                .uuid(password.getUuid())
                .domain_name(password.getDomain())
                .url(password.getUrl())
                .username(password.getUsername())
                .email(password.getEmail())
                .password(encryptedPassword)
                .created_at(Utils.convertStringToDate(password.getCreationDate()))
                .updated_at(Utils.convertStringToDate(password.getUpdationDate()))
                .notes(password.getNotes())
                .isDeleted(false)
                .uname(userEntity)
                .build();
    }

    private Password convertPasswordEntityToPasswordDto(PasswordEntity passwordEntity, String masterPassword, UserEntity userEntity){
        SecretKey key = CryptoUtils.deriveKeyForEncryption(masterPassword , userEntity.getSalt());
        final String encryptedPassword = passwordEntity.getPassword();
        final String decryptedPassword = CryptoUtils.decryptPassword(encryptedPassword, key);
        return Password.builder()
                .uuid(passwordEntity.getUuid())
                .domain(passwordEntity.getDomain_name())
                .url(passwordEntity.getUrl())
                .username(passwordEntity.getUsername())
                .email(passwordEntity.getEmail())
                .password(decryptedPassword)
                .creationDate(passwordEntity.getCreated_at().toString())
                .updationDate(passwordEntity.getUpdated_at().toString())
                .notes(passwordEntity.getNotes())
                .build();
    }
}
