<template>
  <div class="login-container flex h-screen overflow-hidden font-outfit">
    <!-- 左侧视觉区 -->
    <div class="hidden lg:flex lg:w-3/5 bg-brand-dark relative items-center justify-center overflow-hidden">
      <!-- 动态背景光效 -->
      <div class="absolute top-[-10%] left-[-10%] w-80 h-80 bg-primary opacity-20 blur-3xl rounded-full"></div>
      <div class="absolute bottom-[-10%] right-[-10%] w-96 h-96 bg-purple-600 opacity-20 blur-3xl rounded-full"></div>
      
      <div class="relative z-10 text-center px-12 animate-fade-in-up">
        <h1 class="text-6xl font-bold text-white mb-6 tracking-tight">
          Student <span class="text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-purple-400">Management</span>
        </h1>
        <p class="text-xl text-gray-400 max-w-lg mx-auto leading-relaxed">
          高效、专业、现代化的学生信息管理平台。为您提供直观的数据洞察与便捷的管理体验。
        </p>
        
        <div class="mt-12 flex justify-center space-x-8">
          <div class="flex flex-col items-center">
            <div class="w-12 h-12 bg-white/10 rounded-xl flex items-center justify-center mb-2 shadow-lg">
              <el-icon class="text-blue-400 text-2xl"><User /></el-icon>
            </div>
            <span class="text-gray-500 text-sm">简单易用</span>
          </div>
          <div class="flex flex-col items-center">
            <div class="w-12 h-12 bg-white/10 rounded-xl flex items-center justify-center mb-2 shadow-lg">
              <el-icon class="text-purple-400 text-2xl"><Monitor /></el-icon>
            </div>
            <span class="text-gray-500 text-sm">实时同步</span>
          </div>
          <div class="flex flex-col items-center">
            <div class="w-12 h-12 bg-white/10 rounded-xl flex items-center justify-center mb-2 shadow-lg">
              <el-icon class="text-emerald-400 text-2xl"><Lock /></el-icon>
            </div>
            <span class="text-gray-500 text-sm">安全可靠</span>
          </div>
        </div>
      </div>
      
      <!-- 背景装饰 -->
      <div class="absolute bottom-0 left-0 w-full h-1/2 bg-gradient-to-t from-black/40 to-transparent pointer-events-none"></div>
    </div>

    <!-- 右侧登录区 -->
    <div class="w-full lg:w-2/5 flex items-center justify-center p-8 bg-white relative">
      <div class="w-full max-w-md animate-fade-in">
        <div class="text-center mb-10">
          <div class="lg:hidden mb-6 flex justify-center">
             <h1 class="text-3xl font-bold text-gray-900">学生管理系统</h1>
          </div>
          <h2 class="text-4xl font-bold text-gray-900 mb-2">欢迎回来</h2>
          <p class="text-gray-500">请输入您的账号信息以访问系统</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          size="large"
          class="space-y-4"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              :prefix-icon="User"
              class="custom-input"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              :prefix-icon="Lock"
              show-password
              class="custom-input"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          
        
          <el-button
            type="primary"
            class="w-full h-13 text-base font-bold rounded-xl shadow-xl transform active:scale-95 transition-all !bg-gradient-to-r !from-blue-600 !to-indigo-600 hover:brightness-110 !border-none"
            :loading="loading"
            @click="handleLogin"
          >
            立即登录
          </el-button>
        </el-form>

        <div class="mt-8 text-center text-gray-400 text-sm">
          &copy; 2024 Student Management System. All rights reserved.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { login } from '../api/student'
import { ElMessage } from 'element-plus'
import { User, Lock, Monitor } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        userStore.setToken(res.data.token)
        userStore.setUserInfo({
          username: res.data.username,
          nickname: res.data.nickname
        })
        ElMessage.success({
          message: '登录成功，欢迎访问系统',
          plain: true,
        })
        router.push('/')
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.custom-input :deep(.el-input__wrapper) {
  background-color: #f3f4f6;
  box-shadow: none !important;
  border: 2px solid transparent;
  border-radius: 12px;
  padding: 4px 12px;
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  background-color: white;
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1) !important;
}

.animate-fade-in {
  animation: fadeIn 0.8s ease-out;
}

.animate-fade-in-up {
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
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

.h-13 {
  height: 3.25rem;
}
</style>
