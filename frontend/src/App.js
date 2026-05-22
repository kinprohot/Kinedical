import React, { useState } from 'react';
import { AuthProvider } from './context/AuthContext';
import Sidebar from './components/Sidebar';
import HomePage from './components/home/HomePage';
import LoginPage from './components/LoginPage';
import RegisterPage from './components/RegisterPage';
import MedicalRecordsPage from './components/MedicalRecordsPage';
import Dashboard from './components/Dashboard';
import SymptomAssessmentPage from './components/SymptomAssessmentPage';
import AppointmentsPage from './components/AppointmentsPage';
import './App.css';

// eslint-disable-next-line react/prop-types
const ComingSoon = ({ title, icon, onNavigate }) => (
  <div className="page-coming-soon">
    <div style={{ fontSize: '4rem' }}>{icon}</div>
    <h2>{title}</h2>
    <p>Tính năng đang được phát triển, sắp ra mắt!</p>
    <button className="back-btn" onClick={() => onNavigate('home')}>
      ← Về trang chủ
    </button>
  </div>
);

function AppContent() {
  const [currentPage, setCurrentPage] = useState('home');

  const renderPage = () => {
    switch (currentPage) {
      case 'home':
        return <HomePage onNavigate={setCurrentPage} />;
      case 'login':
        return <LoginPage onNavigate={setCurrentPage} />;
      case 'register':
        return <RegisterPage onNavigate={setCurrentPage} />;
      case 'explore':
        return <Dashboard />;
      case 'symptoms':
        return <SymptomAssessmentPage onNavigate={setCurrentPage} />;
      case 'appointments':
        return <AppointmentsPage onNavigate={setCurrentPage} />;
      case 'medical-records':
        return <MedicalRecordsPage onNavigate={setCurrentPage} />;
      case 'profile':
        return <ComingSoon title="Hồ sơ cá nhân" icon="👤" onNavigate={setCurrentPage} />;
      default:
        return <HomePage onNavigate={setCurrentPage} />;
    }
  };

  return (
    <div className="app-layout">
      {renderPage()}
      <Sidebar currentPage={currentPage} onNavigate={setCurrentPage} />
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
