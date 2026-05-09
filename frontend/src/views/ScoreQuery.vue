<template>
  <div class="score-query animate-fade-in-up">
    <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
      <div class="flex flex-col md:flex-row md:items-center justify-between mb-8 space-y-4 md:space-y-0 p-2">
        <div class="flex items-center">
          <div class="w-1.5 h-6 bg-blue-600 rounded-full mr-4 shadow-lg shadow-blue-200"></div>
          <h3 class="text-2xl font-bold text-gray-800 tracking-tight">成绩查询</h3>
        </div>
        <div class="flex items-center space-x-4 flex-wrap">
          <el-select v-model="filterCourseId" placeholder="筛选课程" clearable class="!w-44 custom-select" filterable>
            <el-option v-for="c in courseList" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
          <el-select v-model="filterStudentId" placeholder="筛选学生" clearable class="!w-44 custom-select" filterable>
            <el-option v-for="s in studentList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
          <el-input v-model="searchKeyword" placeholder="搜索姓名/学号/课程..." class="!w-56 custom-search" :prefix-icon="Search" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
          <el-button type="primary" :icon="Search" @click="handleSearch" class="!rounded-xl px-6 !h-11 shadow-md shadow-blue-100">查询</el-button>
          <el-button type="success" :icon="Download" @click="handleExport" class="!rounded-xl px-6 !h-11 shadow-md shadow-emerald-100 !bg-emerald-500 !border-emerald-500">导出Excel</el-button>
        </div>
      </div>

      <div class="table-container bg-white rounded-xl overflow-hidden border border-gray-50">
        <el-table v-loading="loading" :data="scoreList" style="width: 100%" :header-cell-style="{ background: '#f8fafc', fontWeight: 'bold', color: '#475569', height: '64px', fontSize: '14px' }" highlight-current-row class="custom-table" id="score-table">
          <el-table-column prop="studentNo" label="学号" min-width="120">
            <template #default="{ row }">
              <span class="font-mono font-medium text-gray-500">{{ row.studentNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="studentName" label="姓名" min-width="110">
            <template #default="{ row }">
              <div class="font-bold text-gray-900">{{ row.studentName }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="courseNo" label="课程编号" min-width="110">
            <template #default="{ row }">
              <span class="font-mono text-gray-500">{{ row.courseNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="courseName" label="课程名称" min-width="140" />
          <el-table-column prop="credit" label="学分" width="80">
            <template #default="{ row }">
              <el-tag effect="light" class="!rounded-full !text-[11px] !border-none bg-blue-50 text-blue-600">{{ row.credit }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="usualScore" label="平时成绩" width="100" />
          <el-table-column prop="examScore" label="期末成绩" width="100" />
          <el-table-column label="总评" width="120">
            <template #default="{ row }">
              <div class="flex items-center">
                <span class="font-bold" :class="row.totalScore >= 60 ? 'text-emerald-600' : 'text-red-500'">{{ row.totalScore }}</span>
                <el-tag v-if="row.totalScore" :type="getScoreTagType(row.totalScore)" effect="light" class="ml-1 !rounded-full !text-[10px] !px-2 !border-none">{{ getScoreLevel(row.totalScore) }}</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
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
            <el-empty description="未检索到相关成绩记录" :image-size="160" class="py-12" />
          </template>
        </el-table>
      </div>

      <div class="flex justify-between items-center mt-10 p-5 bg-gray-50/70 rounded-2xl border border-gray-100">
        <div class="text-sm text-gray-500 font-medium">显示第 {{(currentPage-1)*pageSize + 1}} 到 {{Math.min(currentPage*pageSize, total)}} 条，共 {{total}} 条记录</div>
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]" layout="prev, pager, next, sizes" :total="total" background class="custom-pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="修改成绩" width="500px" class="custom-dialog" :close-on-click-modal="false" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="p-2">
        <el-form-item label="平时成绩">
          <el-input-number v-model="form.usualScore" :min="0" :max="100" :precision="1" class="!w-full custom-number-input" @change="calcFormTotal" />
        </el-form-item>
        <el-form-item label="期末成绩">
          <el-input-number v-model="form.examScore" :min="0" :max="100" :precision="1" class="!w-full custom-number-input" @change="calcFormTotal" />
        </el-form-item>
        <el-form-item label="总评成绩">
          <div class="text-2xl font-bold" :class="form.totalScore >= 60 ? 'text-emerald-600' : 'text-red-500'">{{ form.totalScore }}</div>
        </el-form-item>
        <div class="mt-6 flex justify-end space-x-4 border-t border-gray-100 pt-6">
          <el-button @click="dialogVisible = false" class="!rounded-xl px-8 !h-12 !border-gray-200 hover:!bg-gray-50 !text-gray-500 font-bold">取消</el-button>
          <el-button type="primary" :loading="submitting" class="!rounded-xl px-10 !h-12 !bg-blue-600 !border-blue-600 shadow-lg shadow-blue-100 font-bold" @click="handleSubmit">保存修改</el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCourseList } from '../api/course'
import { getStudentList } from '../api/student'
import { getScorePage, updateScore, deleteScore } from '../api/score'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Edit, Delete, Download } from '@element-plus/icons-vue'

const loading = ref(false)
const scoreList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const filterCourseId = ref(null)
const filterStudentId = ref(null)
const courseList = ref([])
const studentList = ref([])

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = ref({ id: null, studentId: null, courseId: null, usualScore: 0, examScore: 0, totalScore: 0 })

const rules = {
  usualScore: [{ required: true, message: '请输入平时成绩', trigger: 'blur' }],
  examScore: [{ required: true, message: '请输入期末成绩', trigger: 'blur' }]
}

const getScoreTagType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return ''
  if (score >= 70) return 'warning'
  if (score >= 60) return 'info'
  return 'danger'
}

const getScoreLevel = (score) => {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 70) return '中等'
  if (score >= 60) return '及格'
  return '不及格'
}

const calcFormTotal = () => {
  const u = form.value.usualScore || 0
  const e = form.value.examScore || 0
  form.value.totalScore = Math.round((u * 0.3 + e * 0.7) * 100) / 100
}

const fetchScores = async () => {
  loading.value = true
  try {
    const res = await getScorePage({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      studentId: filterStudentId.value || undefined,
      courseId: filterCourseId.value || undefined,
      keyword: searchKeyword.value || undefined
    })
    if (res.code === 200) {
      scoreList.value = res.data.records
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
  fetchScores()
}

const handleEdit = (row) => {
  form.value = {
    id: row.id,
    studentId: row.studentId,
    courseId: row.courseId,
    usualScore: row.usualScore,
    examScore: row.examScore,
    totalScore: row.totalScore
  }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定删除 [${row.studentName}] 的 [${row.courseName}] 成绩?`,
    '删除确认',
    { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning', roundButton: true }
  ).then(async () => {
    try {
      await deleteScore(row.id)
      ElMessage.success({ message: '删除成功', plain: true })
      fetchScores()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    calcFormTotal()
    await updateScore(form.value)
    ElMessage.success({ message: '修改成功', plain: true })
    dialogVisible.value = false
    fetchScores()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleExport = () => {
  if (scoreList.value.length === 0) {
    ElMessage.warning('当前没有数据可导出')
    return
  }
  const headers = ['学号', '姓名', '课程编号', '课程名称', '学分', '平时成绩', '期末成绩', '总评成绩']
  const rows = scoreList.value.map(r => [r.studentNo, r.studentName, r.courseNo, r.courseName, r.credit, r.usualScore, r.examScore, r.totalScore])
  const csvContent = '\uFEFF' + [headers, ...rows].map(r => r.join(',')).join('\n')
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `成绩查询_${new Date().toLocaleDateString()}.csv`
  link.click()
  URL.revokeObjectURL(link.href)
  ElMessage.success({ message: '导出成功', plain: true })
}

const handleSizeChange = (val) => { pageSize.value = val; fetchScores() }
const handleCurrentChange = (val) => { currentPage.value = val; fetchScores() }

onMounted(async () => {
  try {
    const [cRes, sRes] = await Promise.all([getCourseList(), getStudentList()])
    courseList.value = cRes.data || []
    studentList.value = sRes.data || []
  } catch (error) {
    console.error(error)
  }
  fetchScores()
})
</script>

<style scoped>
.score-query { --el-color-primary: #3b82f6; --el-color-success: #10b981; --el-color-danger: #ef4444; }
.shadow-premium { box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.04) !important; }
.custom-search :deep(.el-input__wrapper) { background-color: #f1f5f9; border-radius: 14px; box-shadow: none !important; border: 2px solid transparent; padding-left: 16px; transition: all 0.3s; }
.custom-search :deep(.el-input__wrapper.is-focus) { background-color: white; border-color: #3b82f6; box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.08) !important; }
.custom-select :deep(.el-input__wrapper) { background-color: #f8fafc; border-radius: 12px; box-shadow: none !important; border: 1px solid #e2e8f0; transition: all 0.3s; }
.custom-select :deep(.el-input__wrapper.is-focus) { background-color: white; border-color: #3b82f6; box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.08) !important; }
.custom-table :deep(.el-table__row) { height: 64px; transition: background-color 0.2s; }
.custom-table :deep(.el-table__row:hover) { background-color: #f8fafc !important; }
.custom-table :deep(.el-table__cell) { border-bottom: 1px solid #f1f5f9 !important; padding: 12px 0; }
.custom-pagination :deep(.el-pager li) { border-radius: 12px; margin: 0 4px; background-color: white !important; color: #64748b; border: 1px solid #e2e8f0; font-weight: 600; height: 36px; min-width: 36px; line-height: 36px; }
.custom-pagination :deep(.el-pager li.is-active) { background: #3b82f6 !important; color: white !important; border-color: #3b82f6; box-shadow: 0 4px 10px rgba(59, 130, 246, 0.3); }
.custom-pagination :deep(.btn-prev), .custom-pagination :deep(.btn-next) { background-color: white !important; border-radius: 12px; border: 1px solid #e2e8f0; height: 36px; width: 36px; }
.custom-dialog :deep(.el-dialog) { border-radius: 28px; overflow: hidden; box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.2); }
.custom-dialog :deep(.el-dialog__header) { padding: 28px 36px; margin-right: 0; border-bottom: 1px solid #f1f5f9; background: #fdfdfd; }
.custom-dialog :deep(.el-dialog__title) { font-size: 22px; font-weight: 800; color: #0f172a; }
.custom-number-input :deep(.el-input__wrapper) { border-radius: 12px; background-color: #f8fafc; }
.animate-fade-in-up { animation: fadeInUp 0.7s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>
