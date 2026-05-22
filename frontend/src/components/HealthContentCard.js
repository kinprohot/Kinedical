import React, { useState } from 'react';
import { interactionAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import '../styles/HealthContentCard.css';

/**
 * Component hiển thị card thông tin bài viết sức khỏe với khả năng tương tác
 */
const HealthContentCard = ({ content }) => {
  const { isLoggedIn } = useAuth();
  const [liked, setLiked] = useState(false);
  const [saved, setSaved] = useState(false);
  const [expanded, setExpanded] = useState(false);
  const [localLikes, setLocalLikes] = useState(content.stats?.likes || 0);

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
    NUTRITION: '#00D2D3',
    DISEASE: '#FF6B6B',
    EXERCISE: '#54A0FF',
    MENTAL_HEALTH: '#FF9FF3',
    PREVENTION: '#5F27CD',
    OTHER: '#95A5A6',
  };

  const categoryColor = categoryColors[content.category] || '#95A5A6';

  const handleView = async () => {
    setExpanded(!expanded);
    if (isLoggedIn && !expanded) {
      // Log VIEW interaction (weight 1.0)
      await interactionAPI.log(content.id || content._id, 'VIEW');
    }
  };

  const handleLike = async (e) => {
    e.stopPropagation();
    if (!isLoggedIn) {
      alert('Vui lòng đăng nhập để thích bài viết.');
      return;
    }
    const newLiked = !liked;
    setLiked(newLiked);
    setLocalLikes(prev => newLiked ? prev + 1 : prev - 1);
    if (newLiked) {
      // Log LIKE interaction (weight 2.0)
      await interactionAPI.log(content.id || content._id, 'LIKE');
    }
  };

  const handleSave = async (e) => {
    e.stopPropagation();
    if (!isLoggedIn) {
      alert('Vui lòng đăng nhập để lưu bài viết.');
      return;
    }
    const newSaved = !saved;
    setSaved(newSaved);
    if (newSaved) {
      // Log SAVE interaction (weight 3.0)
      await interactionAPI.log(content.id || content._id, 'SAVE');
    }
  };

  return (
    <div className={`health-card ${expanded ? 'expanded' : ''}`}>
      <div className="card-image-container">
        <img
          src={content.featuredImage || `https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&q=80&w=400`}
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

        <p className="card-summary">{content.summary}</p>

        {expanded && (
          <div className="card-full-body fade-in">
            <div className="divider-line"></div>
            <p className="body-text">{content.body || 'Nội dung chi tiết đang được cập nhật...'}</p>
          </div>
        )}

        <div className="card-meta">
          <span className="card-author">✍️ {content.authorName}</span>
          <span className="card-date">📅 {publishDate}</span>
        </div>

        <div className="card-stats">
          <span className="stat-item">
            👁️ {content.stats?.views || 0} lượt xem
          </span>
          <span
            className={`stat-item interactive-stat ${liked ? 'active-like' : ''}`}
            onClick={handleLike}
          >
            ❤️ {localLikes} thích
          </span>
          <span
            className={`stat-item interactive-stat ${saved ? 'active-save' : ''}`}
            onClick={handleSave}
          >
            🔖 {saved ? 'Đã lưu' : 'Lưu lại'}
          </span>
        </div>

        <button className="card-button" onClick={handleView}>
          {expanded ? 'Thu gọn ↑' : 'Đọc thêm →'}
        </button>
      </div>
    </div>
  );
};

export default HealthContentCard;
