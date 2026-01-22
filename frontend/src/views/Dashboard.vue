<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="card welcome-card">
          <h1>Xin chào, {{ authStore.user?.firstname }}! 👋</h1>
          <p>Chào mừng bạn đến với Hệ thống Đăng ký Tín chỉ</p>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="card stat-card">
          <div class="stat-icon" style="background: #3498db;">
            <el-icon size="32"><Reading /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ stats.registeredCourses }}</h3>
            <p>Môn đã đăng ký</p>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="card stat-card">
          <div class="stat-icon" style="background: #27ae60;">
            <el-icon size="32"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ stats.totalCredits }}</h3>
            <p>Tổng tín chỉ</p>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="card stat-card">
          <div class="stat-icon" style="background: #f39c12;">
            <el-icon size="32"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ stats.waitlistCourses }}</h3>
            <p>Đang chờ</p>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="card stat-card">
          <div class="stat-icon" style="background: #9b59b6;">
            <el-icon size="32"><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ stats.currentSemester }}</h3>
            <p>Học kỳ hiện tại</p>
          </div>
        </div>
      </el-col>
      <el-col :span="24">
        <div class="card">
          <div class="card-header">
            <span class="card-title">Thao tác nhanh</span>
          </div>
          <el-row :gutter="16">
            <el-col :xs="24" :sm="8" v-if="authStore.isStudent">
              <el-button type="primary" size="large" @click="$router.push('/courses')" class="action-btn">
                <el-icon><Plus /></el-icon>
                Đăng ký môn học
              </el-button>
            </el-col>
            <el-col :xs="24" :sm="8" v-if="authStore.isStudent">
              <el-button size="large" @click="$router.push('/my-registrations')" class="action-btn">
                <el-icon><List /></el-icon>
                Xem môn đã đăng ký
              </el-button>
            </el-col>
            <el-col :xs="24" :sm="8" v-if="authStore.isAdmin">
              <el-button type="success" size="large" @click="$router.push('/admin/courses')" class="action-btn">
                <el-icon><Setting /></el-icon>
                Quản lý lớp học phần
              </el-button>
            </el-col>
          </el-row>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
const authStore = useAuthStore()
const stats = ref({
  registeredCourses: 0,
  totalCredits: 0,
  waitlistCourses: 0,
  currentSemester: 'HK1 2026'
})
onMounted(() => {
})
</script>
<style scoped>
.dashboard {
  padding: 0;
}
.welcome-card {
  background: linear-gradient(135deg, #1e3a5f 0%, #3498db 100%);
  color: white;
  text-align: center;
  padding: 40px;
}
.welcome-card h1 {
  margin: 0 0 8px;
  font-size: 28px;
}
.welcome-card p {
  margin: 0;
  opacity: 0.9;
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}
.stat-info h3 {
  margin: 0;
  font-size: 28px;
  color: #2c3e50;
}
.stat-info p {
  margin: 4px 0 0;
  color: #7f8c8d;
  font-size: 14px;
}
.action-btn {
  width: 100%;
  height: 60px;
  font-size: 16px;
}
</style>
