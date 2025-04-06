package com.example.cua_chat_app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    private String id;
    private String senderId;
    private String recipientId;
    private String content;
}
