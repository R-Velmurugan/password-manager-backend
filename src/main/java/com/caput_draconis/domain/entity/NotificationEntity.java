package com.caput_draconis.domain.entity;


import com.caput_draconis.util.JsonPasswordConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    private String id;
    private String type;

    @Convert(converter = JsonPasswordConverter.class)
    @Column(columnDefinition = "jsonb")
    //json and jsonb are allowed. json stores as text with whitespaces and all duplicates are kept. Processing uses last duplicate.
    //jsonb is widely used. Json is converted to binary thus removing whitespaces. Last duplicate is kept. Can be indexed. Json indexing is limited.
    private Map<String, Object> description;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username" , referencedColumnName = "username" , foreignKey = @ForeignKey(name = "username"))
    private UserEntity userEntity;
}
