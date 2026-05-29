import axios from 'axios';

// Cấu hình base URL cho API backend
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const AUTH_TOKEN_KEY = 'KINEDICAL_AUTH_TOKEN';
const AUTH_USER_KEY = 'KINEDICAL_AUTH_USER';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

const setAuthorizationHeader = (token) => {
  if (token) {
    apiClient.defaults.headers.common.Authorization = `Bearer ${token}`;
  } else {
    delete apiClient.defaults.headers.common.Authorization;
  }
};

const storedToken = localStorage.getItem(AUTH_TOKEN_KEY);
if (storedToken) {
  setAuthorizationHeader(storedToken);
}

export const authAPI = {
  login: async (email, password) => {
    try {
      const response = await apiClient.post('/auth/login', { email, password });
      const authData = response.data;
      localStorage.setItem(AUTH_TOKEN_KEY, authData.token);
      localStorage.setItem(AUTH_USER_KEY, JSON.stringify({
        username: authData.username,
        email: authData.email,
        role: authData.role,
      }));
      setAuthorizationHeader(authData.token);
      return { success: true, data: authData };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data || error.message || 'Đăng nhập thất bại',
      };
    }
  },

  register: async (registerData) => {
    try {
      const response = await apiClient.post('/auth/register', registerData);
      const authData = response.data;
      localStorage.setItem(AUTH_TOKEN_KEY, authData.token);
      localStorage.setItem(AUTH_USER_KEY, JSON.stringify({
        username: authData.username,
        email: authData.email,
        role: authData.role,
      }));
      setAuthorizationHeader(authData.token);
      return { success: true, data: authData };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data || error.message || 'Đăng ký thất bại',
      };
    }
  },

  logout: () => {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_USER_KEY);
    setAuthorizationHeader(null);
  },

  getStoredUser: () => {
    try {
      const userJson = localStorage.getItem(AUTH_USER_KEY);
      return userJson ? JSON.parse(userJson) : null;
    } catch (error) {
      console.error('Failed to parse stored user', error);
      return null;
    }
  },
};

export const healthContentAPI = {
  getRecommendations: async () => {
    try {
      const response = await apiClient.get('/recommendations');
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Không thể tải dữ liệu',
      };
    }
  },

  getContentById: async (id) => {
    try {
      const response = await apiClient.get(`/health-contents/${id}`);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Không tìm thấy bài viết',
      };
    }
  },

  getContentByCategory: async (category) => {
    try {
      const response = await apiClient.get(`/health-contents/category/${category}`);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Lỗi khi tải danh mục',
      };
    }
  },

  getPublishedContent: async () => {
    try {
      const response = await apiClient.get('/health-contents');
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Không thể tải bài viết',
      };
    }
  },
};

export const symptomAPI = {
  analyze: async (symptoms) => {
    try {
      const response = await apiClient.post('/symptoms/analyze', { symptoms });
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Chẩn đoán thất bại',
      };
    }
  },

  getHistory: async () => {
    try {
      const response = await apiClient.get('/symptoms');
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Không thể lấy lịch sử chẩn đoán',
      };
    }
  },
};

export const appointmentAPI = {
  getBookings: async () => {
    try {
      const response = await apiClient.get('/appointments');
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Không thể tải danh sách lịch hẹn',
      };
    }
  },

  book: async (doctorId, appointmentDate, notes) => {
    try {
      const response = await apiClient.post('/appointments', { doctorId, appointmentDate, notes });
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Đăng ký lịch hẹn thất bại',
      };
    }
  },

  updateStatus: async (id, status) => {
    try {
      const response = await apiClient.put(`/appointments/${id}/status`, { status });
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Cập nhật trạng thái thất bại',
      };
    }
  },
};

export const interactionAPI = {
  log: async (contentId, actionType) => {
    try {
      const response = await apiClient.post('/interactions', { contentId, actionType });
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Lỗi lưu tương tác',
      };
    }
  },
};

export const medicalRecordAPI = {
  getRecords: async () => {
    try {
      const response = await apiClient.get('/medical-records');
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Không thể tải bệnh án',
      };
    }
  },
};

export default apiClient;
