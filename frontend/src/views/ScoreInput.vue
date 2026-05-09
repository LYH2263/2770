<template>
  <div class="score-input animate-fade-in-up">
    <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
      <div class="flex flex-col md:flex-row md:items-center justify-between mb-8 space-y-4 md:space-y-0 p-2">
        <div class="flex items-center">
          <div class="w-1.5 h-6 bg-orange-500 rounded-full mr-4 shadow-lg shadow-orange-200"></div>
          <h3 class="text-2xl font-bold text-gray-800 tracking-tight">成绩录入</h3>
        </div>

        <div class="flex items-center space-x-4">
          <el-select
            v-model="selectedCourseId"
            placeholder="请选择课程"
            class="!w-64"
            @change="handleCourseChange"
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
            :icon="EditPen"
            @click="handleSave"
            :disabled="!selectedCourseId || scoreList.length === 0"
            class="!rounded-xl px-6 !h-11 shadow-md shadow-blue-100"
          >
            保存成绩
          </el-button>
        </div>
      </div>

      <div v-if="selectedCourse" class="mb-6 p-5 bg-gradient-to-r from-orange-50 to-amber-50 rounded-2xl border border-orange-100">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div>
            <div class="text-sm text-gray-500 mb-1">课程名称</div>
            <div class="font-bold text-gray-800">{{ selectedCourse.courseName }}</div>
          </div>
          <div>
            <div class="text-sm text-gray-500 mb-1">课程编号</div>
            <div class="font-mono font-medium text-gray-600">{{ selectedCourse.courseNo }}</div>
          </div>
          <div>
            <div class="text-sm text-gray-500 mb-1">学分</div>
            <div class="font-bold text-gray-800">{{ selectedCourse.credit }}</div>
          </div>
          <div>
            <div class="text-sm text-gray-500 mb-1">授课教师</div>
            <div class="font-medium text-gray-700">{{ selectedCourse.teacher }}</div>
          </div>
        </div>
      </div>

      <div v-if="selectedCourse" class="table-container bg-white rounded-xl overflow-hidden border border-gray-50">
        <el-table
          v-loading="loading"
          :data="scoreList"
          style="width: 100%"
          :header-cell-style="{ background: '#f8fafc', fontWeight: 'bold', color: '#475569', height: '64px', fontSize: '14px' }"
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
          <el-table-column label="平时成绩 (40%)" width="180">
            <template #default="{ row }">
              <el-input-number
                v-model="row.regularScore"
                :min="0"
                :max="100"
                :step="1"
                :precision="1"
                @change="calculateTotal(row)"
                class="!w-full"
              />
            </template>
          </el-table-column>
          <el-table-column label="期末成绩 (60%)" width="180">
            <template #default="{ row }">
              <el-input-number
                v-model="row.finalScore"
                :min="0"
                :max="100"
                :step="1"
                :precision="1"
                @change="calculateTotal(row)"
                class="!w-full"
              />
            </template>
          </el-table-column>
          <el-table-column label="总评成绩" width="150">
            <template #default="{ row }">
              <div
                class="text-xl font-bold"
                :class="getTotalScoreClass(row.totalScore)"
              >
                {{ row.totalScore !== null && row.totalScore !== undefined ? row.totalScore.toFixed(1) : '-' }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <el-empty
        v-else
        description="请选择课程开始录入成绩"
        :image-size="180"
        class="py-16"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { getCourseList } from '../api/course'
import { getStudentList } from '../api/student'
import { getScoresByCourseId, batchSaveScores, saveScore, updateScore } from '../api/score'
import { ElMessage } from 'element-plus'
import { EditPen } from '@element-plus/icons-vue'

const loading = ref(false)
const courseList = ref([])
const studentList = ref([])
const selectedCourseId = ref(null)
const selectedCourse = ref(null)
const scoreList = ref([])

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

const handleCourseChange = async (courseId) => {
  if (!courseId) {
    selectedCourse.value = null
    scoreList.value = []
    return
  }

  selectedCourse.value = courseList.value.find(c => c.id === courseId)
  loading.value = true

  try {
    const existingScoresRes = await getScoresByCourseId(courseId)
    const existingScores = existingScoresRes.code === 200 ? existingScoresRes.data : []

    const scoresMap = new Map()
    existingScores.forEach(s => {
      scoresMap.set(s.studentId, s)
    })

    scoreList.value = studentList.value.map(student => {
      const existing = scoresMap.get(student.id)
      if (existing) {
        return {
          id: existing.id,
          studentId: student.id,
          studentNo: student.studentNo,
          studentName: student.name,
          courseId: courseId,
          regularScore: existing.regularScore,
          finalScore: existing.finalScore,
          totalScore: existing.totalScore
        }
      }
      return {
        id: null,
        studentId: student.id,
        studentNo: student.studentNo,
        studentName: student.name,
        courseId: courseId,
        regularScore: null,
        finalScore: null,
        totalScore: null
      }
    })
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const calculateTotal = (row) => {
  const regular = row.regularScore !== null && row.regularScore !== undefined ? row.regularScore : 0
  const final = row.finalScore !== null && row.finalScore !== undefined ? row.finalScore : 0
  row.totalScore = Math.round((regular * 0.4 + final * 0.6) * 10) / 10
}

const getTotalScoreClass = (score) => {
  if (score === null || score === undefined) return 'text-gray-400'
  if (score >= 90) return 'text-emerald-600'
  if (score >= 80) return 'text-blue-600'
  if (score >= 70) return 'text-cyan-600'
  if (score >= 60) return 'text-amber-600'
  return 'text-red-600'
}

const handleSave = async () => {
  const validScores = scoreList.value.filter(
    s => s.regularScore !== null && s.regularScore !== undefined &&
         s.finalScore !== null && s.finalScore !== undefined
  )

  if (validScores.length === 0) {
    ElMessage.warning('请至少录入一位学生的成绩')
    return
  }

  loading.value = true
  try {
    const res = await batchSaveScores(validScores)
    if (res.code === 200) {
      ElMessage.success({
        message: '成绩保存成功',
        plain: true
      })
      handleCourseChange(selectedCourseId.value)
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCourses()
  fetchStudents()
})
</script>

<style scoped>
.score-input {
  --el-color-primary: #f97316;
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
