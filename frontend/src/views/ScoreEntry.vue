<template>
  <div class="score-entry animate-fade-in-up">
    <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
      <div class="flex items-center mb-8 p-2">
        <div class="w-1.5 h-6 bg-blue-600 rounded-full mr-4 shadow-lg shadow-blue-200"></div>
        <h3 class="text-2xl font-bold text-gray-800 tracking-tight">成绩批量录入</h3>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8 p-2">
        <el-form-item label="选择课程" class="!mb-0">
          <el-select v-model="selectedCourseId" placeholder="请选择课程" class="!w-full custom-select" @change="handleCourseChange" filterable>
            <el-option v-for="c in courseList" :key="c.id" :label="`${c.courseNo} - ${c.courseName}`" :value="c.id" />
          </el-select>
        </el-form-item>
        <div class="flex items-end">
          <el-button type="primary" :icon="Refresh" @click="loadStudentScores" :disabled="!selectedCourseId" class="!rounded-xl px-6 !h-11 shadow-md shadow-blue-100">加载学生列表</el-button>
        </div>
      </div>

      <div v-if="scoreTable.length > 0" class="table-container bg-white rounded-xl overflow-hidden border border-gray-50">
        <div class="p-4 bg-blue-50/50 rounded-t-xl border-b border-blue-100">
          <div class="flex items-center text-sm text-blue-600">
            <el-icon class="mr-2"><InfoFilled /></el-icon>
            总评成绩 = 平时成绩 × 30% + 期末成绩 × 70%，修改成绩后自动计算
          </div>
        </div>
        <el-table :data="scoreTable" style="width: 100%" :header-cell-style="{ background: '#f8fafc', fontWeight: 'bold', color: '#475569', height: '64px', fontSize: '14px' }" class="custom-table">
          <el-table-column prop="studentNo" label="学号" min-width="130">
            <template #default="{ row }">
              <span class="font-mono font-medium text-gray-500">{{ row.studentNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="studentName" label="姓名" min-width="110">
            <template #default="{ row }">
              <div class="font-bold text-gray-900">{{ row.studentName }}</div>
            </template>
          </el-table-column>
          <el-table-column label="平时成绩" min-width="160">
            <template #default="{ row }">
              <el-input-number v-model="row.usualScore" :min="0" :max="100" :precision="1" :controls="false" class="!w-full custom-score-input" @change="calcTotal(row)" placeholder="0-100" />
            </template>
          </el-table-column>
          <el-table-column label="期末成绩" min-width="160">
            <template #default="{ row }">
              <el-input-number v-model="row.examScore" :min="0" :max="100" :precision="1" :controls="false" class="!w-full custom-score-input" @change="calcTotal(row)" placeholder="0-100" />
            </template>
          </el-table-column>
          <el-table-column label="总评成绩" min-width="130">
            <template #default="{ row }">
              <div class="flex items-center">
                <span class="font-bold text-lg" :class="row.totalScore >= 60 ? 'text-emerald-600' : 'text-red-500'">{{ row.totalScore }}</span>
                <el-tag v-if="row.totalScore > 0" :type="getScoreTagType(row.totalScore)" effect="light" class="ml-2 !rounded-full !text-[10px] !px-2 !border-none">{{ getScoreLevel(row.totalScore) }}</el-tag>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="mt-8 flex justify-end space-x-4 p-6 border-t border-gray-100">
          <el-button @click="scoreTable = []" class="!rounded-xl px-8 !h-12 !border-gray-200 hover:!bg-gray-50 !text-gray-500 font-bold">清空重置</el-button>
          <el-button type="primary" :loading="submitting" :icon="Check" class="!rounded-xl px-10 !h-12 !bg-blue-600 !border-blue-600 shadow-lg shadow-blue-100 font-bold transform active:scale-95 transition-all" @click="handleBatchSubmit">批量提交</el-button>
        </div>
      </div>

      <div v-else-if="selectedCourseId" class="py-16 text-center">
        <el-empty description="请点击「加载学生列表」开始录入成绩" :image-size="120" />
      </div>
      <div v-else class="py-16 text-center">
        <el-empty description="请先选择一门课程" :image-size="120" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCourseList } from '../api/course'
import { getStudentList } from '../api/student'
import { batchAddScore, getScoresByCourse } from '../api/score'
import { ElMessage } from 'element-plus'
import { Refresh, Check, InfoFilled } from '@element-plus/icons-vue'

const courseList = ref([])
const studentList = ref([])
const selectedCourseId = ref(null)
const scoreTable = ref([])
const submitting = ref(false)

const calcTotal = (row) => {
  const u = row.usualScore || 0
  const e = row.examScore || 0
  row.totalScore = Math.round((u * 0.3 + e * 0.7) * 100) / 100
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

const handleCourseChange = () => {
  scoreTable.value = []
}

const loadStudentScores = async () => {
  if (!selectedCourseId.value) return
  try {
    const [studentsRes, scoresRes] = await Promise.all([
      getStudentList(),
      getScoresByCourse(selectedCourseId.value)
    ])
    studentList.value = studentsRes.data || []
    const existingScores = scoresRes.data || []
    const scoreMap = {}
    existingScores.forEach(s => { scoreMap[s.studentId] = s })

    scoreTable.value = studentList.value.map(st => {
      const existing = scoreMap[st.id]
      const usual = existing ? existing.usualScore : 0
      const exam = existing ? existing.examScore : 0
      const total = existing ? existing.totalScore : 0
      return {
        studentId: st.id,
        studentNo: st.studentNo,
        studentName: st.name,
        usualScore: usual,
        examScore: exam,
        totalScore: total,
        existingId: existing ? existing.id : null
      }
    })
  } catch (error) {
    console.error(error)
  }
}

const handleBatchSubmit = async () => {
  if (!selectedCourseId.value) {
    ElMessage.warning('请先选择课程')
    return
  }
  const validRows = scoreTable.value.filter(r => (r.usualScore > 0 || r.examScore > 0))
  if (validRows.length === 0) {
    ElMessage.warning('请至少录入一条成绩')
    return
  }

  submitting.value = true
  try {
    const scores = validRows.map(r => ({
      studentId: r.studentId,
      usualScore: r.usualScore,
      examScore: r.examScore
    }))
    await batchAddScore({ courseId: selectedCourseId.value, scores })
    ElMessage.success({ message: `成功录入 ${validRows.length} 条成绩`, plain: true })
    loadStudentScores()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getCourseList()
    courseList.value = res.data || []
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.score-entry { --el-color-primary: #3b82f6; --el-color-success: #10b981; --el-color-danger: #ef4444; }
.shadow-premium { box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.04) !important; }
.custom-select :deep(.el-input__wrapper) { background-color: #f8fafc; border-radius: 12px; box-shadow: none !important; border: 1px solid #e2e8f0; transition: all 0.3s; }
.custom-select :deep(.el-input__wrapper.is-focus) { background-color: white; border-color: #3b82f6; box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.08) !important; }
.custom-table :deep(.el-table__row) { height: 64px; transition: background-color 0.2s; }
.custom-table :deep(.el-table__row:hover) { background-color: #f8fafc !important; }
.custom-table :deep(.el-table__cell) { border-bottom: 1px solid #f1f5f9 !important; padding: 12px 0; }
.custom-score-input :deep(.el-input__wrapper) { background-color: #f8fafc; border-radius: 10px; box-shadow: none !important; border: 1px solid #e2e8f0; transition: all 0.2s; }
.custom-score-input :deep(.el-input__wrapper.is-focus) { background-color: white; border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.08) !important; }
.animate-fade-in-up { animation: fadeInUp 0.7s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>
