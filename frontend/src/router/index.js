import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/store'
import Login from '@/views/Login.vue'
import Layout from '@/views/Layout.vue'
import StudentList from '@/views/StudentList.vue'
import CourseList from '@/views/CourseList.vue'
import ScoreEntry from '@/views/ScoreEntry.vue'
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
    redirect: '/students',
    children: [
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
        path: '/score-entry',
        name: 'ScoreEntry',
        component: ScoreEntry
      },
      {
        path: '/score-query',
        name: 'ScoreQuery',
        component: ScoreQuery
      },
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: Dashboard
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

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
