package com.example.cua_chat_app.repository;

import com.example.cua_chat_app.entity.user.Status;
import com.example.cua_chat_app.entity.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
    List<User> findAllByStatus(Status status);
}
