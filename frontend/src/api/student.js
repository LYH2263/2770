import api from './index'

// 登录
export const login = (data) => {
  return api.post('/auth/login', data)
}

// 获取学生分页列表
export const getStudentPage = (params) => {
  return api.get('/students/page', { params })
}

// 获取学生列表
export const getStudentList = () => {
  return api.get('/students/list')
}

// 获取学生详情
export const getStudentById = (id) => {
  return api.get(`/students/${id}`)
}

// 添加学生
export const addStudent = (data) => {
  return api.post('/students', data)
}

// 更新学生
export const updateStudent = (data) => {
  return api.put('/students', data)
}

// 删除学生
export const deleteStudent = (id) => {
  return api.delete(`/students/${id}`)
}
