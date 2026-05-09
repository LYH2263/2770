<template>
  <div class="score-query animate-fade-in-up">
    <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
      <div class="flex flex-col md:flex-row md:items-center justify-between mb-8 space-y-4 md:space-y-0 p-2">
        <div class="flex items-center">
          <div class="w-1.5 h-6 bg-cyan-500 rounded-full mr-4 shadow-lg shadow-cyan-200"></div>
          <h3 class="text-2xl font-bold text-gray-800 tracking-tight">成绩查询</h3>
        </div>

        <div class="flex items-center space-x-4">
          <el-button
            type="success"
            :icon="Download"
            @click="handleExport"
            :disabled="scoreList.length === 0"
            class="!rounded-xl px-6 !h-11 shadow-md shadow-emerald-100 !bg-emerald-500 !border-emerald-500"
          >
            导出Excel
          </el-button>
        </div>
      </div>

      <div class="flex flex-wrap gap-4 mb-8 p-5 bg-gray-50/50 rounded-2xl">
        <el-select
          v-model="filterStudentId"
          placeholder="选择学生"
          clearable
          class="!w-56"
          @change="handleFilter"
        >
          <el-option
            v-for="student in studentList"
            :key="student.id"
            :label="`${student.name} (${student.studentNo})`"
            :value="student.id"
          />
        </el-select>
        <el-select
          v-model="filterCourseId"
          placeholder="选择课程"
          clearable
          class="!w-56"
          @change="handleFilter"
        >
          <el-option
            v-for="course in courseList"
            :key="course.id"
            :label="`${course.courseName} (${course.courseNo})`"
            :value="course.id"
          />
        </el-select>
        <el-button
          type="primary"
          :icon="Search"
          @click="handleFilter"
          class="!rounded-xl px-6 !h-11 shadow-md shadow-blue-100"
        >
          查询
        </el-button>
        <el-button
          @click="handleReset"
          class="!rounded-xl px-6 !h-11"
        >
          重置
        </el-button>
      </div>

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
          <el-table-column label="学号" min-width="140">
            <template #default="{ row }">
              <span class="font-mono font-medium text-gray-500">{{ row.studentNo }}</span>
            </template>
          </el-table-column>
          <el-table-column label="姓名" min-width="130">
            <template #default="{ row }">
              <div class="font-bold text-gray-900">{{ row.studentName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="课程名称" min-width="160">
            <template #default="{ row }">
              <div class="font-medium text-gray-800">{{ row.courseName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="平时成绩" width="120" align="center" />
          <el-table-column label="期末成绩" width="120" align="center" />
          <el-table-column label="总评成绩" width="140" align="center">
            <template #default="{ row }">
              <div
                class="text-lg font-bold"
                :class="getScoreClass(row.totalScore)"
              >
                {{ row.totalScore !== null && row.totalScore !== undefined ? row.totalScore.toFixed(1) : '-' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="学分" width="100" align="center" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag
                :type="(row.totalScore !== null && row.totalScore >= 60) ? 'success' : 'danger'"
                effect="light"
                class="!rounded-full px-3 !font-bold !text-[11px] !border-none"
              >
                {{ (row.totalScore !== null && row.totalScore >= 60) ? '通过' : '挂科' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #empty>
        <el-empty description="未检索到相关成绩记录" :image-size="160" class="py-12" />
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCourseList } from '../api/course'
import { getStudentList } from '../api/student'
import { getScorePage } from '../api/score'
import { ElMessage } from 'element-plus'
import { Search, Download } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'

const loading = ref(false)
const scoreList = ref([])
const courseList = ref([])
const studentList = ref([])
const filterStudentId = ref(null)
const filterCourseId = ref(null)

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
    const params = {
      pageNum: 1,
      pageSize: 1000
    }
    if (filterStudentId.value) {
      params.studentId = filterStudentId.value
    }
    if (filterCourseId.value) {
      params.courseId = filterCourseId.value
    }
    const res = await getScorePage(params)
    if (res.code === 200) {
      scoreList.value = res.data.records || []
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  fetchScores()
}

const handleReset = () => {
  filterStudentId.value = null
  filterCourseId.value = null
  fetchScores()
}

const getScoreClass = (score) => {
  if (score === null || score === undefined) return 'text-gray-400'
  if (score >= 90) return 'text-emerald-600'
  if (score >= 80) return 'text-blue-600'
  if (score >= 70) return 'text-cyan-600'
  if (score >= 60) return 'text-amber-600'
  return 'text-red-600'
}

const handleExport = () => {
  if (scoreList.value.length === 0) {
    ElMessage.warning('没有数据可导出')
    return
  }

  const exportData = scoreList.value.map((item, index) => ({
    '序号': index + 1,
    '学号': item.studentNo,
    '姓名': item.studentName,
    '课程名称': item.courseName,
    '平时成绩': item.regularScore,
    '期末成绩': item.finalScore,
    '总评成绩': item.totalScore ? item.totalScore.toFixed(1) : '',
    '学分': item.credit,
    '状态': (item.totalScore !== null && item.totalScore >= 60) ? '通过' : '挂科'
  }))

  const worksheet = XLSX.utils.json_to_sheet(exportData)
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, '成绩表')
  XLSX.writeFile(workbook, `成绩表_${new Date().toLocaleDateString()}.xlsx`)

  ElMessage.success({
    message: '导出成功',
    plain: true
  })
}

onMounted(() => {
  fetchCourses()
  fetchStudents()
  fetchScores()
})
</script>

<style scoped>
.score-query {
  --el-color-primary: #06b6d4;
  --el-color-success: #10b981;
  --el-color-danger: #ef4444;
}

.shadow-premium {
  box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.04) !important;
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
