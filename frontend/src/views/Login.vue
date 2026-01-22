<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <el-icon size="48" color="#1e3a5f"><School /></el-icon>
        <h1>Đăng nhập</h1>
        <p>Hệ thống Đăng ký Tín chỉ Đại học</p>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="Tên đăng nhập" prop="username">
          <el-input
            v-model="form.username"
            prefix-icon="User"
            placeholder="Nhập tên đăng nhập"
            size="large"
          />
        </el-form-item>
        <el-form-item label="Mật khẩu" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            prefix-icon="Lock"
            placeholder="Nhập mật khẩu"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            size="large"
            :loading="loading"
            class="login-btn"
          >
            Đăng nhập
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>Chưa có tài khoản?</span>
        <router-link to="/register">Đăng ký ngay</router-link>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'
const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})
const rules = {
  username: [{ required: true, message: 'Vui lòng nhập tên đăng nhập', trigger: 'blur' }],
  password: [{ required: true, message: 'Vui lòng nhập mật khẩu', trigger: 'blur' }]
}
const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  const result = await authStore.login(form.username, form.password)
  loading.value = false
  if (result.success) {
    ElMessage.success('Đăng nhập thành công!')
    router.push('/')
  } else {
    ElMessage.error(result.message)
  }
}
</script>
<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e3a5f 0%, #3498db 100%);
}
.login-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-header h1 {
  margin: 16px 0 8px;
  color: #1e3a5f;
  font-size: 24px;
}
.login-header p {
  color: #7f8c8d;
  font-size: 14px;
}
.login-btn {
  width: 100%;
}
.login-footer {
  text-align: center;
  margin-top: 20px;
  color: #7f8c8d;
}
.login-footer a {
  color: #3498db;
  text-decoration: none;
  font-weight: 500;
  margin-left: 8px;
}
</style>
