import axios from 'axios'
import { useAuthStore } from '../stores/auth'
import router from '../router'
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json'
    }
})
api.interceptors.response.use(
    response => response,
    async error => {
        if (error.response?.status === 401) {
            const authStore = useAuthStore()
            authStore.logout()
            router.push('/login')
        }
        return Promise.reject(error)
    }
)
export default api
