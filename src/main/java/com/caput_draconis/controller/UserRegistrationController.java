package com.caput_draconis.controller;

import com.caput_draconis.domain.domain.User;
import com.caput_draconis.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class UserRegistrationController {

    private final UserService userService;


    @Autowired
    public UserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody final User user){
        if(userService.registerUser(user)){
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
    }

    @PostMapping("/isLoggedIn")
    public ResponseEntity<?> isLoggedIn(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(Objects.isNull(session) || Objects.isNull(SecurityContextHolder.getContext().getAuthentication().getName()) || !request.isRequestedSessionIdValid()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        return ResponseEntity.status(HttpStatus.OK).body(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
    }
}
