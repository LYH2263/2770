import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store'
import Login from '@/views/Login.vue'
import Layout from '@/views/Layout.vue'
import StudentList from '@/views/StudentList.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    component: Layout,
    redirect: '/students',
    children: [
      {
        path: '/students',
        name: 'StudentList',
        component: StudentList
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.path !== '/login' && !userStore.token) {
    next('/login')
  } else if (to.path === '/login' && userStore.token) {
    next('/')
  } else {
    next()
  }
})

export default router
