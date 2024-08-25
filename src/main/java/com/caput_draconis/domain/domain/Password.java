package com.caput_draconis.domain.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Password {
    private String uuid;
    private String domain;
    private String url;
    private String username;
    private String email;
    private String password;
    private String creationDate;
    private String updationDate;
    private String notes;
}
