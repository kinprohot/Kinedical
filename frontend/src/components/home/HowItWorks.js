import React, { useEffect, useRef } from 'react';

const STEPS = [
  {
    number: 'Bước 01',
    icon: '📝',
    label: 'Nhập chỉ số',
    title: 'Nhập thông tin & chỉ số sức khỏe',
    desc: 'Cung cấp các chỉ số cơ bản như huyết áp, nhịp tim, cân nặng, chiều cao và lịch sử bệnh lý. Hệ thống lưu trữ an toàn và có thể truy xuất bất kỳ lúc nào.',
    reverse: false,
  },
  {
    number: 'Bước 02',
    icon: '🤖',
    label: 'AI phân tích',
    title: 'AI phân tích & nhận diện nguy cơ',
    desc: 'Thuật toán Machine Learning phân tích dữ liệu của bạn, so sánh với cơ sở tri thức y tế để phát hiện các nguy cơ tiềm ẩn và đề xuất hành động phù hợp.',
    reverse: true,
  },
  {
    number: 'Bước 03',
    icon: '💊',
    label: 'Nhận gợi ý',
    title: 'Nhận phác đồ & gợi ý cá nhân hóa',
    desc: 'Hệ thống đưa ra các bài viết sức khỏe phù hợp, phác đồ điều trị từ bác sĩ và nhắc nhở định kỳ để bạn duy trì lối sống lành mạnh.',
    reverse: false,
  },
];

const HowItWorks = () => {
  const visualRefs = useRef([]);
  const textRefs   = useRef([]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('visible');
            observer.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.2 }
    );

    [...visualRefs.current, ...textRefs.current].forEach(
      (el) => { if (el) observer.observe(el); }
    );
    return () => observer.disconnect();
  }, []);

  return (
    <section className="how-it-works">
      <div className="hiw-header">
        <div className="section-tag">Cách hoạt động</div>
        <h2>Trải nghiệm <span className="grad-text">3 bước đơn giản</span></h2>
      </div>

      <div className="hiw-steps">
        {STEPS.map((step, i) => (
          <div key={i} className={`hiw-step ${step.reverse ? 'reverse' : ''}`}>
            <div
              ref={(el) => (visualRefs.current[i] = el)}
              className={`hiw-visual ${step.reverse ? 'slide-right' : 'slide-left'}`}
            >
              <div className="hiw-icon-box">
                {step.icon}
                <span>{step.label}</span>
              </div>
            </div>

            <div
              ref={(el) => (textRefs.current[i] = el)}
              className={`hiw-text ${step.reverse ? 'slide-left' : 'slide-right'}`}
            >
              <div className="step-number">{step.number}</div>
              <h3>{step.title}</h3>
              <p>{step.desc}</p>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};

export default HowItWorks;
