import React from 'react';

const HeroSection = ({ onNavigate }) => (
  <section className="hero">
    <div className="hero-content">
      <div className="hero-badge">✨ Nền tảng y tế thông minh</div>

      <h1>
        <span className="grad-text">KINEDICAL</span>
        <br />
        Quản lý y tế thông minh
        <br />
        &amp; Cá nhân hóa
      </h1>

      <p>
        Hệ thống quản lý hồ sơ bệnh án điện tử, gợi ý sức khỏe cá nhân hóa
        bằng AI và kết nối bệnh nhân – bác sĩ một cách liền mạch.
      </p>

      <div className="hero-buttons">
        <button className="btn-primary" onClick={() => onNavigate('login')}>
          🔐 Đăng nhập ngay
        </button>
        <button className="btn-secondary" onClick={() => onNavigate('explore')}>
          🔍 Khám phá ngay
        </button>
      </div>
    </div>

    <div className="hero-scroll-hint">
      <span>↓</span>
      Cuộn để khám phá
    </div>
  </section>
);

export default HeroSection;
