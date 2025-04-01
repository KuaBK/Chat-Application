package com.example.cua_chat_app.repository;

import com.example.cua_chat_app.entity.message.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRespository extends MongoRepository<Message,String> {
    List<Message> findByChatRoomName(String strings);
}
