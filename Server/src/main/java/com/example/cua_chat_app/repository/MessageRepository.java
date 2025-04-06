package com.example.cua_chat_app.repository;

import com.example.cua_chat_app.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message,String> {
    List<Message> findByChatId(String chatId);
}
