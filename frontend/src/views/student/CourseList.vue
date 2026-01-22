<template>
  <div class="course-list">
    <div class="card">
      <div class="card-header">
        <span class="card-title">Đăng ký môn học</span>
        <el-input
          v-model="searchQuery"
          placeholder="Tìm kiếm môn học..."
          prefix-icon="Search"
          style="width: 300px"
          clearable
        />
      </div>
      <el-table :data="filteredCourses" v-loading="loading" stripe>
        <el-table-column prop="courseCode" label="Mã lớp" width="120" />
        <el-table-column label="Môn học" min-width="200">
          <template #default="{ row }">
            <div>
              <strong>{{ row.subject?.name }}</strong>
              <div class="text-muted">{{ row.subject?.subjectCode }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Giảng viên" width="150">
          <template #default="{ row }">
            {{ row.lecturer?.user?.firstname }} {{ row.lecturer?.user?.lastname }}
          </template>
        </el-table-column>
        <el-table-column label="Tín chỉ" width="80" align="center">
          <template #default="{ row }">
            {{ row.subject?.credits }}
          </template>
        </el-table-column>
        <el-table-column label="Sĩ số" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.currentStudents >= row.maxStudents ? 'danger' : 'success'">
              {{ row.currentStudents }}/{{ row.maxStudents }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="scheduleInfo" label="Lịch học" width="150" />
        <el-table-column label="Trạng thái" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Thao tác" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleRegister(row)"
              :disabled="row.status !== 'OPEN_FOR_REGISTRATION'"
            >
              Đăng ký
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @change="loadCourses"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'
const loading = ref(false)
const courses = ref([])
const searchQuery = ref('')
const pagination = ref({ page: 1, size: 10, total: 0 })
const filteredCourses = computed(() => {
  if (!searchQuery.value) return courses.value
  const query = searchQuery.value.toLowerCase()
  return courses.value.filter(c =>
    c.courseCode?.toLowerCase().includes(query) ||
    c.subject?.name?.toLowerCase().includes(query)
  )
})
const getStatusType = (status) => {
  const types = {
    'OPEN_FOR_REGISTRATION': 'success',
    'UPCOMING': 'info',
    'IN_PROGRESS': 'warning',
    'FINISHED': '',
    'CANCELLED': 'danger'
  }
  return types[status] || ''
}
const getStatusText = (status) => {
  const texts = {
    'OPEN_FOR_REGISTRATION': 'Đang mở',
    'UPCOMING': 'Sắp mở',
    'IN_PROGRESS': 'Đang học',
    'FINISHED': 'Kết thúc',
    'CANCELLED': 'Đã hủy'
  }
  return texts[status] || status
}
const loadCourses = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/admin/courses', {
      params: {
        page: pagination.value.page - 1,
        size: pagination.value.size
      }
    })
    if (response.data.success) {
      courses.value = response.data.data.items || []
      pagination.value.total = response.data.data.totalItems || 0
    }
  } catch (error) {
    ElMessage.error('Không thể tải danh sách lớp học')
  }
  loading.value = false
}
const handleRegister = async (course) => {
  try {
    await ElMessageBox.confirm(
      `Bạn có chắc muốn đăng ký lớp ${course.courseCode} - ${course.subject?.name}?`,
      'Xác nhận đăng ký',
      { confirmButtonText: 'Đăng ký', cancelButtonText: 'Hủy', type: 'info' }
    )
    const response = await api.post('/api/student/register', {
      courseId: course.id
    })
    if (response.data.success) {
      ElMessage.success('Đăng ký thành công!')
      loadCourses()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || 'Đăng ký thất bại')
    }
  }
}
onMounted(() => {
  loadCourses()
})
</script>
<style scoped>
.text-muted {
  color: #909399;
  font-size: 12px;
}
</style>
