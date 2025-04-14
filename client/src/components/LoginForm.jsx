import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post('http://localhost:8088/auth/login', {
        email, password
      });

      // Lưu nickname hoặc token
      localStorage.setItem('nickname', res.data.nickName);

      // Chuyển đến trang chat
      navigate('/chat');
    } catch (error) {
      alert('Sai thông tin đăng nhập');
    }
  };

  return (
    <form onSubmit={handleLogin}>
      <h2>Đăng nhập</h2>
      <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" />
      <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" placeholder="Password" />
      <button type="submit">Login</button>
      <p>Chưa có tài khoản? <a href="/register">Đăng ký</a></p>
    </form>
  );
}
