package com.example.cua_chat_app.dto.request;

import com.example.cua_chat_app.entity.message.MessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {
    private String content;
    private String senderId;
    private String receiverId;
    private MessageType type;
    private String chatRoomId;
}