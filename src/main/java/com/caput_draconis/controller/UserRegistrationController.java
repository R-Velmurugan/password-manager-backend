package com.caput_draconis.controller;

import com.caput_draconis.domain.domain.User;
import com.caput_draconis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRegistrationController {

    private final UserService userService;

    @Autowired
    public UserRegistrationController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public HttpStatus registerUser(@RequestBody final User user){
        if(userService.registerUser(user)){
            return HttpStatus.CREATED;
        };
        return HttpStatus.CONFLICT;
    }

}
