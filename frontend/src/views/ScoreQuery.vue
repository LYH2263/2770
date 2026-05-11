<template>
  <div class="score-query animate-fade-in-up">
    <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white mb-6">
      <div class="flex flex-col md:flex-row md:items-center justify-between space-y-4 md:space-y-0 p-2">
        <div class="flex items-center">
          <div class="w-1.5 h-6 bg-emerald-600 rounded-full mr-4 shadow-lg shadow-emerald-200"></div>
          <h3 class="text-2xl font-bold text-gray-800 tracking-tight">成绩查询</h3>
        </div>

        <div class="flex items-center space-x-4 flex-wrap gap-y-4">
          <el-select
            v-model="filterStudentId"
            placeholder="选择学生"
            clearable
            class="!w-48"
            @change="handleFilterChange"
          >
            <el-option
              v-for="student in studentList"
              :key="student.id"
              :label="student.name"
              :value="student.id"
            />
          </el-select>
          <el-select
            v-model="filterCourseId"
            placeholder="选择课程"
            clearable
            class="!w-48"
            @change="handleFilterChange"
          >
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索学生、课程..."
            class="!w-56 custom-search"
            :prefix-icon="Search"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" :icon="Search" @click="handleSearch" class="!rounded-xl px-6 !h-11 shadow-md shadow-blue-100">查询</el-button>
          <el-button type="success" :icon="Download" @click="handleExport" class="!rounded-xl px-6 !h-11 shadow-md shadow-emerald-100 !bg-emerald-500 !border-emerald-500">
            导出Excel
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
      <div class="table-container bg-white rounded-xl overflow-hidden border border-gray-50">
        <el-table
          v-loading="loading"
          :data="scoreList"
          style="width: 100%"
          :header-cell-style="{ background: '#f8fafc', fontWeight: 'bold', color: '#475569', height: '64px', fontSize: '14px' }"
          highlight-current-row
          class="custom-table"
        >
          <el-table-column type="index" label="序号" width="80" align="center" />
          <el-table-column prop="studentNo" label="学号" min-width="140">
            <template #default="{ row }">
              <span class="font-mono font-medium text-gray-500">{{ row.studentNo }}</span>
            </template>
          </el-table-column>
          <el-table-column label="姓名" min-width="120">
            <template #default="{ row }">
              <div class="font-bold text-gray-900">{{ row.studentName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="课程" min-width="180">
            <template #default="{ row }">
              <div class="font-medium text-gray-700">{{ row.courseName }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="credit" label="学分" width="100" align="center" />
          <el-table-column label="平时成绩" width="120" align="center">
            <template #default="{ row }">
              {{ formatScore(row.dailyScore) }}
            </template>
          </el-table-column>
          <el-table-column label="期末成绩" width="120" align="center">
            <template #default="{ row }">
              {{ formatScore(row.finalScore) }}
            </template>
          </el-table-column>
          <el-table-column label="总评成绩" width="140" align="center">
            <template #default="{ row }">
              <div :class="getScoreClass(row.totalScore)" class="text-lg font-bold">
                {{ formatScore(row.totalScore) }}
              </div>
            </template>
          </el-table-column>
          
          <template #empty>
            <el-empty description="未检索到相关成绩记录" :image-size="160" class="py-12" />
          </template>
        </el-table>
      </div>

      <div class="flex justify-between items-center mt-10 p-5 bg-gray-50/70 rounded-2xl border border-gray-100">
        <div class="text-sm text-gray-500 font-medium">
          共 {{ scoreList.length }} 条记录
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCourseList } from '../api/course'
import { getStudentList } from '../api/student'
import { getScores } from '../api/score'
import { ElMessage } from 'element-plus'
import { Search, Download } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'

const loading = ref(false)
const courseList = ref([])
const studentList = ref([])
const scoreList = ref([])
const filterStudentId = ref(null)
const filterCourseId = ref(null)
const searchKeyword = ref('')

const fetchCourses = async () => {
  try {
    const res = await getCourseList()
    if (res.code === 200) {
      courseList.value = res.data
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchStudents = async () => {
  try {
    const res = await getStudentList()
    if (res.code === 200) {
      studentList.value = res.data
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchScores = async () => {
  loading.value = true
  try {
    const res = await getScores({
      studentId: filterStudentId.value,
      courseId: filterCourseId.value,
      keyword: searchKeyword.value
    })
    if (res.code === 200) {
      scoreList.value = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  fetchScores()
}

const handleSearch = () => {
  fetchScores()
}

const handleExport = () => {
  if (scoreList.value.length === 0) {
    ElMessage.warning({ message: '没有可导出的数据', plain: true })
    return
  }

  const data = scoreList.value.map((item, index) => ({
    '序号': index + 1,
    '学号': item.studentNo,
    '姓名': item.studentName,
    '课程': item.courseName,
    '学分': item.credit,
    '平时成绩': item.dailyScore != null ? item.dailyScore : '-',
    '期末成绩': item.finalScore != null ? item.finalScore : '-',
    '总评成绩': item.totalScore != null ? item.totalScore : '-'
  }))

  const ws = XLSX.utils.json_to_sheet(data)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '成绩表')
  XLSX.writeFile(wb, '成绩表.xlsx')
  ElMessage.success({ message: '导出成功', plain: true })
}

const formatScore = (score) => {
  return score != null ? score.toFixed(1) : '-'
}

const getScoreClass = (score) => {
  if (score == null) return 'text-gray-400'
  if (score >= 90) return 'text-green-600'
  if (score >= 80) return 'text-blue-600'
  if (score >= 70) return 'text-yellow-600'
  if (score >= 60) return 'text-orange-600'
  return 'text-red-600'
}

onMounted(() => {
  fetchCourses()
  fetchStudents()
  fetchScores()
})
</script>

<style scoped>
.score-query {
  --el-color-primary: #10b981;
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
  border-color: #10b981;
  box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.08) !important;
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
