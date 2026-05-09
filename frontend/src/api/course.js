import api from './index'

export function getCoursePage(params) {
  return api.get('/courses/page', { params })
}

export function getCourseList() {
  return api.get('/courses/list')
}

export function getCourseById(id) {
  return api.get(`/courses/${id}`)
}

export function saveCourse(data) {
  return api.post('/courses', data)
}

export function updateCourse(data) {
  return api.put('/courses', data)
}

export function deleteCourse(id) {
  return api.delete(`/courses/${id}`)
}
