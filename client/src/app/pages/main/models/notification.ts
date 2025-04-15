export interface Notification {
    chatRoomId?: string;
    content?: string;
    senderId?: string;
    receiverId?: string;
    messageType?: 'TEXT' | 'IMAGE' | 'VIDEO' | 'AUDIO';
    type?: 'SEEN' | 'MESSAGE' | 'IMAGE' | 'VIDEO' | 'AUDIO';
    chatRoomName?: string;
    media?: Array<string>;
  }