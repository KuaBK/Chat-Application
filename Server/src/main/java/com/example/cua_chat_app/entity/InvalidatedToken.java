package com.example.cua_chat_app.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "invalidated_tokens") // Tên collection trong MongoDB
public class InvalidatedToken {
    @Id
    String id;

    Date expiryTime;
}
