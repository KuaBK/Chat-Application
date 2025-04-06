package com.example.cua_chat_app.configuration;

import com.example.cua_chat_app.entity.Role;
import com.example.cua_chat_app.entity.Status;
import com.example.cua_chat_app.entity.User;
import com.example.cua_chat_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            Optional<User> adminAccount = userRepository.findByEmail("admin@gmail.com");
            if (adminAccount.isEmpty()) {
                User admin = User.builder()
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("admin"))
                        .fullName("Admin")
                        .status(Status.OFFLINE)
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
                log.info("Admin account created successfully.");
            } else {
                log.info("Admin account already exists.");
            }
            log.info("Application initialization completed.");
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
