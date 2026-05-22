import React, { useState, useEffect, useRef } from 'react';

const STATS = [
  { icon: '📄', value: 10000, suffix: '+', label: 'Bệnh án điện tử' },
  { icon: '👨‍⚕️', value: 500,   suffix: '+', label: 'Bác sĩ tham gia' },
  { icon: '😊', value: 98,    suffix: '%', label: 'Bệnh nhân hài lòng' },
  { icon: '🏥', value: 120,   suffix: '+', label: 'Bệnh viện đối tác' },
];

const useCountUp = (target, duration, active) => {
  const [count, setCount] = useState(0);
  useEffect(() => {
    if (!active) return;
    let start;
    const step = (ts) => {
      if (!start) start = ts;
      const progress = Math.min((ts - start) / duration, 1);
      setCount(Math.floor(progress * target));
      if (progress < 1) requestAnimationFrame(step);
    };
    requestAnimationFrame(step);
  }, [active, target, duration]);
  return count;
};

const StatCard = ({ stat, active }) => {
  const count = useCountUp(stat.value, 1800, active);
  return (
    <div className="stat-card">
      <div className="stat-icon">{stat.icon}</div>
      <div className="stat-number">
        <span className="grad-text">
          {count.toLocaleString('vi-VN')}{stat.suffix}
        </span>
      </div>
      <div className="stat-label">{stat.label}</div>
    </div>
  );
};

const Statistics = () => {
  const [active, setActive] = useState(false);
  const sectionRef = useRef(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => { if (entry.isIntersecting) { setActive(true); observer.disconnect(); } },
      { threshold: 0.3 }
    );
    if (sectionRef.current) observer.observe(sectionRef.current);
    return () => observer.disconnect();
  }, []);

  return (
    <section className="statistics" ref={sectionRef}>
      <div className="stats-header">
        <div className="section-tag">Thống kê</div>
        <h2>Tin tưởng bởi <span className="grad-text">hàng nghìn</span> người dùng</h2>
        <p>Những con số phản ánh cam kết của chúng tôi với cộng đồng sức khỏe Việt Nam.</p>
      </div>
      <div className="stats-grid">
        {STATS.map((s, i) => <StatCard key={i} stat={s} active={active} />)}
      </div>
    </section>
  );
};

export default Statistics;
