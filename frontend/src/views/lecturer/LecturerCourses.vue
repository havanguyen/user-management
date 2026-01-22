<template>
  <div class="lecturer-courses">
    <div class="card">
      <div class="card-header">
        <span class="card-title">Lớp học phần giảng dạy</span>
      </div>
      <el-table :data="courses" v-loading="loading" stripe>
        <el-table-column prop="courseCode" label="Mã lớp" width="120" />
        <el-table-column label="Môn học" min-width="200">
          <template #default="{ row }">
            <div>
              <strong>{{ row.subject?.name }}</strong>
              <div class="text-muted">{{ row.subject?.subjectCode }}</div>
            </div>
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
        <el-table-column label="Học kỳ" width="180">
          <template #default="{ row }">
            {{ row.semester?.name }}
          </template>
        </el-table-column>
        <el-table-column label="Thao tác" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewStudents(row)">
              <el-icon><User /></el-icon>
              Danh sách SV
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && courses.length === 0" description="Bạn chưa được phân công lớp nào" />
    </div>
    <el-dialog v-model="showStudents" :title="`Danh sách sinh viên - ${selectedCourse?.courseCode}`" width="700px">
      <el-table :data="students" stripe>
        <el-table-column type="index" label="STT" width="60" />
        <el-table-column prop="student.studentCode" label="MSSV" width="120" />
        <el-table-column label="Họ tên" min-width="180">
          <template #default="{ row }">
            {{ row.student?.user?.firstname }} {{ row.student?.user?.lastname }}
          </template>
        </el-table-column>
        <el-table-column label="Trạng thái" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'REGISTERED' ? 'success' : 'warning'" size="small">
              {{ row.status === 'REGISTERED' ? 'Đã đăng ký' : 'Đang chờ' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Điểm" width="100" align="center">
          <template #default="{ row }">
            <el-input-number
              v-model="row.grade"
              :min="0"
              :max="10"
              :precision="1"
              size="small"
              @change="updateGrade(row)"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'
import { useAuthStore } from '../../stores/auth'
const authStore = useAuthStore()
const loading = ref(false)
const courses = ref([])
const students = ref([])
const showStudents = ref(false)
const selectedCourse = ref(null)
const loadCourses = async () => {
  loading.value = true
  try {
    const response = await api.get(`/api/lecturer/${authStore.user?.lecturerId}/courses`)
    if (response.data.success) {
      courses.value = response.data.data || []
    }
  } catch (error) {
    ElMessage.error('Không thể tải danh sách lớp')
  }
  loading.value = false
}
const viewStudents = async (course) => {
  selectedCourse.value = course
  try {
    const response = await api.get(`/api/lecturer/${authStore.user?.lecturerId}/courses/${course.id}/students`)
    if (response.data.success) {
      students.value = response.data.data || []
      showStudents.value = true
    }
  } catch (error) {
    ElMessage.error('Không thể tải danh sách sinh viên')
  }
}
const updateGrade = async (enrollment) => {
  try {
    await api.put(`/api/lecturer/grades/${enrollment.id}`, {
      grade: enrollment.grade
    })
    ElMessage.success('Cập nhật điểm thành công')
  } catch (error) {
    ElMessage.error('Cập nhật điểm thất bại')
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
