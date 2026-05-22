import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';

// eslint-disable-next-line react/prop-types
const LoginPage = ({ onNavigate }) => {
  const { login } = useAuth();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');

  const handleChange = (e) =>
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.email || !form.password) {
      setError('Vui lòng nhập đầy đủ thông tin.');
      return;
    }

    const result = await authAPI.login(form.email, form.password);
    if (!result.success) {
      setError(typeof result.error === 'string' ? result.error : 'Đăng nhập không thành công.');
      return;
    }

    login({
      name: result.data.username || form.email,
      email: result.data.email,
      role: result.data.role,
    });
    onNavigate('explore');
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(-45deg,#060614,#0a0a2e,#0d1b50,#060614)',
      backgroundSize: '400% 400%',
      animation: 'heroGrad 10s ease infinite',
      padding: '2rem',
      paddingRight: '80px',
    }}>
      <style>{`
        @keyframes heroGrad {
          0%{background-position:0% 50%}
          50%{background-position:100% 50%}
          100%{background-position:0% 50%}
        }
      `}</style>

      <div style={{
        width: '100%',
        maxWidth: 420,
        background: 'rgba(255,255,255,0.05)',
        backdropFilter: 'blur(20px)',
        border: '1px solid rgba(255,255,255,0.1)',
        borderRadius: 24,
        padding: '2.5rem 2rem',
      }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>🏥</div>
          <h2 style={{ fontSize: '1.6rem', fontWeight: 800, marginBottom: '0.4rem' }}>
            Chào mừng trở lại
          </h2>
          <p style={{ color: 'rgba(255,255,255,0.5)', fontSize: '0.9rem' }}>
            Đăng nhập vào tài khoản KINEDICAL
          </p>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {error && (
            <div style={{
              padding: '0.75rem 1rem',
              background: 'rgba(255,60,60,0.12)',
              border: '1px solid rgba(255,60,60,0.25)',
              borderRadius: 10,
              color: '#ff8080',
              fontSize: '0.88rem',
            }}>
              ⚠️ {error}
            </div>
          )}

          {['email', 'password'].map((field) => (
            <div key={field}>
              <label style={{ display: 'block', fontSize: '0.85rem', color: 'rgba(255,255,255,0.6)', marginBottom: '0.4rem' }}>
                {field === 'email' ? '📧 Email' : '🔒 Mật khẩu'}
              </label>
              <input
                type={field === 'password' ? 'password' : 'email'}
                name={field}
                placeholder={field === 'email' ? 'email@kinedical.vn' : '••••••••'}
                value={form[field]}
                onChange={handleChange}
                style={{
                  width: '100%',
                  padding: '0.85rem 1rem',
                  background: 'rgba(255,255,255,0.06)',
                  border: '1px solid rgba(255,255,255,0.1)',
                  borderRadius: 12,
                  color: '#fff',
                  fontFamily: 'Inter, sans-serif',
                  fontSize: '0.95rem',
                  outline: 'none',
                }}
              />
            </div>
          ))}

          <button
            type="submit"
            style={{
              marginTop: '0.5rem',
              padding: '0.9rem',
              background: 'linear-gradient(135deg,#0066ff,#7c3aed)',
              color: '#fff',
              border: 'none',
              borderRadius: 50,
              fontSize: '1rem',
              fontWeight: 700,
              cursor: 'pointer',
              fontFamily: 'Inter, sans-serif',
            }}
          >
            Đăng nhập →
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: '1.5rem', color: 'rgba(255,255,255,0.4)', fontSize: '0.85rem' }}>
          Chưa có tài khoản?{' '}
          <button
            type="button"
            onClick={() => onNavigate('register')}
            style={{
              display: 'inline-block',
              border: 'none',
              background: 'transparent',
              color: '#00d4ff',
              cursor: 'pointer',
              fontWeight: 600,
              padding: 0,
              font: 'inherit',
            }}
          >
            Đăng ký ngay
          </button>
        </p>

        {/* Demo hint */}
        <div style={{
          marginTop: '1rem',
          padding: '0.6rem 1rem',
          background: 'rgba(0,212,255,0.07)',
          border: '1px solid rgba(0,212,255,0.15)',
          borderRadius: 8,
          fontSize: '0.78rem',
          color: 'rgba(0,212,255,0.7)',
          textAlign: 'center',
        }}>
          💡 Sử dụng tài khoản đã đăng ký hoặc tài khoản admin thử nghiệm để đăng nhập.
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
