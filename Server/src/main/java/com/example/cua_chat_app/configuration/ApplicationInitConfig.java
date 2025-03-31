package com.example.cua_chat_app.configuration;

import com.example.cua_chat_app.entity.account.Account;
import com.example.cua_chat_app.entity.account.Role;
import com.example.cua_chat_app.repository.AccountRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Configuration(proxyBeanMethods=false)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    AccountRepository accountRepository;

    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    String adminPassword = dotenv.get("ADMIN_PASSWORD", "default_password");

    String adminEmail = "admin@gmail.com";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner() {
        return _ -> {
            Optional<Account> adminAccount = accountRepository.findByEmail(adminEmail);

            if (adminAccount.isEmpty()) {
                Account admin = Account.builder()
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .build();

                accountRepository.save(admin);
            }
            log.info("Application initialization completed.");
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
