import React, { useState } from 'react';

const FOOTER_LINKS = [
  { label: 'Trang chủ',  page: 'home' },
  { label: 'Khám phá',  page: 'explore' },
  { label: 'Bệnh án',   page: 'medical-records' },
  { label: 'Đăng nhập', page: 'login' },
];

const CTAFooter = ({ onNavigate }) => {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!email) return;
    setSent(true);
    setEmail('');
    setTimeout(() => setSent(false), 3000);
  };

  return (
    <section className="cta-footer">
      {/* Ripple background */}
      <div className="ripple-bg">
        <div className="ripple-circle" />
        <div className="ripple-circle" />
        <div className="ripple-circle" />
        <div className="ripple-circle" />
      </div>

      {/* CTA content */}
      <div className="cta-content">
        <div className="section-tag">Bắt đầu ngay hôm nay</div>
        <h2>
          Bắt đầu chăm sóc sức khỏe
          <br />
          <span className="grad-text">của bạn ngay hôm nay</span>
        </h2>
        <p>
          Đăng ký nhận bản tin sức khỏe hàng tuần — những gợi ý cá nhân hóa,
          tin tức y tế mới nhất và ưu đãi đặc biệt từ KINEDICAL.
        </p>

        {sent ? (
          <div style={{
            padding: '1rem 2rem',
            background: 'rgba(0,212,100,0.15)',
            border: '1px solid rgba(0,212,100,0.3)',
            borderRadius: '50px',
            color: '#00d464',
            fontWeight: 600,
            display: 'inline-block',
          }}>
            ✅ Đăng ký thành công! Cảm ơn bạn.
          </div>
        ) : (
          <form className="email-form" onSubmit={handleSubmit}>
            <input
              type="email"
              placeholder="Nhập địa chỉ email của bạn..."
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <button type="submit">Đăng ký →</button>
          </form>
        )}
      </div>

      {/* Footer */}
      <footer className="footer">
        <div className="footer-brand">🏥 KINEDICAL</div>

        <div className="footer-links">
          {FOOTER_LINKS.map((l) => (
            <button key={l.page} onClick={() => onNavigate(l.page)}>
              {l.label}
            </button>
          ))}
        </div>

        <div className="footer-copy">
          © {new Date().getFullYear()} KINEDICAL. All rights reserved.
        </div>
      </footer>
    </section>
  );
};

export default CTAFooter;
