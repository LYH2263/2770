import api from './index'

export function getStudentTranscript(studentId) {
  return api.get(`/statistics/transcript/${studentId}`)
}

export function getCourseStatistics() {
  return api.get('/statistics/courses')
}

export function getTopStudents() {
  return api.get('/statistics/top-students')
}

export function getMajorRanking() {
  return api.get('/statistics/major-ranking')
}

export function getOverallStatistics() {
  return api.get('/statistics/overview')
}
