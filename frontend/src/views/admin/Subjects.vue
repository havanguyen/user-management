<template>
  <div class="admin-subjects">
    <div class="card">
      <div class="card-header">
        <span class="card-title">Quản lý Môn học</span>
        <el-button type="primary" @click="showDialog = true">
          <el-icon><Plus /></el-icon>
          Thêm môn học
        </el-button>
      </div>
      <el-table :data="subjects" v-loading="loading" stripe>
        <el-table-column prop="subjectCode" label="Mã môn" width="120" />
        <el-table-column prop="name" label="Tên môn học" min-width="200" />
        <el-table-column prop="credits" label="Tín chỉ" width="100" align="center" />
        <el-table-column label="Khoa" width="200">
          <template #default="{ row }">
            {{ row.department?.name }}
          </template>
        </el-table-column>
        <el-table-column label="Thao tác" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="editSubject(row)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button type="danger" size="small" text @click="deleteSubject(row)">
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
        @change="loadSubjects"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>
    <el-dialog v-model="showDialog" :title="editMode ? 'Sửa môn học' : 'Thêm môn học'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="Mã môn">
          <el-input v-model="form.subjectCode" placeholder="VD: CS101" />
        </el-form-item>
        <el-form-item label="Tên môn">
          <el-input v-model="form.name" placeholder="VD: Lập trình cơ bản" />
        </el-form-item>
        <el-form-item label="Số tín chỉ">
          <el-input-number v-model="form.credits" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="Khoa">
          <el-select v-model="form.departmentId" placeholder="Chọn khoa" style="width: 100%">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">Hủy</el-button>
        <el-button type="primary" @click="saveSubject" :loading="saving">Lưu</el-button>
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
const subjects = ref([])
const departments = ref([])
const pagination = ref({ page: 1, total: 0 })
const form = reactive({
  id: null,
  subjectCode: '',
  name: '',
  credits: 3,
  departmentId: ''
})
const resetForm = () => {
  form.id = null
  form.subjectCode = ''
  form.name = ''
  form.credits = 3
  form.departmentId = ''
  editMode.value = false
}
const loadSubjects = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/admin/subjects', {
      params: { page: pagination.value.page - 1, size: 10 }
    })
    if (response.data.success) {
      subjects.value = response.data.data.items || []
      pagination.value.total = response.data.data.totalItems || 0
    }
  } catch (error) {
    ElMessage.error('Không thể tải danh sách môn học')
  }
  loading.value = false
}
const editSubject = (subject) => {
  Object.assign(form, {
    ...subject,
    departmentId: subject.department?.id
  })
  editMode.value = true
  showDialog.value = true
}
const saveSubject = async () => {
  saving.value = true
  try {
    const response = await api.post('/api/admin/subjects', form)
    if (response.data.success) {
      ElMessage.success(editMode.value ? 'Cập nhật thành công' : 'Thêm thành công')
      showDialog.value = false
      resetForm()
      loadSubjects()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || 'Lưu thất bại')
  }
  saving.value = false
}
const deleteSubject = async (subject) => {
  try {
    await ElMessageBox.confirm('Xác nhận xóa môn học này?', 'Cảnh báo', { type: 'warning' })
    await api.delete(`/api/admin/subjects/${subject.id}`)
    ElMessage.success('Xóa thành công')
    loadSubjects()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Xóa thất bại')
    }
  }
}
onMounted(() => {
  loadSubjects()
})
</script>
