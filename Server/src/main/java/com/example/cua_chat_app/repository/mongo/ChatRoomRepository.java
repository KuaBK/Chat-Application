package com.example.cua_chat_app.repository.mongo;

import com.example.cua_chat_app.entity.mongo.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}

