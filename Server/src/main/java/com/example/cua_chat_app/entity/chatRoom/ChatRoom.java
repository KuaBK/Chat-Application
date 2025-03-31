package com.example.cua_chat_app.entity.chatRoom;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ChatRoom {
    @Id
    String id;

    String chatRoomName;
    String senderId;
    String receiverId;
}
