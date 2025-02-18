package com.caput_draconis.controller;

import com.caput_draconis.config.LoginAuthenticationProvider;
import com.caput_draconis.domain.domain.User;
import com.caput_draconis.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class UserRegistrationController {

    private final UserService userService;
    private final LoginAuthenticationProvider loginAuthenticationProvider;

    @Autowired
    public UserRegistrationController(UserService userService , LoginAuthenticationProvider loginAuthenticationProvider) {
        this.userService = userService;
        this.loginAuthenticationProvider = loginAuthenticationProvider;
    }

    @PostMapping("/register")
    public HttpStatus registerUser(@RequestBody final User user){
        if(userService.registerUser(user)){
            return HttpStatus.CREATED;
        };
        return HttpStatus.CONFLICT;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request , HttpServletResponse response, @RequestParam String username , @RequestParam String password){
        Authentication authentication = loginAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(username , password));
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        return ResponseEntity.ok("Login successful");

    }

    @PostMapping("/isLoggedIn")
    public ResponseEntity<?> isLoggedIn(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(Objects.isNull(session) || Objects.isNull(session.getAttribute("SPRING_SECURITY_CONTEXT"))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login first");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Session is logged in");
    }

}
