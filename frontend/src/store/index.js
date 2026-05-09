import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUserInfo(info) {
    username.value = info.username
    nickname.value = info.nickname
    localStorage.setItem('username', info.username)
    localStorage.setItem('nickname', info.nickname)
  }

  function clearUserInfo() {
    token.value = ''
    username.value = ''
    nickname.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('nickname')
  }

  return {
    token,
    username,
    nickname,
    setToken,
    setUserInfo,
    clearUserInfo,
    logout: clearUserInfo
  }
})
