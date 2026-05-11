import api from './index'

export const getCoursePage = (params) => {
  return api.get('/courses/page', { params })
}

export const getCourseList = () => {
  return api.get('/courses/list')
}

export const getCourseById = (id) => {
  return api.get(`/courses/${id}`)
}

export const addCourse = (data) => {
  return api.post('/courses', data)
}

export const updateCourse = (data) => {
  return api.put('/courses', data)
}

export const deleteCourse = (id) => {
  return api.delete(`/courses/${id}`)
}
