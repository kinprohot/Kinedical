import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { appointmentAPI } from '../services/api';
import '../styles/Appointments.css';

// Rich mock data for doctors to map ids and provide high-fidelity UI elements
const MOCK_DOCTORS = [
  {
    id: 'doc-001',
    name: 'BS. CKII. Nguyễn Văn An',
    specialty: 'Tim mạch & Huyết áp',
    avatar: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?auto=format&fit=crop&q=80&w=200',
    rating: '4.9',
    experience: '15 năm kinh nghiệm',
    price: '300.000đ'
  },
  {
    id: 'doc-002',
    name: 'ThS. BS. Trần Thị Bình',
    specialty: 'Tiêu hóa & Dạ dày',
    avatar: 'https://images.unsplash.com/photo-1594824813573-246434de83fb?auto=format&fit=crop&q=80&w=200',
    rating: '4.8',
    experience: '10 năm kinh nghiệm',
    price: '250.000đ'
  },
  {
    id: 'doc-003',
    name: 'BS. CKI. Lê Hoàng Nam',
    specialty: 'Thần kinh & Suy nhược',
    avatar: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&q=80&w=200',
    rating: '4.9',
    experience: '12 năm kinh nghiệm',
    price: '280.000đ'
  },
  {
    id: 'doc-004',
    name: 'BS. Phạm Minh Đức',
    specialty: 'Hô hấp & Phổi',
    avatar: 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&q=80&w=200',
    rating: '4.7',
    experience: '8 năm kinh nghiệm',
    price: '220.000đ'
  }
];

const AppointmentsPage = ({ onNavigate }) => {
  const { user, isLoggedIn } = useAuth();
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Booking Form State
  const [selectedDoctorId, setSelectedDoctorId] = useState('doc-001');
  const [appointmentDate, setAppointmentDate] = useState('');
  const [notes, setNotes] = useState('');
  const [bookingSuccess, setBookingSuccess] = useState(false);

  // Pre-fill State from AI Symptom Assessment
  const [preFillData, setPreFillData] = useState(null);

  useEffect(() => {
    if (isLoggedIn) {
      fetchAppointments();
      checkPreFill();
    }
  }, [isLoggedIn]);

  const fetchAppointments = async () => {
    setLoading(true);
    const res = await appointmentAPI.getBookings();
    if (res.success) {
      setAppointments(res.data);
    } else {
      setError(res.error);
    }
    setLoading(false);
  };

  const checkPreFill = () => {
    const rawData = sessionStorage.getItem('PRE_FILL_DIAGNOSIS');
    if (rawData) {
      try {
        const parsed = JSON.parse(rawData);
        setPreFillData(parsed);
        
        // Auto-fill form fields
        setNotes(`[Tự động điền từ Chẩn đoán AI]\nTriệu chứng: ${parsed.symptoms}\nKết quả chẩn đoán: ${parsed.disease}\nĐề xuất: ${parsed.recommendation}`);
        
        // Smart specialty recommendation mapping
        if (parsed.disease.includes('Tim') || parsed.disease.includes('ngực')) {
          setSelectedDoctorId('doc-001'); // Dr. An
        } else if (parsed.disease.includes('tiêu') || parsed.disease.includes('dạ dày') || parsed.disease.includes('bụng')) {
          setSelectedDoctorId('doc-002'); // Dr. Bình
        } else if (parsed.disease.includes('thần kinh') || parsed.disease.includes('đầu') || parsed.disease.includes('lo âu')) {
          setSelectedDoctorId('doc-003'); // Dr. Nam
        } else if (parsed.disease.includes('hô hấp') || parsed.disease.includes('Cúm') || parsed.disease.includes('ho')) {
          setSelectedDoctorId('doc-004'); // Dr. Đức
        }
      } catch (e) {
        console.error('Failed to parse pre-fill data', e);
      }
    }
  };

  const clearPreFill = () => {
    sessionStorage.removeItem('PRE_FILL_DIAGNOSIS');
    setPreFillData(null);
    setNotes('');
  };

  const handleBookingSubmit = async (e) => {
    e.preventDefault();
    if (!appointmentDate) {
      alert('Vui lòng chọn thời gian hẹn khám.');
      return;
    }

    setLoading(true);
    // Convert to ISO string for backend Instant parsing
    const isoDate = new Date(appointmentDate).toISOString();
    const res = await appointmentAPI.book(selectedDoctorId, isoDate, notes);

    if (res.success) {
      setBookingSuccess(true);
      setNotes('');
      setAppointmentDate('');
      sessionStorage.removeItem('PRE_FILL_DIAGNOSIS');
      setPreFillData(null);
      fetchAppointments(); // refresh lists
      
      // Auto-hide success banner
      setTimeout(() => setBookingSuccess(false), 5000);
    } else {
      setError(res.error);
    }
    setLoading(false);
  };

  const handleStatusUpdate = async (id, status) => {
    if (window.confirm(`Bạn có chắc chắn muốn ${status === 'CANCELED' ? 'HỦY' : 'XÁC NHẬN'} lịch hẹn này?`)) {
      setLoading(true);
      const res = await appointmentAPI.updateStatus(id, status);
      if (res.success) {
        fetchAppointments();
      } else {
        alert('Cập nhật thất bại: ' + res.error);
      }
      setLoading(false);
    }
  };

  const getDoctorDetails = (id) => {
    return MOCK_DOCTORS.find(d => d.id === id) || {
      name: 'Bác sĩ Kinedical',
      specialty: 'Đa khoa',
      avatar: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?auto=format&fit=crop&q=80&w=200',
      rating: '4.8',
      experience: 'Liên hệ',
      price: 'Đang cập nhật'
    };
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'CONFIRMED': return 'badge-success';
      case 'CANCELED': return 'badge-danger';
      case 'PENDING':
      default: return 'badge-warning';
    }
  };

  const translateStatus = (status) => {
    switch (status) {
      case 'CONFIRMED': return 'Đã xác nhận';
      case 'CANCELED': return 'Đã hủy';
      case 'PENDING':
      default: return 'Đang xử lý';
    }
  };

  if (!isLoggedIn) {
    return (
      <div className="appointments-container unauthenticated">
        <div className="glass-card auth-prompt-card">
          <div className="icon-glow">📅</div>
          <h2>Đặt lịch hẹn khám</h2>
          <p>
            Vui lòng đăng nhập vào tài khoản của bạn để quản lý và đặt lịch khám với các bác sĩ chuyên khoa hàng đầu.
          </p>
          <button className="premium-btn" onClick={() => onNavigate('login')}>
            🔐 Đăng nhập ngay
          </button>
        </div>
      </div>
    );
  }

  const isDoctor = user.role === 'DOCTOR';

  return (
    <div className="appointments-container">
      <header className="appointments-header">
        <h1>📅 {isDoctor ? 'Quản lý lịch hẹn khám' : 'Lịch hẹn khám bệnh'}</h1>
        <p>{isDoctor ? 'Theo dõi và quản lý lịch khám của bệnh nhân dành cho bác sĩ' : 'Đặt lịch hẹn khám trực tuyến với các bác sĩ chuyên khoa đầu ngành'}</p>
      </header>

      {/* Patient view - Scheduling form and user appointments */}
      {!isDoctor ? (
        <div className="appointments-grid">
          {/* Booking Form */}
          <div className="glass-card booking-form-section">
            <h3>📝 Tạo lịch hẹn khám mới</h3>
            <p className="card-subtitle">Hoàn thành mẫu dưới đây để đặt chỗ ưu tiên.</p>

            {preFillData && (
              <div className="prefill-notice fade-in">
                <div className="prefill-text">
                  <strong>💡 Gợi ý chuyên khoa:</strong> Bác sĩ chuyên môn đã được tự động gợi ý dựa trên chẩn đoán triệu chứng AI (<strong>{preFillData.disease}</strong>) của bạn.
                </div>
                <button className="clear-prefill" onClick={clearPreFill}>✕ Hủy gợi ý</button>
              </div>
            )}

            {bookingSuccess && (
              <div className="success-banner fade-in">
                🎉 Đăng ký lịch hẹn thành công! Vui lòng chờ bác sĩ duyệt lịch hẹn.
              </div>
            )}

            {error && (
              <div className="error-banner fade-in">
                ⚠️ Lỗi: {error}
              </div>
            )}

            <form onSubmit={handleBookingSubmit}>
              {/* Doctor Selection */}
              <div className="form-group">
                <label className="form-label">👨‍⚕️ Chọn bác sĩ chuyên khoa:</label>
                <div className="doctor-cards-selector">
                  {MOCK_DOCTORS.map(doc => (
                    <div
                      key={doc.id}
                      className={`doctor-select-card ${selectedDoctorId === doc.id ? 'selected' : ''}`}
                      onClick={() => setSelectedDoctorId(doc.id)}
                    >
                      <img src={doc.avatar} alt={doc.name} className="doc-avatar" />
                      <div className="doc-info">
                        <h4>{doc.name}</h4>
                        <p className="doc-spec">{doc.specialty}</p>
                        <div className="doc-meta">
                          <span>⭐ {doc.rating}</span>
                          <span>•</span>
                          <span>{doc.price}</span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Datepicker */}
              <div className="form-group">
                <label className="form-label" htmlFor="appt-date">⏰ Chọn ngày & giờ hẹn khám:</label>
                <input
                  type="datetime-local"
                  id="appt-date"
                  className="premium-input"
                  required
                  value={appointmentDate}
                  onChange={(e) => setAppointmentDate(e.target.value)}
                />
              </div>

              {/* Notes */}
              <div className="form-group">
                <label className="form-label" htmlFor="appt-notes">✏️ Ghi chú triệu chứng:</label>
                <textarea
                  id="appt-notes"
                  className="premium-textarea"
                  rows={4}
                  placeholder="Nhập ghi chú cho bác sĩ (ví dụ: tiền sử bệnh lý, thuốc đang sử dụng...)"
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                />
              </div>

              <button type="submit" className="premium-btn submit-booking-btn" disabled={loading}>
                {loading ? 'Đang gửi đăng ký...' : '🚀 Xác nhận đặt lịch hẹn'}
              </button>
            </form>
          </div>

          {/* Bookings List */}
          <div className="glass-card bookings-list-section">
            <h3>📋 Lịch hẹn đã đặt</h3>
            <p className="card-subtitle">Theo dõi trạng thái lịch hẹn khám của bạn.</p>

            {appointments.length === 0 ? (
              <div className="empty-bookings">
                <div className="empty-icon">📅</div>
                <h4>Không có lịch hẹn nào</h4>
                <p>Bạn chưa đặt lịch hẹn khám nào gần đây.</p>
              </div>
            ) : (
              <div className="bookings-list">
                {appointments.map(appt => {
                  const doc = getDoctorDetails(appt.doctorId);
                  return (
                    <div key={appt.id} className="booking-card fade-in">
                      <div className="booking-card-header">
                        <img src={doc.avatar} alt={doc.name} className="booking-doc-avatar" />
                        <div className="booking-doc-desc">
                          <h4>{doc.name}</h4>
                          <p>{doc.specialty}</p>
                        </div>
                        <span className={`status-badge ${getStatusBadgeClass(appt.status)}`}>
                          {translateStatus(appt.status)}
                        </span>
                      </div>
                      
                      <div className="booking-details">
                        <div className="detail-row">
                          <span className="detail-label">⏰ Thời gian:</span>
                          <span className="detail-val">
                            {new Date(appt.appointmentDate).toLocaleString('vi-VN', {
                              weekday: 'long',
                              year: 'numeric',
                              month: 'long',
                              day: 'numeric',
                              hour: '2-digit',
                              minute: '2-digit'
                            })}
                          </span>
                        </div>
                        {appt.notes && (
                          <div className="detail-notes">
                            <strong>Ghi chú:</strong>
                            <p>{appt.notes}</p>
                          </div>
                        )}
                      </div>

                      {appt.status !== 'CANCELED' && (
                        <div className="booking-actions">
                          <button
                            className="cancel-appt-btn"
                            onClick={() => handleStatusUpdate(appt.id, 'CANCELED')}
                            disabled={loading}
                          >
                            Hủy lịch hẹn
                          </button>
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </div>
      ) : (
        /* Doctor View - Admin list and confirmations */
        <div className="glass-card doctor-dashboard-card">
          <h3>🩺 Lịch bệnh nhân hẹn khám của bác sĩ</h3>
          <p className="card-subtitle">Xem chi tiết các yêu cầu đặt lịch khám và xác nhận.</p>

          {appointments.length === 0 ? (
            <div className="empty-bookings">
              <div className="empty-icon">🏥</div>
              <h4>Không có yêu cầu nào</h4>
              <p>Hiện chưa có bệnh nhân nào đặt lịch hẹn với bác sĩ.</p>
            </div>
          ) : (
            <div className="doctor-appointments-table-container">
              <table className="doctor-table">
                <thead>
                  <tr>
                    <th>Bệnh nhân</th>
                    <th>Thời gian hẹn</th>
                    <th>Ghi chú lâm sàng</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {appointments.map(appt => (
                    <tr key={appt.id} className="table-row fade-in">
                      <td>
                        <div className="patient-cell">
                          <div className="patient-avatar">
                            {(appt.patientId || 'P').substring(0, 3).toUpperCase()}
                          </div>
                          <div>
                            <strong>Bệnh nhân ID: {appt.patientId}</strong>
                          </div>
                        </div>
                      </td>
                      <td>
                        <div className="time-cell">
                          {new Date(appt.appointmentDate).toLocaleString('vi-VN', {
                            day: '2-digit',
                            month: '2-digit',
                            year: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit'
                          })}
                        </div>
                      </td>
                      <td>
                        <div className="notes-cell" title={appt.notes}>
                          {appt.notes || 'Không có ghi chú'}
                        </div>
                      </td>
                      <td>
                        <span className={`status-badge ${getStatusBadgeClass(appt.status)}`}>
                          {translateStatus(appt.status)}
                        </span>
                      </td>
                      <td>
                        {appt.status === 'PENDING' ? (
                          <div className="action-buttons">
                            <button
                              className="action-btn confirm"
                              onClick={() => handleStatusUpdate(appt.id, 'CONFIRMED')}
                            >
                              ✓ Xác nhận
                            </button>
                            <button
                              className="action-btn cancel"
                              onClick={() => handleStatusUpdate(appt.id, 'CANCELED')}
                            >
                              ✕ Hủy lịch
                            </button>
                          </div>
                        ) : appt.status === 'CONFIRMED' ? (
                          <button
                            className="action-btn cancel outline"
                            onClick={() => handleStatusUpdate(appt.id, 'CANCELED')}
                          >
                            ✕ Hủy lịch
                          </button>
                        ) : (
                          <span className="no-action-text">-</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default AppointmentsPage;
