package com.caput_draconis.service.impl;

import com.caput_draconis.domain.domain.Password;
import com.caput_draconis.domain.entity.PasswordEntity;
import com.caput_draconis.repository.PasswordRepository;
import com.caput_draconis.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
