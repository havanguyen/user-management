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

        <el-form-item label="Vai trò" prop="role">
          <el-select v-model="form.role" placeholder="Chọn vai trò" style="width: 100%" @change="handleRoleChange">
            <el-option label="Sinh viên" value="STUDENT" />
            <el-option label="Giảng viên" value="LECTURER" />
          </el-select>
        </el-form-item>

        <!-- Student specific fields -->
        <template v-if="form.role === 'STUDENT'">
          <el-form-item label="Mã sinh viên" prop="studentCode">
            <el-input v-model="form.studentCode" placeholder="Nhập MSSV (VD: SV001)" />
          </el-form-item>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="Năm nhập học" prop="enrollmentYear">
                <el-input-number v-model="form.enrollmentYear" :min="2000" :max="2030" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Ngành học" prop="majorId">
                <el-select v-model="form.majorId" placeholder="Chọn ngành" style="width: 100%" :loading="loadingMeta">
                  <el-option v-for="m in majors" :key="m.id" :label="m.name" :value="m.id" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- Lecturer specific fields -->
        <template v-if="form.role === 'LECTURER'">
          <el-form-item label="Mã giảng viên" prop="lecturerCode">
            <el-input v-model="form.lecturerCode" placeholder="Nhập MGV (VD: GV001)" />
          </el-form-item>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="Học vị" prop="degree">
                <el-select v-model="form.degree" placeholder="Chọn học vị" style="width: 100%">
                  <el-option label="Cử nhân" value="BACHELOR" />
                  <el-option label="Thạc sĩ" value="MASTER" />
                  <el-option label="Tiến sĩ" value="DOCTOR" />
                  <el-option label="Giáo sư" value="PROFESSOR" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Khoa" prop="departmentId">
                <el-select v-model="form.departmentId" placeholder="Chọn khoa" style="width: 100%" :loading="loadingMeta">
                  <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'
import axios from 'axios' // Use raw axios for public endpoints or api instance

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const loadingMeta = ref(false)
const majors = ref([])
const departments = ref([])

const form = reactive({
  username: '',
  password: '',
  firstname: '',
  lastname: '',
  dod: '', // Changed to null if empty before submit
  role: 'STUDENT',
  studentCode: '',
  enrollmentYear: 2026,
  majorId: '',
  lecturerCode: '',
  degree: '',
  departmentId: ''
})

const rules = computed(() => {
  const baseRules = {
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

  if (form.role === 'STUDENT') {
    return {
      ...baseRules,
      studentCode: [{ required: true, message: 'Vui lòng nhập mã sinh viên', trigger: 'blur' }],
      majorId: [{ required: true, message: 'Vui lòng chọn ngành học', trigger: 'change' }]
    }
  } else if (form.role === 'LECTURER') {
    return {
      ...baseRules,
      lecturerCode: [{ required: true, message: 'Vui lòng nhập mã giảng viên', trigger: 'blur' }],
      departmentId: [{ required: true, message: 'Vui lòng chọn khoa', trigger: 'change' }]
    }
  }
  return baseRules
})

const fetchMetaData = async () => {
  loadingMeta.value = true
  try {
    // We can use the configured api instance but we need to ensure it doesn't redirect on 401 if we were using it for protected routes
    // But public routes are fine.
    const baseUrl = 'http://localhost:8080/api/public' // Hardcoded for simplicity or use env
    
    const [majorRes, deptRes] = await Promise.all([
      axios.get(`${baseUrl}/majors`),
      axios.get(`${baseUrl}/departments`)
    ])
    
    if (majorRes.data.success) {
      majors.value = majorRes.data.data
    }
    if (deptRes.data.success) {
      departments.value = deptRes.data.data
    }
  } catch (error) {
    console.error('Failed to load metadata', error)
    // ElMessage.warning('Không thể tải danh sách ngành/khoa. Vui lòng thử lại sau.')
  }
  loadingMeta.value = false
}

const handleRoleChange = () => {
  // Reset role specific fields
  if (form.role === 'STUDENT') {
    form.lecturerCode = ''
    form.degree = ''
    form.departmentId = ''
  } else {
    form.studentCode = ''
    form.majorId = ''
  }
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  
  // Prepare payload
  const payload = { ...form }
  if (!payload.dod) payload.dod = null // Handle empty date string
  
  const result = await authStore.register(payload)
  loading.value = false

  if (result.success) {
    ElMessage.success('Đăng ký thành công!')
    router.push('/login')
  } else {
    ElMessage.error(result.message)
  }
}

onMounted(() => {
  fetchMetaData()
})
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
  max-width: 600px;
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
