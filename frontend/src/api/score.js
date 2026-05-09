import api from './index'

export const getScorePage = (params) => {
  return api.get('/scores/page', { params })
}

export const getScoresByStudent = (studentId) => {
  return api.get(`/scores/student/${studentId}`)
}

export const getScoresByCourse = (courseId) => {
  return api.get(`/scores/course/${courseId}`)
}

export const addScore = (data) => {
  return api.post('/scores', data)
}

export const updateScore = (data) => {
  return api.put('/scores', data)
}

export const batchAddScore = (data) => {
  return api.post('/scores/batch', data)
}

export const deleteScore = (id) => {
  return api.delete(`/scores/${id}`)
}

export const getStudentTranscript = (studentId) => {
  return api.get(`/statistics/transcript/${studentId}`)
}

export const getCourseStatistics = () => {
  return api.get('/statistics/course')
}

export const getCourseStatisticsById = (courseId) => {
  return api.get(`/statistics/course/${courseId}`)
}

export const getMajorRank = () => {
  return api.get('/statistics/major-rank')
}

export const getTopStudents = (limit = 10) => {
  return api.get('/statistics/top-students', { params: { limit } })
}

export const getDashboard = () => {
  return api.get('/statistics/dashboard')
}
