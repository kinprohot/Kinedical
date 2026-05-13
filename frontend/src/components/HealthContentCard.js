import React from 'react';
import '../styles/HealthContentCard.css';

/**
 * Component hiển thị card thông tin bài viết sức khỏe
 */
const HealthContentCard = ({ content }) => {
  // Lấy ngày xuất bản
  const publishDate = content.publishDate 
    ? new Date(content.publishDate).toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
      })
    : 'Không xác định';

  // Màu badge theo danh mục
  const categoryColors = {
    CARDIOLOGY: '#FF6B6B',
    NEUROLOGY: '#4ECDC4',
    ORTHOPEDICS: '#45B7D1',
    DERMATOLOGY: '#F7B731',
    GENERAL: '#5F27CD',
    NUTRITION: '#00D2D3',
    MENTAL_HEALTH: '#FF9FF3',
    FITNESS: '#54A0FF',
  };

  const categoryColor = categoryColors[content.category] || '#95A5A6';

  return (
    <div className="health-card">
      <div className="card-image-container">
        <img
          src={content.imageUrl || 'https://via.placeholder.com/400x300?text=Sức+Khỏe'}
          alt={content.title}
          className="card-image"
        />
        <span
          className="card-badge"
          style={{ backgroundColor: categoryColor }}
        >
          {content.category}
        </span>
      </div>

      <div className="card-content">
        <h3 className="card-title">{content.title}</h3>

        <p className="card-summary">{content.content}</p>

        <div className="card-meta">
          <span className="card-author">✍️ {content.author}</span>
          <span className="card-date">📅 {publishDate}</span>
        </div>

        <div className="card-stats">
          <span className="stat-item">
            👁️ {content.stats?.views || 0} lượt xem
          </span>
          <span className="stat-item">
            ❤️ {content.stats?.likes || 0} yêu thích
          </span>
          <span className="stat-item">
            💬 {content.stats?.comments || 0} bình luận
          </span>
        </div>

        <button className="card-button">Đọc thêm →</button>
      </div>
    </div>
  );
};

export default HealthContentCard;
