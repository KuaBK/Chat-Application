package com.example.cua_chat_app.repository;

import com.example.cua_chat_app.constant.ChatRoomConstants;
import com.example.cua_chat_app.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Query(name = ChatRoomConstants.FIND_CHATROOM_BY_SENDER_ID)
    List<ChatRoom> findChatsBySenderId(@Param("senderId") String senderId);

    @Query(name = ChatRoomConstants.FIND_CHATROOM_BY_SENDER_ID_AND_RECEIVER)
    Optional<ChatRoom> findChatByReceiverAndSender(@Param("senderId") String id, @Param("recipientId") String recipientId);
}