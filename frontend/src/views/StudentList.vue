<template>
  <div class="student-list animate-fade-in-up">
    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
       <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center transform transition-all hover:scale-[1.02]">
         <div class="w-12 h-12 bg-blue-50 rounded-xl flex items-center justify-center mr-4 text-blue-600 shadow-inner">
           <el-icon class="text-2xl"><User /></el-icon>
         </div>
         <div>
           <div class="text-sm text-gray-400 mb-1">学生总数</div>
           <div class="text-2xl font-bold text-gray-800">{{ total }}</div>
         </div>
       </div>
    </div>

    <!-- 列表卡片 -->
    <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
      <!-- 列表头部/过滤 -->
      <div class="flex flex-col md:flex-row md:items-center justify-between mb-8 space-y-4 md:space-y-0 p-2">
        <div class="flex items-center">
          <div class="w-1.5 h-6 bg-blue-600 rounded-full mr-4 shadow-lg shadow-blue-200"></div>
          <h3 class="text-2xl font-bold text-gray-800 tracking-tight">学生库管理</h3>
        </div>

        <div class="flex items-center space-x-4">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索姓名、学号、专业..."
            class="!w-72 custom-search"
            :prefix-icon="Search"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" :icon="Search" @click="handleSearch" class="!rounded-xl px-6 !h-11 shadow-md shadow-blue-100">查询</el-button>
          <el-button type="success" :icon="Plus" @click="handleAdd" class="!rounded-xl px-6 !h-11 shadow-md shadow-emerald-100 !bg-emerald-500 !border-emerald-500">
            录入学生
          </el-button>
        </div>
      </div>

      <!-- 数据内容 -->
      <div class="table-container bg-white rounded-xl overflow-hidden border border-gray-50">
        <el-table
          v-loading="loading"
          :data="studentList"
          style="width: 100%"
          :header-cell-style="{ background: '#f8fafc', fontWeight: 'bold', color: '#475569', height: '64px', fontSize: '14px' }"
          highlight-current-row
          class="custom-table"
        >
          <el-table-column prop="studentNo" label="学号" min-width="140">
             <template #default="{ row }">
               <span class="font-mono font-medium text-gray-500">{{ row.studentNo }}</span>
             </template>
          </el-table-column>
          <el-table-column label="姓名" min-width="130">
            <template #default="{ row }">
              <div class="font-bold text-gray-900">{{ row.name }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="gender" label="性别" width="90">
            <template #default="{ row }">
              <div class="flex justify-center">
                <el-tag :type="row.gender === '男' ? '' : 'danger'" effect="light" class="!rounded-full px-3 !font-bold !text-[11px] !border-none">
                  {{ row.gender }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="age" label="年龄" width="100" />
          <el-table-column prop="major" label="专业" min-width="160" show-overflow-tooltip />
          <el-table-column prop="phone" label="联系电话" min-width="160" />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <div class="flex space-x-3">
                <el-button link type="primary" @click="handleEdit(scope.row)" class="!font-bold hover:!text-blue-700">
                  <el-icon class="mr-1"><Edit /></el-icon>修改
                </el-button>
                <el-button link type="danger" @click="handleDelete(scope.row)" class="!font-bold hover:!text-red-700">
                  <el-icon class="mr-1"><Delete /></el-icon>移除
                </el-button>
              </div>
            </template>
          </el-table-column>
          
          <template #empty>
            <el-empty description="未检索到相关学生记录" :image-size="160" class="py-12" />
          </template>
        </el-table>
      </div>

      <!-- 分页区域 -->
      <div class="flex justify-between items-center mt-10 p-5 bg-gray-50/70 rounded-2xl border border-gray-100">
        <div class="text-sm text-gray-500 font-medium">
          显示第 {{(currentPage-1)*pageSize + 1}} 到 {{Math.min(currentPage*pageSize, total)}} 条，共 {{total}} 条记录
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="prev, pager, next, sizes"
          :total="total"
          background
          class="custom-pagination"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 表单弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '修改学生档案' : '录入新学生档案'"
      width="640px"
      class="custom-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <student-form
        v-if="dialogVisible"
        :initial-data="currentStudent"
        @success="handleSuccess"
        @cancel="dialogVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStudentPage, deleteStudent } from '../api/student'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete, User } from '@element-plus/icons-vue'
import StudentForm from '../components/StudentForm.vue'

const loading = ref(false)
const studentList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentStudent = ref(null)

const fetchStudents = async () => {
  loading.value = true
  try {
    const res = await getStudentPage({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    })
    if (res.code === 200) {
      studentList.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchStudents()
}

const handleAdd = () => {
  isEdit.value = false
  currentStudent.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  currentStudent.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `此操作将永久移除学生 [${row.name}] 的所有档案信息，且不可回滚。是否继续?`,
    '危险操作警告',
    {
      confirmButtonText: '确定移除',
      cancelButtonText: '取消',
      type: 'error',
      roundButton: true,
      confirmButtonClass: '!bg-danger !border-danger px-6'
    }
  ).then(async () => {
    try {
      const res = await deleteStudent(row.id)
      if (res.code === 200) {
        ElMessage.success({
          message: '档案已成功移除',
          plain: true
        })
        fetchStudents()
      }
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleSuccess = () => {
  dialogVisible.value = false
  fetchStudents()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchStudents()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchStudents()
}

onMounted(() => {
  fetchStudents()
})
</script>

<style scoped>
.student-list {
  --el-color-primary: #3b82f6;
  --el-color-success: #10b981;
  --el-color-danger: #ef4444;
}

.shadow-premium {
  box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.04) !important;
}

.custom-search :deep(.el-input__wrapper) {
  background-color: #f1f5f9;
  border-radius: 14px;
  box-shadow: none !important;
  border: 2px solid transparent;
  padding-left: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.custom-search :deep(.el-input__wrapper.is-focus) {
  background-color: white;
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.08) !important;
}

.custom-table :deep(.el-table__row) {
  height: 72px;
  transition: background-color 0.2s;
}

.custom-table :deep(.el-table__row:hover) {
  background-color: #f8fafc !important;
}

.custom-table :deep(.el-table__cell) {
  border-bottom: 1px solid #f1f5f9 !important;
  padding: 12px 0;
}

.custom-pagination :deep(.el-pager li) {
  border-radius: 12px;
  margin: 0 4px;
  background-color: white !important;
  color: #64748b;
  border: 1px solid #e2e8f0;
  font-weight: 600;
  height: 36px;
  min-width: 36px;
  line-height: 36px;
  transition: all 0.2s;
}

.custom-pagination :deep(.el-pager li.is-active) {
  background: #3b82f6 !important;
  color: white !important;
  border-color: #3b82f6;
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.3);
}

.custom-pagination :deep(.btn-prev), 
.custom-pagination :deep(.btn-next) {
  background-color: white !important;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  height: 36px;
  width: 36px;
}

.custom-dialog :deep(.el-dialog) {
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.2);
}

.custom-dialog :deep(.el-dialog__header) {
  padding: 28px 36px;
  margin-right: 0;
  border-bottom: 1px solid #f1f5f9;
  background: #fdfdfd;
}

.custom-dialog :deep(.el-dialog__title) {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
}

.animate-fade-in-up {
  animation: fadeInUp 0.7s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
