<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon size="28"><School /></el-icon>
        <span>ĐKTC University</span>
      </div>
      <el-menu
        :default-active="$route.path"
        :router="true"
        background-color="#1e3a5f"
        text-color="#ecf0f1"
        active-text-color="#3498db"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <span>Trang chủ</span>
        </el-menu-item>
        <template v-if="authStore.isStudent">
          <el-menu-item index="/courses">
            <el-icon><Reading /></el-icon>
            <span>Đăng ký môn học</span>
          </el-menu-item>
          <el-menu-item index="/my-registrations">
            <el-icon><List /></el-icon>
            <span>Môn đã đăng ký</span>
          </el-menu-item>
        </template>
        <template v-if="authStore.isLecturer">
          <el-menu-item index="/lecturer/courses">
            <el-icon><Notebook /></el-icon>
            <span>Lớp giảng dạy</span>
          </el-menu-item>
        </template>
        <template v-if="authStore.isAdmin">
          <el-sub-menu index="admin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>Quản lý</span>
            </template>
            <el-menu-item index="/admin/semesters">Học kỳ</el-menu-item>
            <el-menu-item index="/admin/subjects">Môn học</el-menu-item>
            <el-menu-item index="/admin/courses">Lớp học phần</el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h2>Hệ thống Đăng ký Tín chỉ</h2>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-dropdown">
              <el-avatar :size="32" class="avatar">
                {{ authStore.user?.firstname?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ authStore.user?.firstname }} {{ authStore.user?.lastname }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  Đăng xuất
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'
const router = useRouter()
const authStore = useAuthStore()
const handleLogout = async () => {
  await authStore.logout()
  ElMessage.success('Đăng xuất thành công')
  router.push('/login')
}
</script>
<style scoped>
.layout-container {
  min-height: 100vh;
}
.sidebar {
  background: #1e3a5f;
  box-shadow: 2px 0 8px rgba(0,0,0,0.15);
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.header {
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.header h2 {
  font-size: 18px;
  color: #1e3a5f;
  margin: 0;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.avatar {
  background: #3498db;
  color: white;
}
.username {
  color: #2c3e50;
  font-weight: 500;
}
.main-content {
  background: #f5f7fa;
  padding: 24px;
}
.el-menu {
  border-right: none;
}
</style>
