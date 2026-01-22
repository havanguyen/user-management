<template>
  <div class="my-registrations">
    <div class="card">
      <div class="card-header">
        <span class="card-title">Môn học đã đăng ký</span>
        <el-tag type="info" size="large">
          Tổng: {{ totalCredits }} tín chỉ
        </el-tag>
      </div>
      <el-table :data="registrations" v-loading="loading" stripe>
        <el-table-column prop="course.courseCode" label="Mã lớp" width="120" />
        <el-table-column label="Môn học" min-width="200">
          <template #default="{ row }">
            <div>
              <strong>{{ row.course?.subject?.name }}</strong>
              <div class="text-muted">{{ row.course?.subject?.subjectCode }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Giảng viên" width="150">
          <template #default="{ row }">
            {{ row.course?.lecturer?.user?.firstname }} {{ row.course?.lecturer?.user?.lastname }}
          </template>
        </el-table-column>
        <el-table-column label="Tín chỉ" width="80" align="center">
          <template #default="{ row }">
            {{ row.course?.subject?.credits }}
          </template>
        </el-table-column>
        <el-table-column prop="course.scheduleInfo" label="Lịch học" width="150" />
        <el-table-column label="Trạng thái" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Điểm" width="80" align="center">
          <template #default="{ row }">
            <span v-if="row.grade !== null">{{ row.grade }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="Thao tác" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              type="danger"
              size="small"
              text
              @click="handleDrop(row)"
              :disabled="row.status === 'COMPLETED'"
            >
              <el-icon><Delete /></el-icon>
              Hủy
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && registrations.length === 0" description="Bạn chưa đăng ký môn học nào" />
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'
const loading = ref(false)
const registrations = ref([])
const totalCredits = computed(() => {
  return registrations.value
    .filter(r => r.status === 'REGISTERED')
    .reduce((sum, r) => sum + (r.course?.subject?.credits || 0), 0)
})
const getStatusType = (status) => {
  const types = {
    'REGISTERED': 'success',
    'WAITLIST': 'warning',
    'CANCELLED': 'info',
    'COMPLETED': ''
  }
  return types[status] || ''
}
const getStatusText = (status) => {
  const texts = {
    'REGISTERED': 'Đã đăng ký',
    'WAITLIST': 'Đang chờ',
    'CANCELLED': 'Đã hủy',
    'COMPLETED': 'Hoàn thành'
  }
  return texts[status] || status
}
const loadRegistrations = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/student/my-registrations')
    if (response.data.success) {
      registrations.value = response.data.data || []
    }
  } catch (error) {
    ElMessage.error('Không thể tải danh sách đăng ký')
  }
  loading.value = false
}
const handleDrop = async (registration) => {
  try {
    await ElMessageBox.confirm(
      `Bạn có chắc muốn hủy đăng ký lớp ${registration.course?.courseCode}?`,
      'Xác nhận hủy',
      { confirmButtonText: 'Hủy đăng ký', cancelButtonText: 'Đóng', type: 'warning' }
    )
    const response = await api.delete(`/api/student/drop/${registration.id}`)
    if (response.data.success) {
      ElMessage.success('Hủy đăng ký thành công')
      loadRegistrations()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || 'Hủy đăng ký thất bại')
    }
  }
}
onMounted(() => {
  loadRegistrations()
})
</script>
<style scoped>
.text-muted {
  color: #909399;
  font-size: 12px;
}
</style>
