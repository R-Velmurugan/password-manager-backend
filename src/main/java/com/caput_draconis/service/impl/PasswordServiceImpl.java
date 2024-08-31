package com.caput_draconis.service.impl;

import com.caput_draconis.domain.domain.InputPassword;
import com.caput_draconis.domain.domain.Password;
import com.caput_draconis.domain.entity.PasswordEntity;
import com.caput_draconis.repository.PasswordRepository;
import com.caput_draconis.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PasswordServiceImpl implements PasswordService {
    private final PasswordRepository passwordRepository;

    @Autowired
    public PasswordServiceImpl(PasswordRepository passwordRepository){
        this.passwordRepository = passwordRepository;
    }
    @Override
    public List<Password> getAllPasswords() {
        List<PasswordEntity> allPasswords = passwordRepository.findAll();

        return allPasswords.stream()
                .map(this::convertPasswordEntityToPasswordDto)
                .collect(Collectors.toList());
    }

    @Override
    public Password savePassword(InputPassword inputPassword){
        Password password = Password.builder()
                .uuid(UUID.randomUUID().toString())
                .domain(inputPassword.getDomain())
                .url(inputPassword.getUrl())
                .email(inputPassword.getEmail())
                .username(inputPassword.getUsername())
                .password(inputPassword.getPassword())
                .creationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .updationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .notes(inputPassword.getNotes())
                .build();
        passwordRepository.save(convertPasswordToPasswordEntity(password));
        return password;
    }

    private PasswordEntity convertPasswordToPasswordEntity(Password password){
        return PasswordEntity.builder()
                .uuid(password.getUuid())
                .domain_name(password.getDomain())
                .url(password.getUrl())
                .username(password.getUsername())
                .email(password.getEmail())
                .password(password.getPassword())
                .created_at(
                        convertStringToDate(password.getCreationDate())
                )
                .updated_at(
                        convertStringToDate(password.getUpdationDate())
                )
                .notes(password.getNotes())
                .build();
    }

    private Date convertStringToDate(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return new Date();
    }

    private Password convertPasswordEntityToPasswordDto(PasswordEntity passwordEntity){
        return Password.builder()
                .uuid(passwordEntity.getUuid())
                .domain(passwordEntity.getDomain_name())
                .url(passwordEntity.getUrl())
                .username(passwordEntity.getUsername())
                .email(passwordEntity.getEmail())
                .password(passwordEntity.getPassword())
                .creationDate(passwordEntity.getCreated_at().toString())
                .updationDate(passwordEntity.getUpdated_at().toString())
                .notes(passwordEntity.getNotes())
                .build();
    }
}
