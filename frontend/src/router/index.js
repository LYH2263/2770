import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/store'
import Login from '@/views/Login.vue'
import Layout from '@/views/Layout.vue'
import StudentList from '@/views/StudentList.vue'
import CourseList from '@/views/CourseList.vue'
import ScoreInput from '@/views/ScoreInput.vue'
import ScoreQuery from '@/views/ScoreQuery.vue'
import Dashboard from '@/views/Dashboard.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: Dashboard
      },
      {
        path: '/students',
        name: 'StudentList',
        component: StudentList
      },
      {
        path: '/courses',
        name: 'CourseList',
        component: CourseList
      },
      {
        path: '/score-input',
        name: 'ScoreInput',
        component: ScoreInput
      },
      {
        path: '/score-query',
        name: 'ScoreQuery',
        component: ScoreQuery
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
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
