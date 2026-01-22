<template>
  <div class="admin-registration-periods">
    <div class="card">
      <div class="card-header">
        <span class="card-title">Đợt đăng ký</span>
        <el-button type="primary" @click="showDialog = true">
          <el-icon><Plus /></el-icon>
          Thêm đợt đăng ký
        </el-button>
      </div>

      <el-table :data="periods" v-loading="loading" stripe>
        <el-table-column label="Học kỳ" min-width="200">
          <template #default="{ row }">
            {{ row.semester?.name }}
          </template>
        </el-table-column>
        <el-table-column label="Thời gian" min-width="320">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }} - {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="Trạng thái" width="150" align="center">
          <template #default="{ row }">
            <el-tag :type="row.active ? 'success' : 'info'">
              {{ row.active ? 'Đang kích hoạt' : 'Hết hạn/Vô hiệu' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Thao tác" width="200" align="center">
          <template #default="{ row }">
            <el-button 
              v-if="!row.active"
              type="success" 
              size="small" 
              @click="toggleStatus(row, true)"
              :loading="row.processing"
            >
              Kích hoạt
            </el-button>
            <el-button 
              v-else
              type="warning" 
              size="small" 
              @click="toggleStatus(row, false)"
              :loading="row.processing"
            >
              Vô hiệu hóa
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        :total="pagination.total"
        :page-size="10"
        layout="prev, pager, next"
        @change="loadPeriods"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>

    <!-- Add Dialog -->
    <el-dialog v-model="showDialog" title="Thêm đợt đăng ký" width="600px" @closed="resetForm">
      <el-form :model="form" label-width="120px">
        <el-form-item label="Học kỳ">
          <el-select v-model="form.semesterId" placeholder="Chọn học kỳ" style="width: 100%">
            <el-option v-for="sem in semesters" :key="sem.id" :label="sem.name" :value="sem.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Thời gian">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="đến"
            start-placeholder="Bắt đầu"
            end-placeholder="Kết thúc"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">Hủy</el-button>
        <el-button type="primary" @click="savePeriod" :loading="saving">Lưu</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'

const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const periods = ref([])
const semesters = ref([])
const pagination = ref({ page: 1, total: 0 })
const dateRange = ref([])

const form = reactive({
  semesterId: '',
  startTime: '',
  endTime: ''
})

const resetForm = () => {
  form.semesterId = ''
  dateRange.value = []
}

const formatDateTime = (isoString) => {
  if (!isoString) return ''
  return new Date(isoString).toLocaleString('vi-VN')
}

const loadPeriods = async () => {
  loading.value = true
  try {
    const response = await api.get('/api/admin/registration-periods', {
      params: { page: pagination.value.page - 1, size: 10 }
    })
    if (response.data.success) {
      periods.value = response.data.data.items || []
      pagination.value.total = response.data.data.totalItems || 0
    }
  } catch (error) {
    ElMessage.error('Không thể tải danh sách đợt đăng ký')
  }
  loading.value = false
}

const loadSemesters = async () => {
  try {
    const response = await api.get('/api/admin/semesters', { params: { size: 100 } })
    if (response.data.success) {
      semesters.value = response.data.data.items || []
    }
  } catch (error) {
    console.error(error)
  }
}

const savePeriod = async () => {
  if (!form.semesterId || !dateRange.value || dateRange.value.length < 2) {
    ElMessage.warning('Vui lòng điền đầy đủ thông tin')
    return
  }

  saving.value = true
  try {
    const payload = {
      semesterId: form.semesterId,
      startTime: dateRange.value[0],
      endTime: dateRange.value[1]
    }
    const response = await api.post('/api/admin/registration-periods', payload)
    if (response.data.success) {
      ElMessage.success('Thêm thành công')
      showDialog.value = false
      loadPeriods()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || 'Lưu thất bại')
  }
  saving.value = false
}

const toggleStatus = async (row, activate) => {
  row.processing = true
  try {
    const endpoint = activate ? 'activate' : 'deactivate'
    await api.put(`/api/admin/registration-periods/${row.id}/${endpoint}`)
    ElMessage.success(activate ? 'Đã kích hoạt' : 'Đã vô hiệu hóa')
    row.active = activate
  } catch (error) {
    ElMessage.error('Thay đổi trạng thái thất bại')
  }
  row.processing = false
}

onMounted(() => {
  loadPeriods()
  loadSemesters()
})
</script>
