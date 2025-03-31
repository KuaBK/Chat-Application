package com.example.cua_chat_app.service;

import com.example.cua_chat_app.entity.chatRoom.ChatRoom;
import com.example.cua_chat_app.repository.ChatRoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomName(String senderId, String receiverId, boolean isExist) {
        return chatRoomRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .map(ChatRoom::getChatRoomName)
                .or(() ->{
                    if (isExist) {
                        var chatRoomName = createChatRoomName(senderId, receiverId);
                        return Optional.of(chatRoomName);
                    }
                    return Optional.empty();
                });
    }

    private String createChatRoomName(String senderId, String receiverId) {
        var chatRoomName = String.format("%s-%s", senderId, receiverId);
        ChatRoom senderToReceiver = ChatRoom.builder()
                .chatRoomName(chatRoomName)
                .senderId(senderId)
                .receiverId(receiverId)
                .build();

        ChatRoom receiverToSender = ChatRoom.builder()
                .chatRoomName(chatRoomName)
                .senderId(senderId)
                .receiverId(receiverId)
                .build();
        chatRoomRepository.save(senderToReceiver);
        chatRoomRepository.save(receiverToSender);
        return chatRoomName;
    }
}
