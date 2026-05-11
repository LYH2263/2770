import api from './index'

export const getScores = (params) => {
  return api.get('/scores', { params })
}

export const addScore = (data) => {
  return api.post('/scores', data)
}

export const batchSaveScores = (data) => {
  return api.post('/scores/batch', data)
}

export const deleteScore = (id) => {
  return api.delete(`/scores/${id}`)
}

export const getTranscript = (studentId) => {
  return api.get(`/scores/transcript/${studentId}`)
}

export const getCourseStatistics = (courseId) => {
  return api.get(`/scores/statistics/course/${courseId}`)
}

export const getMajorRank = () => {
  return api.get('/scores/statistics/major-rank')
}

export const getDashboardStatistics = () => {
  return api.get('/scores/statistics/dashboard')
}
