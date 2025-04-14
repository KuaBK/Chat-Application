import { useEffect, useState, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

export default function ChatPage() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [recipient, setRecipient] = useState('');
  const [connected, setConnected] = useState(false);
  const stompClient = useRef(null);
  const sender = localStorage.getItem('nickname');

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws');
    const client = Stomp.over(socket);
    stompClient.current = client;

    client.connect({}, () => {
      setConnected(true); // ✅ đánh dấu đã kết nối

      client.subscribe('/user/queue/messages', (msg) => {
        const notification = JSON.parse(msg.body);
        setMessages(prev => [...prev, {
          from: notification.senderId,
          content: notification.content
        }]);
      });
    }, (err) => {
      console.error('Connection error:', err);
      setConnected(false);
    });

    return () => {
      if (stompClient.current) {
        stompClient.current.disconnect();
      }
    };
  }, []);

  const sendMessage = () => {
    if (!input || !recipient) return;
    if (!connected || !stompClient.current.connected) {
      alert("Chưa kết nối tới server WebSocket.");
      return;
    }

    const message = {
      senderId: sender,
      recipientId: recipient,
      content: input
    };

    stompClient.current.send('/app/chat', {}, JSON.stringify(message));
    setMessages(prev => [...prev, { from: 'Me', content: input }]);
    setInput('');
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Xin chào, {sender}</h2>
      {!connected && <p style={{ color: 'red' }}>⛔ Chưa kết nối đến WebSocket Server</p>}
      <div>
        <input
          placeholder="Gửi đến (nickname)"
          value={recipient}
          onChange={(e) => setRecipient(e.target.value)}
          style={{ marginRight: 10 }}
        />
        <input
          placeholder="Nội dung"
          value={input}
          onChange={(e) => setInput(e.target.value)}
        />
        <button onClick={sendMessage} disabled={!connected}>Gửi</button>
      </div>

      <hr />

      <div>
        <h3>Tin nhắn</h3>
        <ul>
          {messages.map((msg, idx) => (
            <li key={idx}><strong>{msg.from}:</strong> {msg.content}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}
