<template>
  <div class="admin-semesters">
    <div class="card">
      <div class="card-header">
        <span class="card-title">Quản lý Học kỳ</span>
        <el-button type="primary" @click="showDialog = true">
          <el-icon><Plus /></el-icon>
          Thêm học kỳ
        </el-button>
      </div>
      <el-table :data="semesters" v-loading="loading" stripe>
        <el-table-column prop="name" label="Tên học kỳ" min-width="200" />
        <el-table-column prop="startDate" label="Ngày bắt đầu" width="150" />
        <el-table-column prop="endDate" label="Ngày kết thúc" width="150" />
        <el-table-column label="Thao tác" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="editSemester(row)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button type="danger" size="small" text @click="deleteSemester(row)">
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
        @change="loadSemesters"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>
    <el-dialog v-model="showDialog" :title="editMode ? 'Sửa học kỳ' : 'Thêm học kỳ'" width="500px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Tên học kỳ">
          <el-input v-model="form.name" placeholder="VD: Học kỳ 1 - 2026" />
        </el-form-item>
        <el-form-item label="Ngày bắt đầu">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="Ngày kết thúc">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">Hủy</el-button>
        <el-button type="primary" @click="saveSemester" :loading="saving">Lưu</el-button>
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
const editMode = ref(false)
const semesters = ref([])
const pagination = ref({ page: 1, total: 0 })
const form = reactive({
  id: null,
  name: '',
  startDate: '',
  endDate: ''
})
const resetForm = () => {
  form.id = null
  form.name = ''
  form.startDate = ''
  form.endDate = ''
  editMode.value = false
}
const loadSemesters = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/admin/semesters', {
      params: { page: pagination.value.page - 1, size: 10 }
    })
    if (response.data.success) {
      semesters.value = response.data.data.items || []
      pagination.value.total = response.data.data.totalItems || 0
    }
  } catch (error) {
    ElMessage.error('Không thể tải danh sách học kỳ')
  }
  loading.value = false
}
const editSemester = (semester) => {
  Object.assign(form, semester)
  editMode.value = true
  showDialog.value = true
}
const saveSemester = async () => {
  saving.value = true
  try {
    const response = await api.post('/api/admin/semesters', form)
    if (response.data.success) {
      ElMessage.success(editMode.value ? 'Cập nhật thành công' : 'Thêm thành công')
      showDialog.value = false
      resetForm()
      loadSemesters()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || 'Lưu thất bại')
  }
  saving.value = false
}
const deleteSemester = async (semester) => {
  try {
    await ElMessageBox.confirm('Xác nhận xóa học kỳ này?', 'Cảnh báo', { type: 'warning' })
    await api.delete(`/api/admin/semesters/${semester.id}`)
    ElMessage.success('Xóa thành công')
    loadSemesters()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Xóa thất bại')
    }
  }
}
onMounted(() => {
  loadSemesters()
})
</script>
