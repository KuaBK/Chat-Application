package com.example.cua_chat_app.repository.mongo;

import com.example.cua_chat_app.entity.mongo.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message,String> {
    List<Message> findByChatId(String chatId);
}
