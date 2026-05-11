<template>
  <div class="dashboard animate-fade-in-up">
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <div class="bg-gradient-to-br from-blue-500 to-blue-600 p-6 rounded-2xl shadow-lg shadow-blue-200 transform transition-all hover:scale-[1.02]">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-blue-100 text-sm mb-2">总课程数</div>
            <div class="text-4xl font-bold text-white">{{ dashboardData.totalCourses || 0 }}</div>
          </div>
          <div class="w-14 h-14 bg-white/20 rounded-xl flex items-center justify-center">
            <el-icon class="text-3xl text-white"><Reading /></el-icon>
          </div>
        </div>
      </div>

      <div class="bg-gradient-to-br from-emerald-500 to-emerald-600 p-6 rounded-2xl shadow-lg shadow-emerald-200 transform transition-all hover:scale-[1.02]">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-emerald-100 text-sm mb-2">平均GPA</div>
            <div class="text-4xl font-bold text-white">{{ formatGpa(dashboardData.avgGpa) }}</div>
          </div>
          <div class="w-14 h-14 bg-white/20 rounded-xl flex items-center justify-center">
            <el-icon class="text-3xl text-white"><Trophy /></el-icon>
          </div>
        </div>
      </div>

      <div class="bg-gradient-to-br from-red-500 to-red-600 p-6 rounded-2xl shadow-lg shadow-red-200 transform transition-all hover:scale-[1.02]">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-red-100 text-sm mb-2">挂科率</div>
            <div class="text-4xl font-bold text-white">{{ formatPercent(dashboardData.failRate) }}%</div>
          </div>
          <div class="w-14 h-14 bg-white/20 rounded-xl flex items-center justify-center">
            <el-icon class="text-3xl text-white"><Warning /></el-icon>
          </div>
        </div>
      </div>

      <div class="bg-gradient-to-br from-purple-500 to-purple-600 p-6 rounded-2xl shadow-lg shadow-purple-200 transform transition-all hover:scale-[1.02]">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-purple-100 text-sm mb-2">优秀学生</div>
            <div class="text-4xl font-bold text-white">Top 10</div>
          </div>
          <div class="w-14 h-14 bg-white/20 rounded-xl flex items-center justify-center">
            <el-icon class="text-3xl text-white"><Star /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
      <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
        <div class="mb-4">
          <h3 class="text-lg font-bold text-gray-800">课程成绩分布</h3>
        </div>
        <div ref="barChartRef" class="h-80"></div>
      </el-card>

      <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
        <div class="mb-4">
          <h3 class="text-lg font-bold text-gray-800">GPA趋势</h3>
        </div>
        <div ref="lineChartRef" class="h-80"></div>
      </el-card>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
        <div class="mb-4">
          <h3 class="text-lg font-bold text-gray-800">学生成绩雷达图</h3>
        </div>
        <div ref="radarChartRef" class="h-80"></div>
      </el-card>

      <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white lg:col-span-2">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-bold text-gray-800">优秀学生 Top 10</h3>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="text-left text-gray-500 text-sm border-b border-gray-100">
                <th class="pb-3 font-medium">排名</th>
                <th class="pb-3 font-medium">学号</th>
                <th class="pb-3 font-medium">姓名</th>
                <th class="pb-3 font-medium">专业</th>
                <th class="pb-3 font-medium">GPA</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="student in dashboardData.topStudents"
                :key="student.studentId"
                class="border-b border-gray-50 hover:bg-gray-50 transition-colors"
              >
                <td class="py-4">
                  <div
                    class="w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold"
                    :class="getRankClass(student.rank)"
                  >
                    {{ student.rank }}
                  </div>
                </td>
                <td class="py-4 font-mono text-gray-500">{{ student.studentNo }}</td>
                <td class="py-4 font-bold text-gray-800">{{ student.studentName }}</td>
                <td class="py-4 text-gray-600">{{ student.major }}</td>
                <td class="py-4">
                  <span class="text-lg font-bold text-emerald-600">{{ formatGpa(student.gpa) }}</span>
                </td>
              </tr>
              <tr v-if="!dashboardData.topStudents || dashboardData.topStudents.length === 0">
                <td colspan="5" class="py-12 text-center text-gray-400">暂无数据</td>
              </tr>
            </tbody>
          </table>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getDashboardStatistics, getCourseStatistics, getScores, getMajorRank } from '../api/score'
import { getCourseList } from '../api/course'
import * as echarts from 'echarts'
import { Reading, Trophy, Warning, Star } from '@element-plus/icons-vue'

const dashboardData = ref({})
const courseList = ref([])
const barChartRef = ref(null)
const lineChartRef = ref(null)
const radarChartRef = ref(null)
let barChart = null
let lineChart = null
let radarChart = null

const fetchDashboardData = async () => {
  try {
    const res = await getDashboardStatistics()
    if (res.code === 200) {
      dashboardData.value = res.data
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchCourseList = async () => {
  try {
    const res = await getCourseList()
    if (res.code === 200) {
      courseList.value = res.data
    }
  } catch (error) {
    console.error(error)
  }
}

const initBarChart = async () => {
  if (!barChartRef.value) return

  const courseNames = []
  const avgScores = []
  const passRates = []

  for (const course of courseList.value.slice(0, 6)) {
    try {
      const res = await getCourseStatistics(course.id)
      if (res.code === 200 && res.data.studentCount > 0) {
        courseNames.push(course.courseName)
        avgScores.push(res.data.avgScore.toFixed(1))
        passRates.push(res.data.passRate.toFixed(1))
      }
    } catch (e) {
      console.error(e)
    }
  }

  barChart = echarts.init(barChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['平均分', '及格率'],
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: courseNames,
      axisLabel: {
        rotate: 30,
        fontSize: 12
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '分数',
        min: 0,
        max: 100,
        position: 'left'
      },
      {
        type: 'value',
        name: '百分比',
        min: 0,
        max: 100,
        position: 'right',
        axisLabel: {
          formatter: '{value}%'
        }
      }
    ],
    series: [
      {
        name: '平均分',
        type: 'bar',
        data: avgScores,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ]),
          borderRadius: [8, 8, 0, 0]
        },
        barWidth: '40%'
      },
      {
        name: '及格率',
        type: 'line',
        yAxisIndex: 1,
        data: passRates,
        smooth: true,
        itemStyle: {
          color: '#f093fb'
        },
        lineStyle: {
          width: 3
        }
      }
    ]
  }
  barChart.setOption(option)
}

const initLineChart = async () => {
  if (!lineChartRef.value) return

  try {
    const res = await getMajorRank()
    if (res.code === 200) {
      const majors = res.data.map(item => item.major)
      const avgGpas = res.data.map(item => item.avgGpa.toFixed(2))

      lineChart = echarts.init(lineChartRef.value)
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: majors,
          axisLabel: {
            rotate: 20,
            fontSize: 11
          }
        },
        yAxis: {
          type: 'value',
          name: 'GPA',
          min: 0,
          max: 4
        },
        series: [
          {
            name: '平均GPA',
            type: 'line',
            data: avgGpas,
            smooth: true,
            itemStyle: {
              color: '#10b981'
            },
            lineStyle: {
              width: 4,
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#10b981' },
                { offset: 1, color: '#34d399' }
              ])
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(16, 185, 129, 0.3)' },
                { offset: 1, color: 'rgba(16, 185, 129, 0.05)' }
              ])
            },
            symbol: 'circle',
            symbolSize: 10
          }
        ]
      }
      lineChart.setOption(option)
    }
  } catch (error) {
    console.error(error)
  }
}

const initRadarChart = async () => {
  if (!radarChartRef.value) return

  const topStudent = dashboardData.value.topStudents?.[0]
  if (!topStudent) return

  try {
    const res = await getScores({ studentId: topStudent.studentId })
    if (res.code === 200 && res.data.length > 0) {
      const indicators = res.data.map(item => ({ name: item.courseName, max: 100 }))
      const scores = res.data.map(item => item.totalScore)

      radarChart = echarts.init(radarChartRef.value)
      const option = {
        tooltip: {},
        radar: {
          indicator: indicators,
          shape: 'polygon',
          splitNumber: 5,
          axisName: {
            fontSize: 11
          },
          splitArea: {
            areaStyle: {
              color: ['rgba(139, 92, 246, 0.05)', 'rgba(139, 92, 246, 0.1)']
            }
          }
        },
        series: [
          {
            name: topStudent.studentName,
            type: 'radar',
            data: [
              {
                value: scores,
                name: topStudent.studentName,
                areaStyle: {
                  color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
                    { offset: 0, color: 'rgba(139, 92, 246, 0.6)' },
                    { offset: 1, color: 'rgba(139, 92, 246, 0.1)' }
                  ])
                },
                lineStyle: {
                  color: '#8b5cf6',
                  width: 2
                },
                itemStyle: {
                  color: '#8b5cf6'
                }
              }
            ]
          }
        ]
      }
      radarChart.setOption(option)
    }
  } catch (error) {
    console.error(error)
  }
}

const handleResize = () => {
  barChart?.resize()
  lineChart?.resize()
  radarChart?.resize()
}

const formatGpa = (gpa) => {
  return gpa != null ? gpa.toFixed(2) : '0.00'
}

const formatPercent = (percent) => {
  return percent != null ? percent.toFixed(1) : '0.0'
}

const getRankClass = (rank) => {
  if (rank === 1) return 'bg-yellow-100 text-yellow-700'
  if (rank === 2) return 'bg-gray-100 text-gray-700'
  if (rank === 3) return 'bg-orange-100 text-orange-700'
  return 'bg-gray-50 text-gray-600'
}

onMounted(async () => {
  await fetchDashboardData()
  await fetchCourseList()
  await initBarChart()
  await initLineChart()
  await initRadarChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  barChart?.dispose()
  lineChart?.dispose()
  radarChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  --el-color-primary: #3b82f6;
  --el-color-success: #10b981;
  --el-color-danger: #ef4444;
}

.shadow-premium {
  box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.04) !important;
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
