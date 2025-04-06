package com.example.cua_chat_app.service;

import com.example.cua_chat_app.entity.Status;
import com.example.cua_chat_app.entity.User;
import com.example.cua_chat_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void saveUser(User user) {
        user.setStatus(Status.ONLINE);
        repository.save(user);
    }

    public void disconnect(User user) {
        var storedUser = repository.findById(user.getNickName()).orElse(null);
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            repository.save(storedUser);
        }
    }

    public List<User> findConnectedUsers() {
        return repository.findAllByStatus(Status.ONLINE);
    }
    public List<User> findUnConnectedUsers()  {
        return repository.findAllByStatus(Status.OFFLINE);
    }

    public List<User> findAllUsers() {
        return repository.findAll();
    }
}
