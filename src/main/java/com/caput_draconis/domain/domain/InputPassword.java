package com.caput_draconis.domain.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputPassword {
    private String domain;
    private String url;
    private String username;
    private String email;
    private String password;
    private String notes;
}
