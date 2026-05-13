import React, { useState, useEffect } from 'react';
import HealthContentCard from './HealthContentCard';
import { healthContentAPI } from '../services/api';
import '../styles/Dashboard.css';

/**
 * Component Dashboard chính - hiển thị danh sách bài viết sức khỏe gợi ý
 */
const Dashboard = () => {
  const [contentList, setContentList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('all');

  // Lấy dữ liệu từ API khi component mount
  useEffect(() => {
    fetchHealthContent();
  }, [filter]);

  const fetchHealthContent = async () => {
    setLoading(true);
    setError(null);

    try {
      let result;

      if (filter === 'all') {
        result = await healthContentAPI.getRecommendations();
      } else if (filter === 'published') {
        result = await healthContentAPI.getPublishedContent();
      } else {
        result = await healthContentAPI.getContentByCategory(filter);
      }

      if (result.success) {
        // Đảm bảo dữ liệu là mảng
        const data = Array.isArray(result.data) ? result.data : [];
        setContentList(data);
      } else {
        setError(result.error);
        setContentList([]);
      }
    } catch (err) {
      setError(
        'Có lỗi xảy ra khi tải dữ liệu. Vui lòng kiểm tra kết nối internet.'
      );
      setContentList([]);
    } finally {
      setLoading(false);
    }
  };

  // Danh sách các danh mục
  const categories = [
    { value: 'all', label: 'Tất cả' },
    { value: 'published', label: 'Đã xuất bản' },
    { value: 'CARDIOLOGY', label: 'Tim mạch' },
    { value: 'NEUROLOGY', label: 'Thần kinh' },
    { value: 'ORTHOPEDICS', label: 'Xương khớp' },
    { value: 'DERMATOLOGY', label: 'Da liễu' },
    { value: 'NUTRITION', label: 'Dinh dưỡng' },
    { value: 'MENTAL_HEALTH', label: 'Sức khỏe tâm thần' },
    { value: 'FITNESS', label: 'Thể dục' },
  ];

  return (
    <div className="dashboard-container">
      {/* Header */}
      <header className="dashboard-header">
        <div className="header-content">
          <h1>🏥 Bảng điều khiển sức khỏe</h1>
          <p>Khám phá các bài viết sức khỏe được gợi ý cho bạn</p>
        </div>
      </header>

      {/* Filter Section */}
      <div className="filter-section">
        <h3>Lọc theo danh mục:</h3>
        <div className="filter-buttons">
          {categories.map((cat) => (
            <button
              key={cat.value}
              className={`filter-btn ${filter === cat.value ? 'active' : ''}`}
              onClick={() => setFilter(cat.value)}
            >
              {cat.label}
            </button>
          ))}
        </div>
      </div>

      {/* Loading State */}
      {loading && (
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Đang tải dữ liệu sức khỏe cho bạn...</p>
        </div>
      )}

      {/* Error State */}
      {error && !loading && (
        <div className="error-container">
          <div className="error-icon">⚠️</div>
          <h3>Oops! Có lỗi xảy ra</h3>
          <p>{error}</p>
          <button className="retry-button" onClick={fetchHealthContent}>
            🔄 Thử lại
          </button>
        </div>
      )}

      {/* Empty State */}
      {!loading && !error && contentList.length === 0 && (
        <div className="empty-container">
          <div className="empty-icon">📭</div>
          <h3>Không có bài viết</h3>
          <p>
            Hiện tại không có bài viết sức khỏe trong danh mục này. Hãy thử
            danh mục khác!
          </p>
        </div>
      )}

      {/* Content Grid */}
      {!loading && !error && contentList.length > 0 && (
        <>
          <div className="content-stats">
            <p>
              Tìm thấy <strong>{contentList.length}</strong> bài viết sức khỏe
            </p>
          </div>
          <div className="content-grid">
            {contentList.map((content) => (
              <HealthContentCard
                key={content.id || content._id}
                content={content}
              />
            ))}
          </div>
        </>
      )}

      {/* Footer */}
      <footer className="dashboard-footer">
        <p>
          💡 Mẹo: Hãy thường xuyên kiểm tra các bài viết sức khỏe mới để cập
          nhật kiến thức về sức khỏe
        </p>
      </footer>
    </div>
  );
};

export default Dashboard;
