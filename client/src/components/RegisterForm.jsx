import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function RegisterForm() {
  const [form, setForm] = useState({
    email: '', password: '', fullName: '', 
  });
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:8088/account/register', form);
      alert('Đăng ký thành công!');
      navigate('/');
    } catch (error) {
      alert('Đăng ký thất bại');
    }
  };

  return (
    <form onSubmit={handleRegister}>
      <h2>Đăng ký</h2>
      <input placeholder="Họ tên" onChange={e => setForm({ ...form, fullName: e.target.value })} />
      <input placeholder="Email" onChange={e => setForm({ ...form, email: e.target.value })} />
      <input type="password" placeholder="Mật khẩu" onChange={e => setForm({ ...form, password: e.target.value })} />
      <button type="submit">Đăng ký</button>
    </form>
  );
}
