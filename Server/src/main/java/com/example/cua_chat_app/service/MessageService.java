package com.example.cua_chat_app.service;

import com.example.cua_chat_app.entity.message.Message;
import com.example.cua_chat_app.repository.MessageRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRespository messageRespository;
    private final ChatRoomService chatRoomService;


    public Message save(Message message) {
        var chatRoomName = chatRoomService.getChatRoomName(
                message.getSenderId(),
                message.getReceiverId(),
                true
        ).orElseThrow();
        message.setChatRoomName(chatRoomName);
        messageRespository.save(message);
        return message;
    }

    public List<Message> findAllMessage(String senderId, String receiverId) {
        var chatRoomName = chatRoomService.getChatRoomName(senderId, receiverId, false);
        return chatRoomName.map(messageRespository::findByChatRoomName).orElse(new ArrayList<>());
    }
}
