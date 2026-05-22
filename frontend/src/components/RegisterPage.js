import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';

const RegisterPage = ({ onNavigate }) => {
  const { login } = useAuth();
  const [form, setForm] = useState({ username: '', email: '', fullName: '', phone: '', password: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.username || !form.email || !form.password || !form.fullName) {
      setError('Vui lòng điền đầy đủ thông tin đăng ký.');
      return;
    }

    const result = await authAPI.register(form);
    if (!result.success) {
      setError(typeof result.error === 'string' ? result.error : 'Đăng ký thất bại.');
      return;
    }

    login({ name: result.data.username, email: result.data.email, role: result.data.role });
    setSuccess('Đăng ký thành công! Đang chuyển hướng...');
    setTimeout(() => onNavigate('medical-records'), 800);
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '2rem', background: '#0b1227' }}>
      <div style={{ width: '100%', maxWidth: 460, background: '#0f1a3a', borderRadius: 24, border: '1px solid rgba(255,255,255,0.08)', padding: '2rem' }}>
        <h2 style={{ color: '#fff', fontSize: '2rem', marginBottom: '0.75rem' }}>Tạo tài khoản mới</h2>
        <p style={{ color: '#bfc7d9', marginBottom: '1.5rem' }}>Đăng ký để quản lý bệnh án và nội dung y tế.</p>

        <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '1rem' }}>
          {error && <div style={{ color: '#ff9a9a', background: 'rgba(255, 90, 90, 0.1)', padding: '0.85rem 1rem', borderRadius: 12 }}>{error}</div>}
          {success && <div style={{ color: '#b8f7b8', background: 'rgba(90, 215, 125, 0.12)', padding: '0.85rem 1rem', borderRadius: 12 }}>{success}</div>}

          <label style={{ color: '#dde4f7' }}>
            Họ và tên
            <input
              name="fullName"
              value={form.fullName}
              onChange={handleChange}
              placeholder="Nguyễn Văn A"
              style={{ width: '100%', marginTop: '0.4rem', padding: '0.9rem 1rem', borderRadius: 14, border: '1px solid rgba(255,255,255,0.14)', background: '#14244c', color: '#fff' }}
            />
          </label>

          <label style={{ color: '#dde4f7' }}>
            Tên người dùng
            <input
              name="username"
              value={form.username}
              onChange={handleChange}
              placeholder="username"
              style={{ width: '100%', marginTop: '0.4rem', padding: '0.9rem 1rem', borderRadius: 14, border: '1px solid rgba(255,255,255,0.14)', background: '#14244c', color: '#fff' }}
            />
          </label>

          <label style={{ color: '#dde4f7' }}>
            Email
            <input
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              placeholder="email@kinedical.vn"
              style={{ width: '100%', marginTop: '0.4rem', padding: '0.9rem 1rem', borderRadius: 14, border: '1px solid rgba(255,255,255,0.14)', background: '#14244c', color: '#fff' }}
            />
          </label>

          <label style={{ color: '#dde4f7' }}>
            Số điện thoại
            <input
              name="phone"
              value={form.phone}
              onChange={handleChange}
              placeholder="0123 456 789"
              style={{ width: '100%', marginTop: '0.4rem', padding: '0.9rem 1rem', borderRadius: 14, border: '1px solid rgba(255,255,255,0.14)', background: '#14244c', color: '#fff' }}
            />
          </label>

          <label style={{ color: '#dde4f7' }}>
            Mật khẩu
            <input
              name="password"
              type="password"
              value={form.password}
              onChange={handleChange}
              placeholder="••••••••"
              style={{ width: '100%', marginTop: '0.4rem', padding: '0.9rem 1rem', borderRadius: 14, border: '1px solid rgba(255,255,255,0.14)', background: '#14244c', color: '#fff' }}
            />
          </label>

          <button type="submit" style={{ width: '100%', padding: '0.95rem', borderRadius: 16, border: 'none', background: 'linear-gradient(135deg,#3b82f6,#9333ea)', color: '#fff', fontWeight: 700, cursor: 'pointer' }}>
            Đăng ký ngay
          </button>
        </form>

        <div style={{ marginTop: '1rem', textAlign: 'center', color: '#9bb2ff' }}>
          Đã có tài khoản?{' '}
          <span style={{ color: '#88d7ff', cursor: 'pointer' }} onClick={() => onNavigate('login')}>
            Đăng nhập
          </span>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
