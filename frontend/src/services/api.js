import axios from 'axios';

// Cấu hình base URL cho API backend
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// API service cho health content
export const healthContentAPI = {
  // Lấy danh sách bài viết sức khỏe gợi ý
  getRecommendations: async () => {
    try {
      const response = await apiClient.get('/health-contents');
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

  // Lấy bài viết theo ID
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

  // Lấy bài viết theo danh mục
  getContentByCategory: async (category) => {
    try {
      const response = await apiClient.get(`/health-contents?category=${category}`);
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

  // Lấy bài viết đã xuất bản
  getPublishedContent: async () => {
    try {
      const response = await apiClient.get('/health-contents/published');
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

export default apiClient;
