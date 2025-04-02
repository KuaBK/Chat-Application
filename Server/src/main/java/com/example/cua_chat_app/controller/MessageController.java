package com.example.cua_chat_app.controller;

import com.example.cua_chat_app.entity.mongo.Message;
import com.example.cua_chat_app.entity.mongo.Notification;
import com.example.cua_chat_app.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat")
    public void processMessage(@Payload Message chatMessage) {
        Message savedMsg = messageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "/queue/messages",
                new Notification(
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getContent()
                )
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<Message>> findChatMessages(@PathVariable String senderId,
                                                              @PathVariable String recipientId) {
        return ResponseEntity
                .ok(messageService.findChatMessages(senderId, recipientId));
    }
}