<template>
  <div class="course-list animate-fade-in-up">
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
      <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center transform transition-all hover:scale-[1.02]">
        <div class="w-12 h-12 bg-blue-50 rounded-xl flex items-center justify-center mr-4 text-blue-600 shadow-inner">
          <el-icon class="text-2xl"><Reading /></el-icon>
        </div>
        <div>
          <div class="text-sm text-gray-400 mb-1">课程总数</div>
          <div class="text-2xl font-bold text-gray-800">{{ total }}</div>
        </div>
      </div>
      <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center transform transition-all hover:scale-[1.02]">
        <div class="w-12 h-12 bg-emerald-50 rounded-xl flex items-center justify-center mr-4 text-emerald-600 shadow-inner">
          <el-icon class="text-2xl"><Coin /></el-icon>
        </div>
        <div>
          <div class="text-sm text-gray-400 mb-1">总学分</div>
          <div class="text-2xl font-bold text-gray-800">{{ totalCredits }}</div>
        </div>
      </div>
      <div class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center transform transition-all hover:scale-[1.02]">
        <div class="w-12 h-12 bg-purple-50 rounded-xl flex items-center justify-center mr-4 text-purple-600 shadow-inner">
          <el-icon class="text-2xl"><User /></el-icon>
        </div>
        <div>
          <div class="text-sm text-gray-400 mb-1">授课教师</div>
          <div class="text-2xl font-bold text-gray-800">{{ teacherCount }}</div>
        </div>
      </div>
    </div>

    <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
      <div class="flex flex-col md:flex-row md:items-center justify-between mb-8 space-y-4 md:space-y-0 p-2">
        <div class="flex items-center">
          <div class="w-1.5 h-6 bg-blue-600 rounded-full mr-4 shadow-lg shadow-blue-200"></div>
          <h3 class="text-2xl font-bold text-gray-800 tracking-tight">课程库管理</h3>
        </div>
        <div class="flex items-center space-x-4">
          <el-input v-model="searchKeyword" placeholder="搜索课程名称、编号、教师..." class="!w-72 custom-search" :prefix-icon="Search" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
          <el-button type="primary" :icon="Search" @click="handleSearch" class="!rounded-xl px-6 !h-11 shadow-md shadow-blue-100">查询</el-button>
          <el-button type="success" :icon="Plus" @click="handleAdd" class="!rounded-xl px-6 !h-11 shadow-md shadow-emerald-100 !bg-emerald-500 !border-emerald-500">新增课程</el-button>
        </div>
      </div>

      <div class="table-container bg-white rounded-xl overflow-hidden border border-gray-50">
        <el-table v-loading="loading" :data="courseList" style="width: 100%" :header-cell-style="{ background: '#f8fafc', fontWeight: 'bold', color: '#475569', height: '64px', fontSize: '14px' }" highlight-current-row class="custom-table">
          <el-table-column prop="courseNo" label="课程编号" min-width="120">
            <template #default="{ row }">
              <span class="font-mono font-medium text-gray-500">{{ row.courseNo }}</span>
            </template>
          </el-table-column>
          <el-table-column label="课程名称" min-width="160">
            <template #default="{ row }">
              <div class="font-bold text-gray-900">{{ row.courseName }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="credit" label="学分" width="100">
            <template #default="{ row }">
              <el-tag effect="light" class="!rounded-full px-3 !font-bold !text-[11px] !border-none bg-blue-50 text-blue-600">{{ row.credit }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="teacher" label="授课教师" min-width="130" />
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
            <el-empty description="未检索到相关课程记录" :image-size="160" class="py-12" />
          </template>
        </el-table>
      </div>

      <div class="flex justify-between items-center mt-10 p-5 bg-gray-50/70 rounded-2xl border border-gray-100">
        <div class="text-sm text-gray-500 font-medium">显示第 {{(currentPage-1)*pageSize + 1}} 到 {{Math.min(currentPage*pageSize, total)}} 条，共 {{total}} 条记录</div>
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]" layout="prev, pager, next, sizes" :total="total" background class="custom-pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '修改课程信息' : '新增课程'" width="560px" class="custom-dialog" :close-on-click-modal="false" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="p-2">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-2">
          <el-form-item label="课程编号" prop="courseNo">
            <el-input v-model="form.courseNo" placeholder="请输入课程编号" class="custom-input" />
          </el-form-item>
          <el-form-item label="课程名称" prop="courseName">
            <el-input v-model="form.courseName" placeholder="请输入课程名称" class="custom-input" />
          </el-form-item>
          <el-form-item label="学分" prop="credit">
            <el-input-number v-model="form.credit" :min="0.5" :max="10" :step="0.5" class="!w-full custom-number-input" />
          </el-form-item>
          <el-form-item label="授课教师" prop="teacher">
            <el-input v-model="form.teacher" placeholder="请输入授课教师" class="custom-input" />
          </el-form-item>
        </div>
        <div class="mt-8 flex justify-end space-x-4 border-t border-gray-100 pt-6">
          <el-button @click="dialogVisible = false" class="!rounded-xl px-8 !h-12 !border-gray-200 hover:!bg-gray-50 !text-gray-500 font-bold">取消返回</el-button>
          <el-button type="primary" :loading="submitting" class="!rounded-xl px-10 !h-12 !bg-blue-600 !border-blue-600 shadow-lg shadow-blue-100 font-bold transform active:scale-95 transition-all" @click="handleSubmit">确认保存</el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCoursePage, addCourse, updateCourse, deleteCourse } from '../api/course'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete, Reading, Coin, User } from '@element-plus/icons-vue'

const loading = ref(false)
const courseList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = ref({
  id: null,
  courseNo: '',
  courseName: '',
  credit: 3.0,
  teacher: ''
})

const rules = {
  courseNo: [{ required: true, message: '课程编号不能为空', trigger: 'blur' }],
  courseName: [{ required: true, message: '课程名称不能为空', trigger: 'blur' }],
  credit: [{ required: true, message: '学分不能为空', trigger: 'blur' }]
}

const totalCredits = computed(() => {
  return courseList.value.reduce((sum, c) => sum + (c.credit || 0), 0).toFixed(1)
})

const teacherCount = computed(() => {
  const teachers = new Set(courseList.value.map(c => c.teacher).filter(Boolean))
  return teachers.size
})

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
  form.value = { id: null, courseNo: '', courseName: '', credit: 3.0, teacher: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `此操作将永久删除课程 [${row.courseName}]，是否继续?`,
    '危险操作警告',
    { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'error', roundButton: true, confirmButtonClass: '!bg-danger !border-danger px-6' }
  ).then(async () => {
    try {
      const res = await deleteCourse(row.id)
      if (res.code === 200) {
        ElMessage.success({ message: '课程已成功删除', plain: true })
        fetchCourses()
      }
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const apiFunc = isEdit.value ? updateCourse : addCourse
        const res = await apiFunc(form.value)
        if (res.code === 200) {
          ElMessage.success({ message: isEdit.value ? '修改成功' : '新增成功', plain: true })
          dialogVisible.value = false
          fetchCourses()
        }
      } catch (error) {
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleSizeChange = (val) => { pageSize.value = val; fetchCourses() }
const handleCurrentChange = (val) => { currentPage.value = val; fetchCourses() }

onMounted(() => { fetchCourses() })
</script>

<style scoped>
.course-list {
  --el-color-primary: #3b82f6;
  --el-color-success: #10b981;
  --el-color-danger: #ef4444;
}
.shadow-premium { box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.04) !important; }
.custom-search :deep(.el-input__wrapper) { background-color: #f1f5f9; border-radius: 14px; box-shadow: none !important; border: 2px solid transparent; padding-left: 16px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
.custom-search :deep(.el-input__wrapper.is-focus) { background-color: white; border-color: #3b82f6; box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.08) !important; }
.custom-table :deep(.el-table__row) { height: 72px; transition: background-color 0.2s; }
.custom-table :deep(.el-table__row:hover) { background-color: #f8fafc !important; }
.custom-table :deep(.el-table__cell) { border-bottom: 1px solid #f1f5f9 !important; padding: 12px 0; }
.custom-pagination :deep(.el-pager li) { border-radius: 12px; margin: 0 4px; background-color: white !important; color: #64748b; border: 1px solid #e2e8f0; font-weight: 600; height: 36px; min-width: 36px; line-height: 36px; transition: all 0.2s; }
.custom-pagination :deep(.el-pager li.is-active) { background: #3b82f6 !important; color: white !important; border-color: #3b82f6; box-shadow: 0 4px 10px rgba(59, 130, 246, 0.3); }
.custom-pagination :deep(.btn-prev), .custom-pagination :deep(.btn-next) { background-color: white !important; border-radius: 12px; border: 1px solid #e2e8f0; height: 36px; width: 36px; }
.custom-dialog :deep(.el-dialog) { border-radius: 28px; overflow: hidden; box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.2); }
.custom-dialog :deep(.el-dialog__header) { padding: 28px 36px; margin-right: 0; border-bottom: 1px solid #f1f5f9; background: #fdfdfd; }
.custom-dialog :deep(.el-dialog__title) { font-size: 22px; font-weight: 800; color: #0f172a; }
.custom-input :deep(.el-input__wrapper) { background-color: #f8fafc; border-radius: 12px; box-shadow: none !important; border: 1px solid #e2e8f0; padding: 8px 16px; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); }
.custom-input :deep(.el-input__wrapper.is-focus) { background-color: white; border-color: #3b82f6; box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.08) !important; }
.custom-number-input :deep(.el-input__wrapper) { border-radius: 12px; background-color: #f8fafc; }
.animate-fade-in-up { animation: fadeInUp 0.7s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>
