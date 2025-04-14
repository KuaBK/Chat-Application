package com.example.cua_chat_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
public class CuaChatAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CuaChatAppApplication.class, args);
	}

}
