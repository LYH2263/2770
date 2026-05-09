<template>
  <el-container class="h-screen font-outfit bg-[#f8fafc]">
    <!-- 侧边栏 -->
    <el-aside
      :width="isCollapse ? '80px' : '260px'"
      class="sidebar-container transition-all duration-300 shadow-premium z-20 overflow-hidden"
    >
      <div class="flex flex-col h-full bg-[#1e293b]">
        <!-- Logo 区域 -->
        <div class="h-20 flex items-center justify-center border-b border-white/5 px-6">
          <div class="w-10 h-10 bg-primary rounded-xl flex items-center justify-center mr-3 shadow-lg shadow-primary/30">
            <el-icon class="text-white text-xl"><Monitor /></el-icon>
          </div>
          <span v-show="!isCollapse" class="text-lg font-bold text-white tracking-wide animate-fade-in">学生管理系统</span>
        </div>

        <!-- 菜单 -->
        <el-scrollbar class="flex-1 px-3 py-4">
          <el-menu
            :default-active="activePath"
            router
            background-color="transparent"
            text-color="#94a3b8"
            active-text-color="#ffffff"
            :collapse="isCollapse"
            class="border-none"
          >
            <el-menu-item index="/students" class="rounded-xl mb-1 hover:!bg-white/5 group">
              <el-icon class="group-hover:text-primary transition-colors"><User /></el-icon>
              <template #title>
                <span class="font-medium">学生管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/courses" class="rounded-xl mb-1 hover:!bg-white/5 group">
              <el-icon class="group-hover:text-primary transition-colors"><Reading /></el-icon>
              <template #title>
                <span class="font-medium">课程管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/score-entry" class="rounded-xl mb-1 hover:!bg-white/5 group">
              <el-icon class="group-hover:text-primary transition-colors"><EditPen /></el-icon>
              <template #title>
                <span class="font-medium">成绩录入</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/score-query" class="rounded-xl mb-1 hover:!bg-white/5 group">
              <el-icon class="group-hover:text-primary transition-colors"><Search /></el-icon>
              <template #title>
                <span class="font-medium">成绩查询</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/dashboard" class="rounded-xl mb-1 hover:!bg-white/5 group">
              <el-icon class="group-hover:text-primary transition-colors"><DataAnalysis /></el-icon>
              <template #title>
                <span class="font-medium">数据看板</span>
              </template>
            </el-menu-item>
          </el-menu>
        </el-scrollbar>
        
        <!-- 侧边栏页脚 -->
        <div class="p-4 border-t border-white/5">
           <div class="flex items-center justify-center text-xs text-gray-500 italic" v-show="!isCollapse">
             v1.0.0 Pro Edition
           </div>
        </div>
      </div>
    </el-aside>

    <el-container class="relative overflow-hidden">
      <!-- 顶部 Header -->
      <el-header class="bg-white/80 backdrop-blur-md border-b border-gray-100 flex items-center justify-between px-8 h-20 shadow-sm z-10">
        <div class="flex items-center">
          <el-button
            link
            @click="isCollapse = !isCollapse"
            class="hover:bg-gray-100 p-2 rounded-lg transition-colors"
          >
            <el-icon class="text-xl text-gray-600"><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </el-button>
          
          <el-breadcrumb separator="/" class="ml-6 hidden md:block">
            <el-breadcrumb-item>管理系统</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="flex items-center space-x-6">
          <el-dropdown trigger="click">
            <div class="flex items-center cursor-pointer group">
              <div class="w-10 h-10 rounded-full bg-gradient-to-tr from-blue-100 to-indigo-100 flex items-center justify-center mr-3 group-hover:shadow-md transition-all">
                <el-icon class="text-primary"><UserFilled /></el-icon>
              </div>
              <div class="hidden sm:block">
                <div class="text-sm font-bold text-gray-800">{{ userStore.nickname || userStore.username }}</div>
                <div class="text-xs text-gray-400">系统权限: 管理员</div>
              </div>
              <el-icon class="ml-2 text-gray-400 group-hover:text-primary"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="w-48 py-2">
                <el-dropdown-item divided @click="handleLogout" class="text-danger py-2">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="bg-[#f8fafc] p-0 overflow-x-hidden relative">
        <div class="p-8 max-w-[1600px] mx-auto min-h-full">
           <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../store'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  User,
  Monitor,
  Fold,
  Expand,
  ArrowDown,
  UserFilled,
  SwitchButton,
  Setting,
  Reading,
  EditPen,
  Search,
  DataAnalysis
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isCollapse = ref(false)

const activePath = computed(() => route.path)
const currentRouteName = computed(() => {
  const map = {
    '/students': '学生管理',
    '/courses': '课程管理',
    '/score-entry': '成绩录入',
    '/score-query': '成绩查询',
    '/dashboard': '数据看板'
  }
  return map[route.path] || '首页'
})

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '退出提示', {
    confirmButtonText: '确定退出',
    cancelButtonText: '取消',
    type: 'warning',
    roundButton: true,
    confirmButtonClass: '!bg-danger !border-danger'
  }).then(() => {
    userStore.logout()
    ElMessage.success({
      message: '已成功退出系统',
      plain: true
    })
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.sidebar-container :deep(.el-menu-item.is-active) {
  background: white !important;
  color: #1e293b !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.sidebar-container :deep(.el-menu-item:hover:not(.is-active)) {
  color: white !important;
}

/* 动效 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}

.animate-fade-in {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
