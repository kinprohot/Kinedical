import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import '../styles/Sidebar.css';

const menuItems = [
  { key: 'home',            icon: '🏠', label: 'Trang chủ' },
  { key: 'explore',         icon: '🔍', label: 'Khám phá' },
  { key: 'symptoms',        icon: '🧠', label: 'Chẩn đoán AI' },
  { key: 'appointments',    icon: '📅', label: 'Lịch hẹn khám' },
  { key: 'medical-records', icon: '🗂️', label: 'Bệnh án' },
];

const Sidebar = ({ currentPage, onNavigate }) => {
  const [isOpen, setIsOpen] = useState(false);
  const { user, logout } = useAuth();

  const go = (page) => { onNavigate(page); setIsOpen(false); };

  return (
    <>
      {isOpen && (
        <div className="sidebar-overlay" onClick={() => setIsOpen(false)} />
      )}

      <button
        className={`sidebar-toggle ${isOpen ? 'open' : ''}`}
        onClick={() => setIsOpen(o => !o)}
        aria-label="Toggle sidebar"
      >
        {isOpen ? '✕' : '☰'}
      </button>

      <nav className={`sidebar ${isOpen ? 'sidebar-open' : ''}`}>
        <div className="sidebar-header">
          <span className="sidebar-logo">🏥 KINEDICAL</span>
        </div>

        <ul className="sidebar-menu">
          {menuItems.map(item => (
            <li key={item.key}>
              <button
                className={`sidebar-item ${currentPage === item.key ? 'active' : ''}`}
                onClick={() => go(item.key)}
              >
                {item.icon} {item.label}
              </button>
            </li>
          ))}

          <div className="sidebar-divider" />

          {user ? (
            <>
              <li>
                <button
                  className={`sidebar-item ${currentPage === 'profile' ? 'active' : ''}`}
                  onClick={() => go('profile')}
                >
                  👤 Hồ sơ
                </button>
              </li>
              <li>
                <button
                  className="sidebar-item sidebar-logout"
                  onClick={() => { logout(); go('home'); }}
                >
                  🚪 Đăng xuất
                </button>
              </li>
            </>
          ) : (
            <li>
              <button
                className="sidebar-item sidebar-login"
                onClick={() => go('login')}
              >
                🔐 Đăng nhập
              </button>
            </li>
          )}
        </ul>

        {user && (
          <div className="sidebar-user-info">
            <div className="sidebar-avatar">
              {(user.name || 'U')[0].toUpperCase()}
            </div>
            <span>{user.name || 'Người dùng'}</span>
          </div>
        )}
      </nav>
    </>
  );
};

export default Sidebar;
