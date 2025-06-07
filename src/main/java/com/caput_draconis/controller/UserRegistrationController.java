package com.caput_draconis.controller;

import com.caput_draconis.domain.domain.User;
import com.caput_draconis.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
public class UserRegistrationController {

    private final UserService userService;

    private final JobLauncher jobLauncher;
    private final Job job;

    @Autowired
    public UserRegistrationController(UserService userService , JobLauncher jobLauncher , Job passwordHealthJob) {
        this.userService = userService;
        this.jobLauncher = jobLauncher;
        this.job = passwordHealthJob;
    }

    @PostMapping("/register")
    public HttpStatus registerUser(@RequestBody final User user){
        if(userService.registerUser(user)){
            return HttpStatus.CREATED;
        }
        return HttpStatus.CONFLICT;
    }

    @PostMapping("/isLoggedIn")
    public ResponseEntity<?> isLoggedIn(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(Objects.isNull(session) || Objects.isNull(SecurityContextHolder.getContext().getAuthentication().getName()) || !request.isRequestedSessionIdValid()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        return ResponseEntity.status(HttpStatus.OK).body(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
    }

    @GetMapping("/job")
    public void getJob(){
        try {
            jobLauncher.run(job , new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters());
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }
}
