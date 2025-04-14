package com.example.cua_chat_app.mapper;

import com.example.cua_chat_app.dto.response.ChatRoomResponse;
import com.example.cua_chat_app.entity.chat.ChatRoom;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomMapper {
    public ChatRoomResponse toChatResponse(ChatRoom chatRoom, String senderId) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getChatName(senderId))
                .unreadCount(chatRoom.getUnreadMessages(senderId))
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageTime(chatRoom.getLastMessageTime())
                .isRecipientOnline(chatRoom.getRecipient().isUserOnline())
                .senderId(chatRoom.getSender().getId())
                .receiverId(chatRoom.getRecipient().getId())
                .build();
    }
}