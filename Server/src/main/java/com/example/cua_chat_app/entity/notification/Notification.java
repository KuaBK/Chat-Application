package com.example.cua_chat_app.entity.notification;

import com.example.cua_chat_app.entity.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    private String chatId;
    private String content;
    private String senderId;
    private String receiverId;
    private String chatRoomName;
    private MessageType messageType;
    private NotificationType type;
    private byte[] media;
}