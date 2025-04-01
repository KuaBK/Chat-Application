package com.example.cua_chat_app.entity.message;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document
public class Message {
    @Id
    String id;

    String chatRoomName;
    String senderId;
    String receiverId;
    String content;
    Date timestamp;
}
