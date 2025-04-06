package com.example.cua_chat_app.repository;

import com.example.cua_chat_app.entity.InvalidatedToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository extends MongoRepository<InvalidatedToken, String> {}
