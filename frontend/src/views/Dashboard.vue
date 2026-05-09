<template>
  <div class="dashboard animate-fade-in-up">
    <div v-loading="loading" class="space-y-8">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="bg-gradient-to-br from-blue-500 to-blue-600 p-6 rounded-2xl shadow-lg shadow-blue-200/50 transform transition-all hover:scale-[1.02] hover:shadow-xl">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-sm text-blue-100 mb-2">总课程数</div>
              <div class="text-4xl font-bold text-white">{{ dashboard.totalCourses || 0 }}</div>
            </div>
            <div class="w-14 h-14 bg-white/20 rounded-2xl flex items-center justify-center">
              <el-icon class="text-white text-3xl"><Reading /></el-icon>
            </div>
          </div>
        </div>
        <div class="bg-gradient-to-br from-emerald-500 to-emerald-600 p-6 rounded-2xl shadow-lg shadow-emerald-200/50 transform transition-all hover:scale-[1.02] hover:shadow-xl">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-sm text-emerald-100 mb-2">平均GPA</div>
              <div class="text-4xl font-bold text-white">{{ dashboard.averageGpa || 0 }}</div>
            </div>
            <div class="w-14 h-14 bg-white/20 rounded-2xl flex items-center justify-center">
              <el-icon class="text-white text-3xl"><TrendCharts /></el-icon>
            </div>
          </div>
        </div>
        <div class="bg-gradient-to-br from-red-400 to-red-500 p-6 rounded-2xl shadow-lg shadow-red-200/50 transform transition-all hover:scale-[1.02] hover:shadow-xl">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-sm text-red-100 mb-2">挂科率</div>
              <div class="text-4xl font-bold text-white">{{ dashboard.failRate || 0 }}%</div>
            </div>
            <div class="w-14 h-14 bg-white/20 rounded-2xl flex items-center justify-center">
              <el-icon class="text-white text-3xl"><WarnTriangleFilled /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <el-card class="!rounded-2xl !border-none shadow-premium">
          <template #header>
            <div class="flex items-center">
              <div class="w-1.5 h-6 bg-blue-600 rounded-full mr-3 shadow-lg shadow-blue-200"></div>
              <span class="text-lg font-bold text-gray-800">课程成绩柱状图</span>
            </div>
          </template>
          <div ref="barChartRef" style="height: 360px;"></div>
        </el-card>

        <el-card class="!rounded-2xl !border-none shadow-premium">
          <template #header>
            <div class="flex items-center">
              <div class="w-1.5 h-6 bg-purple-600 rounded-full mr-3 shadow-lg shadow-purple-200"></div>
              <span class="text-lg font-bold text-gray-800">专业GPA排名</span>
            </div>
          </template>
          <div ref="radarChartRef" style="height: 360px;"></div>
        </el-card>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <el-card class="!rounded-2xl !border-none shadow-premium lg:col-span-1">
          <template #header>
            <div class="flex items-center">
              <div class="w-1.5 h-6 bg-amber-500 rounded-full mr-3 shadow-lg shadow-amber-200"></div>
              <span class="text-lg font-bold text-gray-800">优秀学生 Top10</span>
            </div>
          </template>
          <div class="space-y-3">
            <div v-for="(student, index) in (dashboard.topStudents || [])" :key="student.studentId" class="flex items-center p-3 rounded-xl transition-all hover:bg-gray-50">
              <div class="w-9 h-9 rounded-full flex items-center justify-center mr-3 text-sm font-bold" :class="index < 3 ? 'bg-gradient-to-tr from-amber-400 to-orange-500 text-white shadow-md' : 'bg-gray-100 text-gray-500'">
                {{ index + 1 }}
              </div>
              <div class="flex-1 min-w-0">
                <div class="font-bold text-gray-800 truncate">{{ student.studentName }}</div>
                <div class="text-xs text-gray-400 truncate">{{ student.major }}</div>
              </div>
              <div class="text-lg font-bold" :class="student.gpa >= 3.5 ? 'text-emerald-600' : student.gpa >= 3.0 ? 'text-blue-600' : 'text-gray-600'">{{ student.gpa }}</div>
            </div>
            <el-empty v-if="!dashboard.topStudents || dashboard.topStudents.length === 0" description="暂无数据" :image-size="80" />
          </div>
        </el-card>

        <el-card class="!rounded-2xl !border-none shadow-premium lg:col-span-2">
          <template #header>
            <div class="flex items-center">
              <div class="w-1.5 h-6 bg-emerald-500 rounded-full mr-3 shadow-lg shadow-emerald-200"></div>
              <span class="text-lg font-bold text-gray-800">各专业平均分对比</span>
            </div>
          </template>
          <div ref="lineChartRef" style="height: 380px;"></div>
        </el-card>
      </div>

      <el-card class="!rounded-2xl !border-none shadow-premium">
        <template #header>
          <div class="flex items-center">
            <div class="w-1.5 h-6 bg-indigo-600 rounded-full mr-3 shadow-lg shadow-indigo-200"></div>
            <span class="text-lg font-bold text-gray-800">课程成绩分布详情</span>
          </div>
        </template>
        <div class="table-container bg-white rounded-xl overflow-hidden border border-gray-50">
          <el-table :data="dashboard.courseStatistics || []" style="width: 100%" :header-cell-style="{ background: '#f8fafc', fontWeight: 'bold', color: '#475569', height: '56px', fontSize: '13px' }" class="custom-table">
            <el-table-column prop="courseName" label="课程名称" min-width="140" />
            <el-table-column prop="studentCount" label="选课人数" width="100" />
            <el-table-column prop="averageScore" label="平均分" width="100">
              <template #default="{ row }">
                <span class="font-bold" :class="row.averageScore >= 80 ? 'text-emerald-600' : row.averageScore >= 60 ? 'text-blue-600' : 'text-red-500'">{{ row.averageScore }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="passRate" label="及格率" width="100">
              <template #default="{ row }">
                <span :class="row.passRate >= 90 ? 'text-emerald-600' : row.passRate >= 70 ? 'text-blue-600' : 'text-red-500'">{{ row.passRate }}%</span>
              </template>
            </el-table-column>
            <el-table-column prop="maxScore" label="最高分" width="90" />
            <el-table-column prop="minScore" label="最低分" width="90" />
            <el-table-column label="成绩分布" min-width="280">
              <template #default="{ row }">
                <div class="flex items-center space-x-1 text-xs">
                  <el-tag effect="light" class="!rounded-full !text-[10px] !px-2 !border-none bg-emerald-50 text-emerald-600">优{{ row.excellentCount || 0 }}</el-tag>
                  <el-tag effect="light" class="!rounded-full !text-[10px] !px-2 !border-none bg-blue-50 text-blue-600">良{{ row.goodCount || 0 }}</el-tag>
                  <el-tag effect="light" class="!rounded-full !text-[10px] !px-2 !border-none bg-amber-50 text-amber-600">中{{ row.mediumCount || 0 }}</el-tag>
                  <el-tag effect="light" class="!rounded-full !text-[10px] !px-2 !border-none bg-gray-50 text-gray-600">及{{ row.passCount || 0 }}</el-tag>
                  <el-tag effect="light" class="!rounded-full !text-[10px] !px-2 !border-none bg-red-50 text-red-500">挂{{ row.failCount || 0 }}</el-tag>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { getDashboard } from '../api/score'
import { Reading, TrendCharts, WarnTriangleFilled } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const loading = ref(false)
const dashboard = ref({})
const barChartRef = ref(null)
const radarChartRef = ref(null)
const lineChartRef = ref(null)
let barChart = null
let radarChart = null
let lineChart = null

const initCharts = () => {
  if (barChartRef.value) {
    barChart = echarts.init(barChartRef.value)
  }
  if (radarChartRef.value) {
    radarChart = echarts.init(radarChartRef.value)
  }
  if (lineChartRef.value) {
    lineChart = echarts.init(lineChartRef.value)
  }
}

const updateCharts = () => {
  const stats = dashboard.value.courseStatistics || []
  const majorRanks = dashboard.value.majorRanks || []

  if (barChart) {
    barChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      legend: { data: ['平均分', '及格率'], top: 10 },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: stats.map(s => s.courseName), axisLabel: { rotate: 20, fontSize: 11 } },
      yAxis: [
        { type: 'value', name: '分数', min: 0, max: 100 },
        { type: 'value', name: '及格率%', min: 0, max: 100 }
      ],
      series: [
        { name: '平均分', type: 'bar', data: stats.map(s => s.averageScore), itemStyle: { borderRadius: [6, 6, 0, 0], color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#3b82f6' }, { offset: 1, color: '#93c5fd' }]) }, barWidth: 24 },
        { name: '及格率', type: 'line', yAxisIndex: 1, data: stats.map(s => s.passRate), smooth: true, lineStyle: { width: 3, color: '#10b981' }, itemStyle: { color: '#10b981' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(16,185,129,0.25)' }, { offset: 1, color: 'rgba(16,185,129,0.02)' }]) } }
      ]
    })
  }

  if (radarChart) {
    const topMajors = majorRanks.slice(0, 6)
    const indicators = topMajors.map(m => ({ name: m.major, max: 4.0 }))
    radarChart.setOption({
      tooltip: {},
      radar: {
        indicator: indicators,
        shape: 'circle',
        splitNumber: 4,
        axisName: { color: '#475569', fontSize: 11 },
        splitArea: { areaStyle: { color: ['rgba(59,130,246,0.02)', 'rgba(59,130,246,0.04)', 'rgba(59,130,246,0.06)', 'rgba(59,130,246,0.08)'] } }
      },
      series: [{
        type: 'radar',
        data: [{
          value: topMajors.map(m => m.averageGpa),
          name: '平均GPA',
          areaStyle: { color: 'rgba(139,92,246,0.15)' },
          lineStyle: { color: '#8b5cf6', width: 2 },
          itemStyle: { color: '#8b5cf6' }
        }]
      }]
    })
  }

  if (lineChart) {
    lineChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['平均分', '平均GPA(×25)'], top: 10 },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: majorRanks.map(m => m.major), axisLabel: { rotate: 25, fontSize: 10 } },
      yAxis: [
        { type: 'value', name: '平均分', min: 0, max: 100 },
        { type: 'value', name: 'GPA(×25)', min: 0, max: 100 }
      ],
      series: [
        { name: '平均分', type: 'bar', data: majorRanks.map(m => m.averageScore), itemStyle: { borderRadius: [6, 6, 0, 0], color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#10b981' }, { offset: 1, color: '#6ee7b7' }]) }, barWidth: 28 },
        { name: '平均GPA(×25)', type: 'line', yAxisIndex: 1, data: majorRanks.map(m => (m.averageGpa * 25).toFixed(1)), smooth: true, lineStyle: { width: 3, color: '#f59e0b' }, itemStyle: { color: '#f59e0b' }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(245,158,11,0.2)' }, { offset: 1, color: 'rgba(245,158,11,0.02)' }]) } }
      ]
    })
  }
}

const fetchDashboard = async () => {
  loading.value = true
  try {
    const res = await getDashboard()
    if (res.code === 200) {
      dashboard.value = res.data
      await nextTick()
      initCharts()
      updateCharts()
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleResize = () => {
  barChart && barChart.resize()
  radarChart && radarChart.resize()
  lineChart && lineChart.resize()
}

onMounted(() => {
  fetchDashboard()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  barChart && barChart.dispose()
  radarChart && radarChart.dispose()
  lineChart && lineChart.dispose()
})
</script>

<style scoped>
.dashboard { --el-color-primary: #3b82f6; --el-color-success: #10b981; --el-color-danger: #ef4444; }
.shadow-premium { box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.04) !important; }
.custom-table :deep(.el-table__row) { height: 56px; transition: background-color 0.2s; }
.custom-table :deep(.el-table__row:hover) { background-color: #f8fafc !important; }
.custom-table :deep(.el-table__cell) { border-bottom: 1px solid #f1f5f9 !important; padding: 10px 0; }
.animate-fade-in-up { animation: fadeInUp 0.7s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
</style>
