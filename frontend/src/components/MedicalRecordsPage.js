import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { medicalRecordAPI } from '../services/api';

// eslint-disable-next-line react/prop-types
const MedicalRecordsPage = ({ onNavigate }) => {
  const { user, isLoggedIn } = useAuth();
  const [records, setRecords] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadRecords = async () => {
      setLoading(true);
      const result = await medicalRecordAPI.getRecords();
      if (result.success) {
        setRecords(Array.isArray(result.data) ? result.data : []);
      } else {
        setError(result.error || 'Không thể tải danh sách bệnh án.');
      }
      setLoading(false);
    };

    if (isLoggedIn) {
      loadRecords();
    } else {
      setLoading(false);
    }
  }, [isLoggedIn]);

  if (!isLoggedIn) {
    return (
      <div className="page-coming-soon" style={{ padding: '2rem' }}>
        <h2>Vui lòng đăng nhập để xem bệnh án</h2>
        <p>Quản lý bệnh án chỉ dành cho người dùng đã đăng nhập.</p>
        <button className="back-btn" onClick={() => onNavigate('login')}>
          Đăng nhập ngay
        </button>
      </div>
    );
  }

  return (
    <div className="medical-records-page" style={{ padding: '2rem', maxWidth: 1100, margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h1 style={{ margin: 0 }}>Bảng điều khiển bệnh án</h1>
          <p style={{ color: '#64748b', marginTop: '0.5rem' }}>
            Xin chào, {user?.name || user?.username}. Dưới đây là danh sách bệnh án mà bạn được phép truy cập.
          </p>
        </div>
        <button className="back-btn" onClick={() => onNavigate('home')}>
          ← Về trang chủ
        </button>
      </div>

      {loading && <div>Đang tải bệnh án...</div>}
      {error && <div style={{ color: '#ff6b6b' }}>{error}</div>}

      {!loading && !error && (
        <div style={{ display: 'grid', gap: '1rem' }}>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(240px,1fr))', gap: '1rem' }}>
            <div style={{ padding: '1.3rem', borderRadius: 18, background: '#f8fafc', border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '0.95rem', color: '#334155' }}>Tổng bệnh án</div>
              <div style={{ marginTop: '0.75rem', fontSize: '2rem', fontWeight: 700 }}>{records.length}</div>
            </div>
            <div style={{ padding: '1.3rem', borderRadius: 18, background: '#f8fafc', border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '0.95rem', color: '#334155' }}>Vai trò hiện tại</div>
              <div style={{ marginTop: '0.75rem', fontSize: '1.6rem', fontWeight: 700 }}>{user?.role || 'PATIENT'}</div>
            </div>
          </div>

          <div style={{ background: '#fff', borderRadius: 24, boxShadow: '0 24px 80px rgba(15, 23, 42, 0.08)', overflow: 'hidden' }}>
            <div style={{ padding: '1.25rem 1.5rem', borderBottom: '1px solid #e2e8f0', background: '#f8fafc' }}>
              <h2 style={{ margin: 0, fontSize: '1.15rem' }}>Danh sách bệnh án</h2>
            </div>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ textAlign: 'left', color: '#475569', borderBottom: '1px solid #e2e8f0' }}>
                  <th style={{ padding: '1rem' }}>Mã bệnh án</th>
                  <th style={{ padding: '1rem' }}>Bệnh nhân</th>
                  <th style={{ padding: '1rem' }}>Chuẩn đoán</th>
                  <th style={{ padding: '1rem' }}>Ngày tạo</th>
                  <th style={{ padding: '1rem' }}>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {records.length === 0 ? (
                  <tr>
                    <td colSpan="5" style={{ padding: '1rem', color: '#64748b' }}>
                      Chưa có bệnh án nào.
                    </td>
                  </tr>
                ) : (
                  records.map((record) => (
                    <tr key={record.id} style={{ borderBottom: '1px solid #f1f5f9' }}>
                      <td style={{ padding: '1rem', fontWeight: 600 }}>{record.id || record._id || 'N/A'}</td>
                      <td style={{ padding: '1rem' }}>{record.patientName || record.patientId || 'Không rõ'}</td>
                      <td style={{ padding: '1rem' }}>{record.diagnosis || record.condition || 'Không xác định'}</td>
                      <td style={{ padding: '1rem' }}>{new Date(record.createdAt || record.updatedAt || Date.now()).toLocaleDateString('vi-VN')}</td>
                      <td style={{ padding: '1rem', color: '#2563eb', fontWeight: 600 }}>Xem</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default MedicalRecordsPage;
