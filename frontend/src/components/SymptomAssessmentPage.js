import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { symptomAPI } from '../services/api';
import '../styles/SymptomAssessment.css';

const QUICK_TAGS = [
  { text: 'Sốt', search: 'sốt' },
  { text: 'Ho', search: 'ho' },
  { text: 'Đau ngực', search: 'đau ngực' },
  { text: 'Khó thở', search: 'khó thở' },
  { text: 'Đau đầu', search: 'đau đầu' },
  { text: 'Mất ngủ', search: 'mất ngủ' },
  { text: 'Đau bụng', search: 'đau bụng' },
  { text: 'Buồn nôn', search: 'buồn nôn' },
  { text: 'Tiêu chảy', search: 'tiêu chảy' },
  { text: 'Stress & Căng thẳng', search: 'stress' },
  { text: 'Sổ mũi & Đau họng', search: 'sổ mũi' }
];

const SymptomAssessmentPage = ({ onNavigate }) => {
  const { isLoggedIn } = useAuth();
  const [symptoms, setSymptoms] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [diagnosisResult, setDiagnosisResult] = useState(null);
  const [history, setHistory] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(false);

  // Fetch assessment history on mount
  useEffect(() => {
    if (isLoggedIn) {
      fetchHistory();
    }
  }, [isLoggedIn]);

  const fetchHistory = async () => {
    setHistoryLoading(true);
    const res = await symptomAPI.getHistory();
    if (res.success) {
      setHistory(res.data);
    }
    setHistoryLoading(false);
  };

  const handleTagClick = (tag) => {
    if (symptoms.trim() === '') {
      setSymptoms(tag.text);
    } else if (!symptoms.toLowerCase().includes(tag.search.toLowerCase())) {
      setSymptoms(prev => `${prev}, ${tag.text.toLowerCase()}`);
    }
  };

  const handleAnalyze = async (e) => {
    e.preventDefault();
    if (!symptoms.trim()) return;

    setLoading(true);
    setError(null);
    setDiagnosisResult(null);

    // Artificial delay to show high-end analytical loading animation
    setTimeout(async () => {
      const res = await symptomAPI.analyze(symptoms);
      if (res.success) {
        setDiagnosisResult(res.data);
        fetchHistory(); // refresh history
      } else {
        setError(res.error);
      }
      setLoading(false);
    }, 1500);
  };

  const handleBookDeepLink = () => {
    if (diagnosisResult) {
      // Store diagnostic details to pre-populate booking form
      sessionStorage.setItem('PRE_FILL_DIAGNOSIS', JSON.stringify({
        disease: diagnosisResult.predictedDiseases,
        symptoms: diagnosisResult.reportedSymptoms,
        recommendation: diagnosisResult.aiRecommendations?.[0] || ''
      }));
    }
    onNavigate('appointments');
  };

  if (!isLoggedIn) {
    return (
      <div className="symptoms-container unauthenticated">
        <div className="glass-card auth-prompt-card">
          <div className="icon-glow">🧠</div>
          <h2>Chẩn đoán triệu chứng AI</h2>
          <p>
            Vui lòng đăng nhập vào tài khoản của bạn để truy cập tính năng chẩn đoán sức khỏe thông minh bằng Trí tuệ Nhân tạo.
          </p>
          <button className="premium-btn" onClick={() => onNavigate('login')}>
            🔐 Đăng nhập ngay
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="symptoms-container">
      <header className="symptoms-header">
        <h1>🧠 Chẩn đoán bệnh lý AI</h1>
        <p>Phân tích triệu chứng tức thì hỗ trợ bởi Trí tuệ Nhân tạo</p>
      </header>

      <div className="symptoms-grid">
        {/* Input Card */}
        <div className="glass-card input-card">
          <h3>✍️ Khai báo triệu chứng của bạn</h3>
          <p className="card-subtitle">Hãy mô tả chi tiết sức khỏe hiện tại của bạn bằng tiếng Việt hoặc tiếng Anh.</p>
          
          <form onSubmit={handleAnalyze}>
            <textarea
              className="symptoms-textarea"
              placeholder="Ví dụ: Tôi cảm thấy bị sốt cao kèm theo ho khan, sổ mũi và đau đầu từ ngày hôm qua..."
              value={symptoms}
              onChange={(e) => setSymptoms(e.target.value)}
              rows={5}
              maxLength={500}
            />
            
            <div className="char-count">{symptoms.length}/500</div>

            {/* Quick tags */}
            <div className="quick-tags-section">
              <span className="tags-label">🏷️ Chọn nhanh triệu chứng:</span>
              <div className="tags-container">
                {QUICK_TAGS.map((tag, idx) => (
                  <button
                    key={idx}
                    type="button"
                    className="tag-badge"
                    onClick={() => handleTagClick(tag)}
                  >
                    + {tag.text}
                  </button>
                ))}
              </div>
            </div>

            <button
              type="submit"
              className={`premium-btn analyze-btn ${loading ? 'loading' : ''}`}
              disabled={loading || !symptoms.trim()}
            >
              {loading ? (
                <>
                  <div className="loading-spinner"></div>
                  Đang phân tích triệu chứng...
                </>
              ) : (
                '🔍 Tiến hành chẩn đoán AI'
              )}
            </button>
          </form>

          {error && <div className="symptoms-error">⚠️ Lỗi: {error}</div>}
        </div>

        {/* Results Card */}
        <div className="glass-card results-card">
          {!loading && !diagnosisResult && (
            <div className="empty-results">
              <div className="pulse-circle">🤖</div>
              <h4>Đang đợi khai báo</h4>
              <p>Mô tả các triệu chứng ở bảng bên trái và bấm nút phân tích để nhận kết quả chẩn đoán từ AI.</p>
            </div>
          )}

          {loading && (
            <div className="loading-results">
              <div className="scanner-line"></div>
              <div className="pulse-circle analyzing">🔬</div>
              <h4>Đang phân tích chuyên sâu...</h4>
              <p>AI đang quét cơ sở dữ liệu y tế, tính toán xác suất bệnh lý và tổng hợp lời khuyên lâm sàng...</p>
            </div>
          )}

          {!loading && diagnosisResult && (
            <div className="results-content fade-in">
              <div className="results-badge">KẾT QUẢ PHÂN TÍCH</div>
              
              <div className="predicted-disease-section">
                <h2>{diagnosisResult.predictedDiseases}</h2>
                <div className="confidence-wrapper">
                  <span className="confidence-label">Độ tin cậy:</span>
                  <div className="progress-bar-bg">
                    <div
                      className="progress-bar-fill"
                      style={{
                        width: `${diagnosisResult.confidenceScore * 100}%`,
                        backgroundColor: diagnosisResult.confidenceScore > 0.8 ? '#10b981' : '#f59e0b'
                      }}
                    >
                      <span className="progress-text">{(diagnosisResult.confidenceScore * 100).toFixed(0)}%</span>
                    </div>
                  </div>
                </div>
              </div>

              <div className="recommendations-section">
                <h4>📋 Đề xuất chăm sóc từ AI:</h4>
                <ul className="rec-list">
                  {diagnosisResult.aiRecommendations?.map((rec, idx) => (
                    <li key={idx}>
                      <span className="bullet-icon">🩺</span>
                      <p>{rec}</p>
                    </li>
                  ))}
                </ul>
              </div>

              <button className="premium-btn book-now-btn" onClick={handleBookDeepLink}>
                📅 Đặt lịch hẹn khám với bác sĩ chuyên khoa
              </button>
            </div>
          )}
        </div>
      </div>

      {/* History Timeline */}
      <div className="glass-card history-section">
        <h3>🕰️ Lịch sử chẩn đoán của bạn</h3>
        <p className="card-subtitle font-sm">Danh sách các lần chẩn đoán sức khỏe trước đây của bạn.</p>

        {historyLoading ? (
          <div className="history-loading">
            <div className="loading-spinner"></div>
            Đang tải lịch sử...
          </div>
        ) : history.length === 0 ? (
          <div className="empty-history">
            <p>Bạn chưa thực hiện lần chẩn đoán triệu chứng nào.</p>
          </div>
        ) : (
          <div className="history-timeline">
            {history.map((record) => (
              <div key={record.id} className="timeline-item" onClick={() => setDiagnosisResult(record)}>
                <div className="timeline-marker"></div>
                <div className="timeline-content">
                  <div className="timeline-meta">
                    <span className="timeline-date">
                      {new Date(record.createdAt).toLocaleString('vi-VN', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                      })}
                    </span>
                    <span className="timeline-score">
                      {(record.confidenceScore * 100).toFixed(0)}% tin cậy
                    </span>
                  </div>
                  <h4>{record.predictedDiseases}</h4>
                  <p className="timeline-symptoms">Triệu chứng: {record.reportedSymptoms}</p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default SymptomAssessmentPage;
