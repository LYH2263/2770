import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import WindiCSS from 'vite-plugin-windicss'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue(),
    WindiCSS()
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3270,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://backend:8270',
        changeOrigin: true
      }
    }
  }
})
