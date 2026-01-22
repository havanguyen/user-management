<template>
  <div class="admin-courses">
    <div class="card">
      <div class="card-header">
        <span class="card-title">Quản lý Lớp học phần</span>
        <el-button type="primary" @click="showDialog = true">
          <el-icon><Plus /></el-icon>
          Thêm lớp học phần
        </el-button>
      </div>
      <el-table :data="courses" v-loading="loading" stripe>
        <el-table-column prop="courseCode" label="Mã lớp" width="120" />
        <el-table-column label="Môn học" min-width="180">
          <template #default="{ row }">
            {{ row.subject?.name }}
          </template>
        </el-table-column>
        <el-table-column label="Giảng viên" width="150">
          <template #default="{ row }">
            {{ row.lecturer?.user?.firstname }} {{ row.lecturer?.user?.lastname }}
          </template>
        </el-table-column>
        <el-table-column label="Sĩ số" width="100" align="center">
          <template #default="{ row }">
            {{ row.currentStudents }}/{{ row.maxStudents }}
          </template>
        </el-table-column>
        <el-table-column prop="scheduleInfo" label="Lịch học" width="150" />
        <el-table-column label="Trạng thái" width="130" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Thao tác" width="100" align="center">
          <template #default="{ row }">
            <el-button type="danger" size="small" text @click="deleteCourse(row)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pagination.page"
        :total="pagination.total"
        :page-size="10"
        layout="prev, pager, next"
        @change="loadCourses"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>
    <el-dialog v-model="showDialog" title="Thêm lớp học phần" width="600px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Mã lớp">
          <el-input v-model="form.courseCode" placeholder="VD: CS101-01" />
        </el-form-item>
        <el-form-item label="Môn học">
          <el-select v-model="form.subjectId" placeholder="Chọn môn học" style="width: 100%">
            <el-option v-for="s in subjects" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Học kỳ">
          <el-select v-model="form.semesterId" placeholder="Chọn học kỳ" style="width: 100%">
            <el-option v-for="s in semesters" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Giảng viên">
          <el-select v-model="form.lecturerId" placeholder="Chọn giảng viên" style="width: 100%">
            <el-option v-for="l in lecturers" :key="l.id" 
              :label="`${l.user?.firstname} ${l.user?.lastname}`" :value="l.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Sĩ số tối đa">
          <el-input-number v-model="form.maxStudents" :min="10" :max="200" />
        </el-form-item>
        <el-form-item label="Lịch học">
          <el-input v-model="form.scheduleInfo" placeholder="VD: T2 1-3, T4 1-3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">Hủy</el-button>
        <el-button type="primary" @click="saveCourse" :loading="saving">Lưu</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const courses = ref([])
const subjects = ref([])
const semesters = ref([])
const lecturers = ref([])
const pagination = ref({ page: 1, total: 0 })
const form = reactive({
  courseCode: '',
  subjectId: '',
  semesterId: '',
  lecturerId: '',
  maxStudents: 40,
  scheduleInfo: ''
})
const getStatusType = (status) => {
  const types = { 'OPEN_FOR_REGISTRATION': 'success', 'UPCOMING': 'info', 'IN_PROGRESS': 'warning' }
  return types[status] || ''
}
const getStatusText = (status) => {
  const texts = { 'OPEN_FOR_REGISTRATION': 'Đang mở', 'UPCOMING': 'Sắp mở', 'IN_PROGRESS': 'Đang học', 'FINISHED': 'Kết thúc' }
  return texts[status] || status
}
const loadCourses = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/admin/courses', {
      params: { page: pagination.value.page - 1, size: 10 }
    })
    if (response.data.success) {
      courses.value = response.data.data.items || []
      pagination.value.total = response.data.data.totalItems || 0
    }
  } catch (error) {
    ElMessage.error('Không thể tải danh sách')
  }
  loading.value = false
}
const loadFormData = async () => {
  try {
    const [subRes, semRes] = await Promise.all([
      api.get('/api/admin/subjects', { params: { page: 0, size: 100 } }),
      api.get('/api/admin/semesters', { params: { page: 0, size: 100 } })
    ])
    subjects.value = subRes.data.data?.items || []
    semesters.value = semRes.data.data?.items || []
  } catch (error) {
    console.error('Error loading form data:', error)
  }
}
const saveCourse = async () => {
  saving.value = true
  try {
    const response = await api.post('/api/admin/courses', form)
    if (response.data.success) {
      ElMessage.success('Thêm thành công')
      showDialog.value = false
      loadCourses()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || 'Lưu thất bại')
  }
  saving.value = false
}
const deleteCourse = async (course) => {
  try {
    await ElMessageBox.confirm('Xác nhận xóa lớp học phần?', 'Cảnh báo', { type: 'warning' })
    await api.delete(`/api/admin/courses/${course.id}`)
    ElMessage.success('Xóa thành công')
    loadCourses()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('Xóa thất bại')
  }
}
onMounted(() => {
  loadCourses()
  loadFormData()
})
</script>
