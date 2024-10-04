package com.caput_draconis.job;

import com.caput_draconis.repository.PasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeletePasswordsFromTrash {
    private final PasswordRepository passwordRepository;

    @Autowired
    public DeletePasswordsFromTrash(PasswordRepository passwordRepository){
        this.passwordRepository = passwordRepository;
    }
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deletePasswordsFromTrash(){
        passwordRepository.deletePasswordsFromTrash();
    }
}
