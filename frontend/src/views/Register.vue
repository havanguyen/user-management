<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <el-icon size="48" color="#1e3a5f"><School /></el-icon>
        <h1>Đăng ký tài khoản</h1>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleRegister"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="Họ" prop="firstname">
              <el-input v-model="form.firstname" placeholder="Nguyễn" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Tên" prop="lastname">
              <el-input v-model="form.lastname" placeholder="Văn A" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="Tên đăng nhập" prop="username">
          <el-input v-model="form.username" prefix-icon="User" placeholder="username" />
        </el-form-item>
        <el-form-item label="Mật khẩu" prop="password">
          <el-input v-model="form.password" type="password" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item label="Vai trò" prop="role">
          <el-select v-model="form.role" placeholder="Chọn vai trò" style="width: 100%">
            <el-option label="Sinh viên" value="STUDENT" />
            <el-option label="Giảng viên" value="LECTURER" />
          </el-select>
        </el-form-item>
        <el-form-item label="Ngày sinh" prop="dod">
          <el-date-picker
            v-model="form.dod"
            type="date"
            placeholder="Chọn ngày sinh"
            style="width: 100%"
            format="DD/MM/YYYY"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            size="large"
            :loading="loading"
            class="register-btn"
          >
            Đăng ký
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <span>Đã có tài khoản?</span>
        <router-link to="/login">Đăng nhập</router-link>
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
  password: '',
  firstname: '',
  lastname: '',
  dod: '',
  role: 'STUDENT'
})
const rules = {
  username: [
    { required: true, message: 'Vui lòng nhập tên đăng nhập', trigger: 'blur' },
    { min: 3, max: 20, message: 'Tên đăng nhập từ 3-20 ký tự', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Vui lòng nhập mật khẩu', trigger: 'blur' },
    { min: 8, message: 'Mật khẩu ít nhất 8 ký tự', trigger: 'blur' }
  ],
  firstname: [{ required: true, message: 'Vui lòng nhập họ', trigger: 'blur' }],
  lastname: [{ required: true, message: 'Vui lòng nhập tên', trigger: 'blur' }],
  role: [{ required: true, message: 'Vui lòng chọn vai trò', trigger: 'change' }]
}
const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  const result = await authStore.register(form)
  loading.value = false
  if (result.success) {
    ElMessage.success('Đăng ký thành công!')
    router.push('/')
  } else {
    ElMessage.error(result.message)
  }
}
</script>
<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e3a5f 0%, #3498db 100%);
  padding: 40px 20px;
}
.register-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}
.register-header {
  text-align: center;
  margin-bottom: 24px;
}
.register-header h1 {
  margin: 16px 0 0;
  color: #1e3a5f;
  font-size: 24px;
}
.register-btn {
  width: 100%;
}
.register-footer {
  text-align: center;
  margin-top: 20px;
  color: #7f8c8d;
}
.register-footer a {
  color: #3498db;
  text-decoration: none;
  font-weight: 500;
  margin-left: 8px;
}
</style>
