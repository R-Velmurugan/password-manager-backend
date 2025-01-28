package com.caput_draconis.config;

import com.caput_draconis.domain.domain.User;
import com.caput_draconis.repository.UserRepository;
import com.caput_draconis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LoginAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public LoginAuthenticationProvider(UserService userService , PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        User user = userService.findUserByUsername(authentication.getName());
        if(Objects.isNull(user)) return null;
        if(!passwordEncoder.matches(password , user.getHashedPassword())) return null;
        return new UsernamePasswordAuthenticationToken(username , password , null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
