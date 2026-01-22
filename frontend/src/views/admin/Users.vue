<template>
  <div class="admin-users">
    <div class="card">
      <div class="card-header">
        <span class="card-title">Quản lý Người dùng</span>
        <el-button type="primary" @click="showDialog = true">
          <el-icon><Plus /></el-icon>
          Thêm người dùng
        </el-button>
      </div>

      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="username" label="Tên đăng nhập" width="150" />
        <el-table-column label="Họ tên" min-width="200">
          <template #default="{ row }">
            {{ row.firstname }} {{ row.lastname }}
          </template>
        </el-table-column>
        <el-table-column label="Vai trò" width="150">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role" size="small" style="margin-right: 5px">
              {{ role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dod" label="Ngày sinh" width="120" />
        <el-table-column label="Thao tác" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="editUser(row)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button type="danger" size="small" text @click="deleteUser(row)">
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
        @change="loadUsers"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="showDialog" :title="editMode ? 'Sửa người dùng' : 'Thêm người dùng'" width="500px" @closed="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" label-position="top">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="Họ" prop="firstname">
              <el-input v-model="form.firstname" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Tên" prop="lastname">
              <el-input v-model="form.lastname" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="Tên đăng nhập" prop="username" v-if="!editMode">
          <el-input v-model="form.username" />
        </el-form-item>

        <el-form-item label="Mật khẩu" prop="password">
          <el-input v-model="form.password" type="password" show-password :placeholder="editMode ? 'Để trống nếu không đổi' : ''" />
        </el-form-item>

        <el-form-item label="Ngày sinh" prop="dod">
          <el-date-picker v-model="form.dod" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">Hủy</el-button>
        <el-button type="primary" @click="saveUser" :loading="saving">Lưu</el-button>
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
const users = ref([])
const pagination = ref({ page: 1, total: 0 })
const formRef = ref(null)

const form = reactive({
  id: null,
  username: '',
  password: '',
  firstname: '',
  lastname: '',
  dod: ''
})

const rules = {
  firstname: [{ required: true, message: 'Vui lòng nhập họ', trigger: 'blur' }],
  lastname: [{ required: true, message: 'Vui lòng nhập tên', trigger: 'blur' }],
  username: [{ required: true, message: 'Vui lòng nhập tên đăng nhập', trigger: 'blur' }],
  password: [{ required: false, message: 'Vui lòng nhập mật khẩu', trigger: 'blur' }] // Dynamic check in save
}

const resetForm = () => {
  form.id = null
  form.username = ''
  form.password = ''
  form.firstname = ''
  form.lastname = ''
  form.dod = ''
  editMode.value = false
  if (formRef.value) formRef.value.resetFields()
}

const loadUsers = async () => {
  loading.value = true
  try {
    const response = await api.get('/users', {
      params: { page: pagination.value.page - 1, size: 10 }
    })
    if (response.data.success) {
      users.value = response.data.data.items || []
      pagination.value.total = response.data.data.totalItems || 0
    }
  } catch (error) {
    ElMessage.error('Không thể tải danh sách người dùng')
  }
  loading.value = false
}

const editUser = (user) => {
  Object.assign(form, user)
  form.password = '' // Don't show hash
  editMode.value = true
  showDialog.value = true
}

const saveUser = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (!editMode.value && !form.password) {
        ElMessage.error('Vui lòng nhập mật khẩu')
        return
      }

      saving.value = true
      try {
        if (editMode.value) {
          const payload = { ...form }
          if (!payload.password) delete payload.password
          await api.put(`/users/${form.id}`, payload)
        } else {
          await api.post('/users', form)
        }
        
        ElMessage.success(editMode.value ? 'Cập nhật thành công' : 'Thêm thành công')
        showDialog.value = false
        loadUsers()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || 'Lưu thất bại')
      }
      saving.value = false
    }
  })
}

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm('Xác nhận xóa người dùng này?', 'Cảnh báo', { type: 'warning' })
    await api.delete(`/users/${user.id}`)
    ElMessage.success('Xóa thành công')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Xóa thất bại')
    }
  }
}

onMounted(() => {
  loadUsers()
})
</script>
