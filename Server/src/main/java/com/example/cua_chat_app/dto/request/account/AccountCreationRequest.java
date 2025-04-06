package com.example.cua_chat_app.dto.request.account;

import com.example.cua_chat_app.entity.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountCreationRequest {
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password;

    @Email
    String email;

    String fullName;
}
