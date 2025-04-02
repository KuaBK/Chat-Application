package com.example.cua_chat_app.repository.mongo;

import com.example.cua_chat_app.entity.mongo.Status;
import com.example.cua_chat_app.entity.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findAllByStatus(Status status);
}
