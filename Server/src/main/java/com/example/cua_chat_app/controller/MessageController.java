package com.example.cua_chat_app.controller;

import com.example.cua_chat_app.entity.message.Message;
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
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        Message savedMessage = messageService.save(message);
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/messages",
                null
        );
    }

    @GetMapping("/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<Message>> findMessages(@PathVariable("senderId") String senderId,
                                                      @PathVariable("receiverId") String receiverId)
    {
        return ResponseEntity.ok(messageService.findAllMessage(senderId, receiverId));
    }
}
