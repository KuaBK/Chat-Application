package com.example.cua_chat_app.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime lastSeen;
    private boolean isOnline;
}