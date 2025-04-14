package com.example.cua_chat_app.service;

import com.example.cua_chat_app.dto.response.ChatRoomResponse;
import com.example.cua_chat_app.entity.chat.ChatRoom;
import com.example.cua_chat_app.entity.user.User;
import com.example.cua_chat_app.mapper.ChatRoomMapper;
import com.example.cua_chat_app.repository.ChatRoomRepository;
import com.example.cua_chat_app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomMapper chatRoomMapper;

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatsByReceiverId(Authentication currentUser) {
        final String userId = currentUser.getName();
        return chatRoomRepository.findChatsBySenderId(userId)
                .stream()
                .map(c -> chatRoomMapper.toChatResponse(c, userId))
                .toList();
    }

    public String createChat(String senderId, String receiverId) {
        Optional<ChatRoom> existingChat = chatRoomRepository.findChatByReceiverAndSender(senderId, receiverId);
        if (existingChat.isPresent()) {
            return existingChat.get().getId();
        }

        User sender = userRepository.findByPublicId(senderId)
                .orElseThrow(() ->  new EntityNotFoundException("User with id " + senderId + " not found"));
        User receiver = userRepository.findByPublicId(receiverId)
                .orElseThrow(() ->  new EntityNotFoundException("User with id " + receiverId + " not found"));

        ChatRoom chat = new ChatRoom();
        chat.setSender(sender);
        chat.setRecipient(receiver);

        ChatRoom savedChat = chatRoomRepository.save(chat);
        return savedChat.getId();
    }
}
