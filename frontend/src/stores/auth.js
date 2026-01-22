import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../api'
export const useAuthStore = defineStore('auth', () => {
    const user = ref(null)
    const isAuthenticated = ref(false)
    const roles = ref([])
    const isAdmin = computed(() => roles.value.includes('ADMIN'))
    const isStudent = computed(() => roles.value.includes('STUDENT'))
    const isLecturer = computed(() => roles.value.includes('LECTURER'))
    async function login(username, password) {
        try {
            const response = await api.post('/auth/login', { username, password })
            if (response.data.success) {
                isAuthenticated.value = true
                user.value = response.data.data
                roles.value = response.data.data.roles || []
                return { success: true }
            }
            return { success: false, message: response.data.message }
        } catch (error) {
            return { success: false, message: error.response?.data?.message || 'Đăng nhập thất bại' }
        }
    }
    async function register(userData) {
        try {
            const response = await api.post('/auth/register', userData)
            if (response.data.success) {
                isAuthenticated.value = true
                user.value = response.data.data
                roles.value = response.data.data.roles || []
                return { success: true }
            }
            return { success: false, message: response.data.message }
        } catch (error) {
            return { success: false, message: error.response?.data?.message || 'Đăng ký thất bại' }
        }
    }
    async function logout() {
        try {
            await api.post('/auth/logout')
        } catch (error) {
            console.error('Logout error:', error)
        }
        user.value = null
        isAuthenticated.value = false
        roles.value = []
    }
    async function checkAuth() {
        try {
            const response = await api.post('/auth/refresh')
            if (response.data.success) {
                isAuthenticated.value = true
                user.value = response.data.data
                roles.value = response.data.data?.roles || []
                return true
            }
        } catch (error) {
            isAuthenticated.value = false
        }
        return false
    }
    return {
        user,
        isAuthenticated,
        roles,
        isAdmin,
        isStudent,
        isLecturer,
        login,
        register,
        logout,
        checkAuth
    }
})
