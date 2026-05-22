import React, { useEffect, useRef } from 'react';

const FEATURES = [
  {
    key: 'medical-records',
    icon: '📋',
    title: 'Hồ sơ bệnh án điện tử',
    desc: 'Lưu trữ và truy xuất hồ sơ y tế an toàn, có cấu trúc. Bác sĩ cập nhật, bệnh nhân theo dõi mọi lúc mọi nơi.',
  },
  {
    key: 'explore',
    icon: '🤖',
    title: 'Gợi ý bài viết AI',
    desc: 'Thuật toán AI phân tích hành vi và hồ sơ sức khỏe để đề xuất nội dung y tế phù hợp nhất với bạn.',
  },
  {
    key: 'symptoms',
    icon: '🧠',
    title: 'Chẩn đoán triệu chứng AI',
    desc: 'Phân tích triệu chứng bằng Trí tuệ Nhân tạo thông minh, đưa ra dự đoán bệnh lý nhanh chóng và đề xuất hướng xử lý.',
  },
  {
    key: 'appointments',
    icon: '📅',
    title: 'Đặt lịch khám',
    desc: 'Đặt hẹn trực tuyến với bác sĩ chuyên khoa nhanh chóng, nhận nhắc nhở tự động trước mỗi buổi khám.',
  },
];

const FeatureShowcase = ({ onNavigate }) => {
  const cardRefs = useRef([]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            const el = entry.target;
            const delay = Number(el.dataset.delay || 0);
            setTimeout(() => el.classList.add('revealed'), delay);
            observer.unobserve(el);
          }
        });
      },
      { threshold: 0.15 }
    );

    cardRefs.current.forEach((el) => { if (el) observer.observe(el); });
    return () => observer.disconnect();
  }, []);

  return (
    <section className="features">
      <div className="features-header">
        <div className="section-tag">Tính năng</div>
        <h2>Bên trong <span className="grad-text">KINEDICAL</span> có gì?</h2>
        <p>Bộ công cụ toàn diện giúp bạn chủ động chăm sóc sức khỏe mỗi ngày.</p>
      </div>

      <div className="features-grid">
        {FEATURES.map((f, i) => (
          <div
            key={i}
            className="feature-card"
            ref={(el) => (cardRefs.current[i] = el)}
            data-delay={i * 120}
            onClick={() => onNavigate && onNavigate(f.key)}
            style={{ cursor: 'pointer' }}
          >
            <div className="feature-icon">{f.icon}</div>
            <h3>{f.title}</h3>
            <p>{f.desc}</p>
          </div>
        ))}
      </div>
    </section>
  );
};

export default FeatureShowcase;
