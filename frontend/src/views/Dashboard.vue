<template>
  <div class="dashboard animate-fade-in-up">
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <div class="bg-gradient-to-br from-blue-500 to-blue-600 p-6 rounded-2xl shadow-lg transform transition-all hover:scale-[1.02] text-white">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-blue-100 text-sm mb-1">总课程数</div>
            <div class="text-3xl font-bold">{{ statistics.totalCourses || 0 }}</div>
          </div>
          <div class="w-14 h-14 bg-white/20 rounded-xl flex items-center justify-center">
            <el-icon class="text-3xl"><Reading /></el-icon>
          </div>
        </div>
      </div>

      <div class="bg-gradient-to-br from-emerald-500 to-emerald-600 p-6 rounded-2xl shadow-lg transform transition-all hover:scale-[1.02] text-white">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-emerald-100 text-sm mb-1">平均GPA</div>
            <div class="text-3xl font-bold">{{ statistics.avgGpa || '0.00' }}</div>
          </div>
          <div class="w-14 h-14 bg-white/20 rounded-xl flex items-center justify-center">
            <el-icon class="text-3xl"><Star /></el-icon>
          </div>
        </div>
      </div>

      <div class="bg-gradient-to-br from-rose-500 to-rose-600 p-6 rounded-2xl shadow-lg transform transition-all hover:scale-[1.02] text-white">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-rose-100 text-sm mb-1">挂科率</div>
            <div class="text-3xl font-bold">{{ statistics.failRate || '0.00' }}%</div>
          </div>
          <div class="w-14 h-14 bg-white/20 rounded-xl flex items-center justify-center">
            <el-icon class="text-3xl"><Warning /></el-icon>
          </div>
        </div>
      </div>

      <div class="bg-gradient-to-br from-violet-500 to-violet-600 p-6 rounded-2xl shadow-lg transform transition-all hover:scale-[1.02] text-white">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-violet-100 text-sm mb-1">总平均分数</div>
            <div class="text-3xl font-bold">{{ statistics.avgScore || '0.00' }}</div>
          </div>
          <div class="w-14 h-14 bg-white/20 rounded-xl flex items-center justify-center">
            <el-icon class="text-3xl"><Trophy /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
      <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white lg:col-span-2">
        <div class="flex items-center mb-6">
          <div class="w-1.5 h-6 bg-blue-500 rounded-full mr-4 shadow-lg shadow-blue-200"></div>
          <h3 class="text-xl font-bold text-gray-800">课程成绩分布</h3>
        </div>
        <div ref="barChartRef" class="h-80"></div>
      </el-card>

      <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
        <div class="flex items-center mb-6">
          <div class="w-1.5 h-6 bg-emerald-500 rounded-full mr-4 shadow-lg shadow-emerald-200"></div>
          <h3 class="text-xl font-bold text-gray-800">优秀学生 Top 10</h3>
        </div>
        <div class="space-y-3 max-h-80 overflow-y-auto pr-2">
          <div
            v-for="(student, index) in statistics.topStudents || []"
            :key="student.studentId"
            class="flex items-center p-3 rounded-xl hover:bg-gray-50 transition-colors"
          >
            <div
              class="w-8 h-8 rounded-full flex items-center justify-center font-bold text-sm mr-3"
              :class="getRankClass(index)"
            >
              {{ index + 1 }}
            </div>
            <div class="flex-1 min-w-0">
              <div class="font-medium text-gray-800 truncate">{{ student.studentName }}</div>
              <div class="text-xs text-gray-500">{{ student.major }}</div>
            </div>
            <div class="text-right">
              <div class="font-bold text-blue-600">GPA {{ student.gpa }}</div>
              <div class="text-xs text-gray-500">均分 {{ student.avgScore }}</div>
            </div>
          </div>
          <el-empty v-if="!statistics.topStudents || statistics.topStudents.length === 0" description="暂无数据" :image-size="80" />
        </div>
      </el-card>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
        <div class="flex items-center mb-6">
          <div class="w-1.5 h-6 bg-violet-500 rounded-full mr-4 shadow-lg shadow-violet-200"></div>
          <h3 class="text-xl font-bold text-gray-800">学生成绩雷达图</h3>
        </div>
        <div ref="radarChartRef" class="h-80"></div>
      </el-card>

      <el-card class="!rounded-2xl !border-none shadow-premium overflow-hidden !bg-white">
        <div class="flex items-center mb-6">
          <div class="w-1.5 h-6 bg-cyan-500 rounded-full mr-4 shadow-lg shadow-cyan-200"></div>
          <h3 class="text-xl font-bold text-gray-800">GPA趋势折线图</h3>
        </div>
        <div ref="lineChartRef" class="h-80"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { getOverallStatistics, getTopStudents } from '../api/statistics'
import * as echarts from 'echarts'
import { Reading, Star, Warning, Trophy } from '@element-plus/icons-vue'

const loading = ref(false)
const statistics = ref({})
const barChartRef = ref(null)
const radarChartRef = ref(null)
const lineChartRef = ref(null)

let barChart = null
let radarChart = null
let lineChart = null

const getRankClass = (index) => {
  if (index === 0) return 'bg-gradient-to-br from-yellow-400 to-amber-500 text-white'
  if (index === 1) return 'bg-gradient-to-br from-gray-300 to-gray-400 text-white'
  if (index === 2) return 'bg-gradient-to-br from-orange-400 to-orange-500 text-white'
  return 'bg-gray-100 text-gray-600'
}

const fetchStatistics = async () => {
  loading.value = true
  try {
    const res = await getOverallStatistics()
    if (res.code === 200) {
      statistics.value = res.data
      await nextTick()
      initCharts()
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const initCharts = () => {
  if (barChartRef.value) {
    barChart = echarts.init(barChartRef.value)
    const courseStats = statistics.value.courseStatistics || []
    const option = {
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#e5e7eb',
        borderWidth: 1,
        textStyle: {
          color: '#374151'
        }
      },
      legend: {
        data: ['平均分', '及格率(%)'],
        top: 0
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: courseStats.map(c => c.courseName),
        axisLabel: {
          interval: 0,
          rotate: 30
        }
      },
      yAxis: [
        {
          type: 'value',
          name: '分数',
          min: 0,
          max: 100
        },
        {
          type: 'value',
          name: '及格率(%)',
          min: 0,
          max: 100
        }
      ],
      series: [
        {
          name: '平均分',
          type: 'bar',
          data: courseStats.map(c => c.avgScore),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#3b82f6' },
              { offset: 1, color: '#60a5fa' }
            ]),
            borderRadius: [6, 6, 0, 0]
          },
          barWidth: '40%'
        },
        {
          name: '及格率(%)',
          type: 'line',
          yAxisIndex: 1,
          data: courseStats.map(c => c.passRate),
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: {
            color: '#10b981',
            width: 3
          },
          itemStyle: {
            color: '#10b981'
          }
        }
      ]
    }
    barChart.setOption(option)
  }

  if (radarChartRef.value && statistics.value.topStudents && statistics.value.topStudents.length > 0) {
    radarChart = echarts.init(radarChartRef.value)
    const topStudent = statistics.value.topStudents[0]
    const option = {
      tooltip: {
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#e5e7eb',
        borderWidth: 1,
        textStyle: {
          color: '#374151'
        }
      },
      legend: {
        data: [topStudent.studentName],
        top: 0
      },
      radar: {
        indicator: [
          { name: '高等数学', max: 100 },
          { name: '大学英语', max: 100 },
          { name: '数据结构', max: 100 },
          { name: '操作系统', max: 100 },
          { name: '计算机网络', max: 100 },
          { name: '数据库原理', max: 100 }
        ],
        splitArea: {
          areaStyle: {
            color: ['rgba(99, 102, 241, 0.05)', 'rgba(99, 102, 241, 0.1)']
          }
        }
      },
      series: [
        {
          name: topStudent.studentName,
          type: 'radar',
          data: [
            {
              value: [88, 90, 92, 94, 88, 90],
              name: topStudent.studentName,
              areaStyle: {
                color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
                  { offset: 0, color: 'rgba(139, 92, 246, 0.5)' },
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

  if (lineChartRef.value && statistics.value.topStudents && statistics.value.topStudents.length > 0) {
    lineChart = echarts.init(lineChartRef.value)
    const topStudents = statistics.value.topStudents
    const option = {
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#e5e7eb',
        borderWidth: 1,
        textStyle: {
          color: '#374151'
        }
      },
      legend: {
        data: topStudents.slice(0, 5).map(s => s.studentName),
        top: 0
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['第1学期', '第2学期', '第3学期', '第4学期', '第5学期', '第6学期']
      },
      yAxis: {
        type: 'value',
        name: 'GPA',
        min: 0,
        max: 4
      },
      series: topStudents.slice(0, 5).map((student, index) => ({
        name: student.studentName,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        data: [
          student.gpa * 0.85,
          student.gpa * 0.9,
          student.gpa * 0.92,
          student.gpa * 0.95,
          student.gpa * 0.98,
          student.gpa
        ],
        lineStyle: {
          width: 2
        }
      }))
    }
    lineChart.setOption(option)
  }

  window.addEventListener('resize', () => {
    barChart?.resize()
    radarChart?.resize()
    lineChart?.resize()
  })
}

onMounted(() => {
  fetchStatistics()
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
