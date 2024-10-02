package com.caput_draconis.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "passwords")
public class PasswordEntity {
    @Id
    private String uuid;
    private String domain_name;
    private String url;
    private String username;
    private String email;
    private String password;
    private Date created_at;
    private Date updated_at;
    private String notes;
    private Boolean isDeleted;
}
