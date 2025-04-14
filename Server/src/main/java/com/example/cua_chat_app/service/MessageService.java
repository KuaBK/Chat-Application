package com.example.cua_chat_app.service;

import com.example.cua_chat_app.dto.request.MessageRequest;
import com.example.cua_chat_app.dto.response.MessageResponse;
import com.example.cua_chat_app.entity.chat.ChatRoom;
import com.example.cua_chat_app.entity.message.Message;
import com.example.cua_chat_app.entity.message.MessageState;
import com.example.cua_chat_app.entity.message.MessageType;
import com.example.cua_chat_app.entity.notification.Notification;
import com.example.cua_chat_app.entity.notification.NotificationType;
import com.example.cua_chat_app.helper.FileUtils;
import com.example.cua_chat_app.mapper.MessageMapper;
import com.example.cua_chat_app.repository.ChatRoomRepository;
import com.example.cua_chat_app.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageMapper mapper;
    private final NotificationService notificationService;
    private final FileService fileService;

    public void saveMessage(MessageRequest messageRequest) {
        ChatRoom chat = chatRoomRepository.findById(messageRequest.getChatRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setChatRoom(chat);
        message.setSenderId(messageRequest.getSenderId());
        message.setReceiverId(messageRequest.getReceiverId());
        message.setType(messageRequest.getType());
        message.setState(MessageState.SENT);

        messageRepository.save(message);

        Notification notification = Notification.builder()
                .chatRoomId(chat.getId())
                .messageType(messageRequest.getType())
                .content(messageRequest.getContent())
                .senderId(messageRequest.getSenderId())
                .receiverId(messageRequest.getReceiverId())
                .type(NotificationType.MESSAGE)
                .chatRoomName(chat.getTargetChatName(message.getSenderId()))
                .build();

        notificationService.sendNotification(messageRequest.getReceiverId(), notification);
    }

    public List<MessageResponse> findChatMessages(String chatId) {
        return messageRepository.findMessagesByChatId(chatId)
                .stream()
                .map(mapper::toMessageResponse)
                .toList();
    }

    @Transactional
    public void setMessagesToSeen(String chatId, Authentication authentication) {
        ChatRoom chat = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        final String recipientId = getRecipientId(chat, authentication);

        messageRepository.setMessagesToSeenByChatId(chatId, MessageState.SEEN);

        Notification notification = Notification.builder()
                .chatRoomId(chat.getId())
                .type(NotificationType.SEEN)
                .receiverId(recipientId)
                .senderId(getSenderId(chat, authentication))
                .build();

        notificationService.sendNotification(recipientId, notification);
    }

    public void uploadMediaMessage(String chatId, MultipartFile file, Authentication authentication) {
        ChatRoom chat = chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        final String senderId = getSenderId(chat, authentication);
        final String receiverId = getRecipientId(chat, authentication);

        final String filePath = fileService.saveFile(file, senderId);
        Message message = new Message();
        message.setReceiverId(receiverId);
        message.setSenderId(senderId);
        message.setState(MessageState.SENT);
        message.setType(MessageType.IMAGE);
        message.setMediaFilePath(filePath);
        message.setChatRoom(chat);
        messageRepository.save(message);

        Notification notification = Notification.builder()
                .chatRoomId(chat.getId())
                .type(NotificationType.IMAGE)
                .senderId(senderId)
                .receiverId(receiverId)
                .messageType(MessageType.IMAGE)
                .media(FileUtils.readFileFromLocation(filePath))
                .build();

        notificationService.sendNotification(receiverId, notification);
    }

    private String getSenderId(ChatRoom chat, Authentication authentication) {
        if (chat.getSender().getId().equals(authentication.getName())) {
            return chat.getSender().getId();
        }
        return chat.getRecipient().getId();
    }

    private String getRecipientId(ChatRoom chat, Authentication authentication) {
        if (chat.getSender().getId().equals(authentication.getName())) {
            return chat.getRecipient().getId();
        }
        return chat.getSender().getId();
    }
}