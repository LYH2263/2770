<template>
  <div class="course-list animate-fade-in-up">
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
      <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center transform transition-all hover:scale-[1.02]">
        <div class="w-12 h-12 bg-purple-50 rounded-xl flex items-center justify-center mr-4 text-purple-600 shadow-inner">
          <el-icon class="text-2xl"><Reading /></el-icon>
        </div>
        <div>
          <div class="text-sm text-gray-400 mb-1">课程总数</div>
          <div class="text-2xl font-bold text-gray-800">{{ total }}</div>
        </div>
      </div>
    </div>

    <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
      <div class="flex flex-col md:flex-row md:items-center justify-between mb-8 space-y-4 md:space-y-0 p-2">
        <div class="flex items-center">
          <div class="w-1.5 h-6 bg-purple-600 rounded-full mr-4 shadow-lg shadow-purple-200"></div>
          <h3 class="text-2xl font-bold text-gray-800 tracking-tight">课程管理</h3>
        </div>

        <div class="flex items-center space-x-4">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程名称、编号、教师..."
            class="!w-72 custom-search"
            :prefix-icon="Search"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" :icon="Search" @click="handleSearch" class="!rounded-xl px-6 !h-11 shadow-md shadow-blue-100">查询</el-button>
          <el-button type="success" :icon="Plus" @click="handleAdd" class="!rounded-xl px-6 !h-11 shadow-md shadow-emerald-100 !bg-emerald-500 !border-emerald-500">
            添加课程
          </el-button>
        </div>
      </div>

      <div class="table-container bg-white rounded-xl overflow-hidden border border-gray-50">
        <el-table
          v-loading="loading"
          :data="courseList"
          style="width: 100%"
          :header-cell-style="{ background: '#f8fafc', fontWeight: 'bold', color: '#475569', height: '64px', fontSize: '14px' }"
          highlight-current-row
          class="custom-table"
        >
          <el-table-column prop="courseNo" label="课程编号" min-width="140">
            <template #default="{ row }">
              <span class="font-mono font-medium text-gray-500">{{ row.courseNo }}</span>
            </template>
          </el-table-column>
          <el-table-column label="课程名称" min-width="200">
            <template #default="{ row }">
              <div class="font-bold text-gray-900">{{ row.courseName }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="credit" label="学分" width="100" />
          <el-table-column prop="teacher" label="授课教师" min-width="150" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <div class="flex space-x-3">
                <el-button link type="primary" @click="handleEdit(scope.row)" class="!font-bold hover:!text-blue-700">
                  <el-icon class="mr-1"><Edit /></el-icon>修改
                </el-button>
                <el-button link type="danger" @click="handleDelete(scope.row)" class="!font-bold hover:!text-red-700">
                  <el-icon class="mr-1"><Delete /></el-icon>删除
                </el-button>
              </div>
            </template>
          </el-table-column>
          
          <template #empty>
            <el-empty description="未检索到相关课程记录" :image-size="160" class="py-12" />
          </template>
        </el-table>
      </div>

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

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '修改课程' : '添加课程'"
      width="560px"
      class="custom-dialog"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        v-if="dialogVisible"
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        class="pt-4"
      >
        <el-form-item label="课程编号" prop="courseNo">
          <el-input v-model="formData.courseNo" placeholder="请输入课程编号" class="!rounded-xl" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="formData.courseName" placeholder="请输入课程名称" class="!rounded-xl" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="formData.credit" :min="1" :max="10" class="w-full" />
        </el-form-item>
        <el-form-item label="授课教师" prop="teacher">
          <el-input v-model="formData.teacher" placeholder="请输入授课教师" class="!rounded-xl" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end space-x-4">
          <el-button @click="dialogVisible = false" class="!rounded-xl px-6">取消</el-button>
          <el-button type="primary" @click="handleSubmit" class="!rounded-xl px-6 !bg-blue-500 !border-blue-500">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCoursePage, addCourse, updateCourse, deleteCourse } from '../api/course'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete, Reading } from '@element-plus/icons-vue'

const loading = ref(false)
const courseList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const formData = ref({})

const rules = {
  courseNo: [{ required: true, message: '请输入课程编号', trigger: 'blur' }],
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }]
}

const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await getCoursePage({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    })
    if (res.code === 200) {
      courseList.value = res.data.records
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
  fetchCourses()
}

const handleAdd = () => {
  isEdit.value = false
  formData.value = { credit: 3 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  formData.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `此操作将永久删除课程 [${row.courseName}]，是否继续?`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      roundButton: true,
      confirmButtonClass: '!bg-danger !border-danger px-6'
    }
  ).then(async () => {
    try {
      const res = await deleteCourse(row.id)
      if (res.code === 200) {
        ElMessage.success({ message: '删除成功', plain: true })
        fetchCourses()
      }
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    const res = isEdit.value
      ? await updateCourse(formData.value)
      : await addCourse(formData.value)
    if (res.code === 200) {
      ElMessage.success({ message: isEdit.value ? '更新成功' : '添加成功', plain: true })
      dialogVisible.value = false
      fetchCourses()
    }
  } catch (error) {
    if (error !== false) {
      console.error(error)
    }
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchCourses()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchCourses()
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.course-list {
  --el-color-primary: #8b5cf6;
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
  border-color: #8b5cf6;
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.08) !important;
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
  background: #8b5cf6 !important;
  color: white !important;
  border-color: #8b5cf6;
  box-shadow: 0 4px 10px rgba(139, 92, 246, 0.3);
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
