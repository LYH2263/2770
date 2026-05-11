<template>
  <div class="score-input animate-fade-in-up">
    <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
      <div class="flex flex-col md:flex-row md:items-center justify-between mb-8 space-y-4 md:space-y-0 p-2">
        <div class="flex items-center">
          <div class="w-1.5 h-6 bg-blue-600 rounded-full mr-4 shadow-lg shadow-blue-200"></div>
          <h3 class="text-2xl font-bold text-gray-800 tracking-tight">成绩录入</h3>
        </div>

        <div class="flex items-center space-x-4">
          <el-select
            v-model="selectedCourseId"
            placeholder="选择课程"
            class="!w-64"
            @change="handleCourseChange"
          >
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
          <el-button
            type="primary"
            :icon="DocumentAdd"
            @click="handleSave"
            :disabled="!selectedCourseId || !hasChanges"
            class="!rounded-xl px-6 !h-11 shadow-md shadow-blue-100"
          >
            保存成绩
          </el-button>
        </div>
      </div>

      <div class="mb-6 p-6 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl border border-blue-100">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-sm text-gray-500 mb-1">当前课程</div>
            <div class="text-xl font-bold text-gray-800">
              {{ currentCourse ? currentCourse.courseName : '请选择课程' }}
            </div>
          </div>
          <div v-if="currentCourse" class="text-right">
            <div class="text-sm text-gray-500 mb-1">学分</div>
            <div class="text-lg font-bold text-blue-600">{{ currentCourse.credit }} 学分</div>
          </div>
        </div>
        <div class="mt-4 text-sm text-gray-500">
          <el-icon class="mr-1"><InfoFilled /></el-icon>
          平时成绩占 30%，期末成绩占 70%，总评成绩自动计算
        </div>
      </div>

      <div class="table-container bg-white rounded-xl overflow-hidden border border-gray-50">
        <el-table
          v-loading="loading"
          :data="scoreList"
          style="width: 100%"
          :header-cell-style="{ background: '#f8fafc', fontWeight: 'bold', color: '#475569', height: '64px', fontSize: '14px' }"
          class="custom-table"
          border
        >
          <el-table-column type="index" label="序号" width="80" align="center" />
          <el-table-column prop="studentNo" label="学号" min-width="140">
            <template #default="{ row }">
              <span class="font-mono font-medium text-gray-500">{{ row.studentNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="studentName" label="姓名" min-width="120">
            <template #default="{ row }">
              <div class="font-bold text-gray-900">{{ row.studentName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="平时成绩" width="160">
            <template #default="{ row }">
              <el-input-number
                v-model="row.dailyScore"
                :min="0"
                :max="100"
                :precision="1"
                :controls="false"
                class="w-full"
                @change="handleScoreChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="期末成绩" width="160">
            <template #default="{ row }">
              <el-input-number
                v-model="row.finalScore"
                :min="0"
                :max="100"
                :precision="1"
                :controls="false"
                class="w-full"
                @change="handleScoreChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="总评成绩" width="140" align="center">
            <template #default="{ row }">
              <div :class="getScoreClass(row.totalScore)" class="text-xl font-bold">
                {{ formatScore(row.totalScore) }}
              </div>
            </template>
          </el-table-column>
          
          <template #empty>
            <el-empty description="请先选择课程" :image-size="160" class="py-12" />
          </template>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getCourseList } from '../api/course'
import { getStudentList } from '../api/student'
import { getScores, batchSaveScores } from '../api/score'
import { ElMessage } from 'element-plus'
import { DocumentAdd, InfoFilled } from '@element-plus/icons-vue'

const loading = ref(false)
const courseList = ref([])
const studentList = ref([])
const selectedCourseId = ref(null)
const scoreList = ref([])
const hasChanges = ref(false)

const currentCourse = computed(() => {
  return courseList.value.find(c => c.id === selectedCourseId.value)
})

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

const handleCourseChange = async () => {
  if (!selectedCourseId.value) {
    scoreList.value = []
    return
  }

  loading.value = true
  try {
    const res = await getScores({ courseId: selectedCourseId.value })
    if (res.code === 200) {
      const existingScores = res.data
      const studentMap = new Map(studentList.value.map(s => [s.id, s]))
      
      scoreList.value = studentList.value.map(student => {
        const existing = existingScores.find(s => s.studentId === student.id)
        return {
          studentId: student.id,
          studentNo: student.studentNo,
          studentName: student.name,
          dailyScore: existing ? existing.dailyScore : null,
          finalScore: existing ? existing.finalScore : null,
          totalScore: existing ? existing.totalScore : null
        }
      })
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleScoreChange = (row) => {
  const daily = row.dailyScore != null ? row.dailyScore : 0
  const final = row.finalScore != null ? row.finalScore : 0
  row.totalScore = Math.round((daily * 0.3 + final * 0.7) * 100) / 100
  hasChanges.value = true
}

const handleSave = async () => {
  const data = scoreList.value
    .filter(row => row.dailyScore != null || row.finalScore != null)
    .map(row => ({
      studentId: row.studentId,
      courseId: selectedCourseId.value,
      dailyScore: row.dailyScore,
      finalScore: row.finalScore
    }))

  if (data.length === 0) {
    ElMessage.warning({ message: '没有需要保存的成绩', plain: true })
    return
  }

  loading.value = true
  try {
    const res = await batchSaveScores(data)
    if (res.code === 200) {
      ElMessage.success({ message: '保存成功', plain: true })
      hasChanges.value = false
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
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
})
</script>

<style scoped>
.score-input {
  --el-color-primary: #3b82f6;
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
