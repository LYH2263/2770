import api from './index'

export function getScorePage(params) {
  return api.get('/scores/page', { params })
}

export function getScoresByStudentId(studentId) {
  return api.get(`/scores/student/${studentId}`)
}

export function getScoresByCourseId(courseId) {
  return api.get(`/scores/course/${courseId}`)
}

export function getScoreById(id) {
  return api.get(`/scores/${id}`)
}

export function saveScore(data) {
  return api.post('/scores', data)
}

export function updateScore(data) {
  return api.put('/scores', data)
}

export function batchSaveScores(data) {
  return api.post('/scores/batch', data)
}

export function deleteScore(id) {
  return api.delete(`/scores/${id}`)
}
