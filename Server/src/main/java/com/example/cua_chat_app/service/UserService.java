package com.example.cua_chat_app.service;

import com.example.cua_chat_app.entity.user.Status;
import com.example.cua_chat_app.entity.user.User;
import com.example.cua_chat_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    private final UserRepository userRepository;

    public void saveUser(User user) {
        user.setStatus(Status.ONLINE);
        userRepository.save(user);
    }

    public void disconnect(User user) {
        var storeUser = userRepository.findById(user.getId()).orElse(null);
        if (storeUser != null) {
            storeUser.setStatus(Status.OFFLINE);
            userRepository.save(storeUser);
        }
    }

    public List<User> listConnectedUsers(){
        return userRepository.findAllByStatus(Status.ONLINE);
    }
}
