package com.caput_draconis.domain.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class User {
    private String username;
    private String hashedPassword;
    private String password;
    private String email;
    private String createdAt;
    private String updatedAt;
}
