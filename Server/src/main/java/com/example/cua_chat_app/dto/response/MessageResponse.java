package com.example.cua_chat_app.dto.response;

import com.example.cua_chat_app.entity.message.MessageState;
import com.example.cua_chat_app.entity.message.MessageType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {

    private Long id;
    private String content;
    private MessageType type;
    private MessageState state;
    private String senderId;
    private String receiverId;
    private LocalDateTime createdAt;
    private byte[] media;
}
