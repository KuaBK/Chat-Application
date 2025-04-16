import {Component, input, InputSignal, output} from '@angular/core';
import { ChatService } from '../../services/services/chat.service';
import { UserService } from '../../services/services/user.service';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';
import { UserResponse } from '../../services/models/user-response';
import { ChatRoomResponse } from '../../services/models/chat-room-response';
import {DatePipe} from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chat-list',
  imports: [
    FormsModule,
    DatePipe,
  ],
  templateUrl: './chat-list.component.html',
  styleUrl: './chat-list.component.scss'
})
export class ChatListComponent {
  chats: InputSignal<ChatRoomResponse[]> = input<ChatRoomResponse[]>([]);
  searchNewContact = false;
  contacts: Array<UserResponse> = [];
  chatSelected = output<ChatRoomResponse>();
  showUnread = false;

  constructor(
    private chatService: ChatService,
    private userService: UserService,
    private keycloakService: KeycloakService
  ) {
  }

  toggleUnread() {
    this.showUnread = !this.showUnread;
  }


  searchTerm: string = '';

  getFilteredChats(): ChatRoomResponse[] {
    const searchTermLower = this.searchTerm.toLowerCase(); 
  
    if (this.showUnread) {
      return this.chats().filter(chat =>
        chat.unreadCount && chat.unreadCount > 0 &&
        chat.name?.toLowerCase().includes(searchTermLower)
      );
    }
  
    return this.chats().filter(chat =>
      chat.name?.toLowerCase().includes(searchTermLower) // Lọc theo tên phòng chat
    );
  }

  searchContact() {
    this.userService.getAllUsers()
      .subscribe({
        next: (users) => {
          // Lấy danh sách ID của bạn bè
          const existingUserIds = this.chats().map(chat =>
            chat.senderId === this.keycloakService.userId ? chat.receiverId : chat.senderId
          );
  
          // Lọc danh sách bạn bè
          const friends = users.filter(user => existingUserIds.includes(user.id));
  
          // Lọc danh sách không phải bạn bè
          const nonFriends = users.filter(user => !existingUserIds.includes(user.id));
  
          // Lọc theo tên dựa trên searchTerm
          const searchTermLower = this.searchTerm.toLowerCase();
          const filteredFriends = friends.filter(user =>
            (user.firstName + ' ' + user.lastName).toLowerCase().includes(searchTermLower)
          );
          const filteredNonFriends = nonFriends.filter(user =>
            (user.firstName + ' ' + user.lastName).toLowerCase().includes(searchTermLower)
          );
  
          // Kết hợp cả hai danh sách
          this.contacts = [...filteredNonFriends]; // CHANGED
          this.searchNewContact = true;
        }
      });
  }


  selectContact(contact: UserResponse) {
    // Kiểm tra xem đã có phòng chat với liên hệ này chưa
    const existingChat = this.chats().find(chat =>
      (chat.senderId === this.keycloakService.userId && chat.receiverId === contact.id) ||
      (chat.receiverId === this.keycloakService.userId && chat.senderId === contact.id)
    );
  
    if (existingChat) {
      // Nếu đã có phòng chat, chỉ cần chọn phòng chat đó
      this.searchNewContact = false;
      this.chatSelected.emit(existingChat);
    } else {
      // Nếu chưa có, tạo phòng chat mới
      this.chatService.createChat({
        'sender-id': this.keycloakService.userId as string,
        'receiver-id': contact.id as string
      }).subscribe({
        next: (res) => {
          const chat: ChatRoomResponse = {
            id: res.response,
            name: contact.firstName + ' ' + contact.lastName,
            recipientOnline: contact.online,
            lastMessageTime: contact.lastSeen,
            senderId: this.keycloakService.userId,
            receiverId: contact.id
          };
          this.chats().unshift(chat);
          this.searchNewContact = false;
          this.chatSelected.emit(chat);
        }
      });
    }
  }

  chatClicked(chat: ChatRoomResponse) {
    this.chatSelected.emit(chat);
  }

  wrapMessage(lastMessage: string | undefined): string {
    if (lastMessage && lastMessage.length <= 20) {
      return lastMessage;
    }
    return lastMessage?.substring(0, 17) + '...';
  }
}