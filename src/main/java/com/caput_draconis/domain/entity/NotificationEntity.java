package com.caput_draconis.domain.entity;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(
        name = "notifications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"type", "username"})
        //to ensure there is only one entry per user per type
)
public class NotificationEntity {
    @Id
    //type|username
    private String uuid;
    private String type;
    @Transient
    private Map<String, Object> description;
    //json and jsonb are allowed. json stores as text with whitespaces and all duplicates are kept. Processing uses last duplicate.
    //jsonb is widely used. Json is converted to binary thus removing whitespaces. Last duplicate is kept. Can be indexed. Json indexing is limited.

    @Column(name = "description" , columnDefinition = "jsonb")
    private String descriptionAsJson;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username", referencedColumnName = "username", foreignKey = @ForeignKey(name = "username"))
    private UserEntity userEntity;

    public String getUsername() {
        if (Objects.nonNull(userEntity)) return userEntity.getUsername();
        return null;
    }

    public String getDescriptionAsJson() {
        try {
            return new ObjectMapper().writeValueAsString(description);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
