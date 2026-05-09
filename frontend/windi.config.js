import { defineConfig } from 'windicss/helpers'

export default defineConfig({
  darkMode: false,
  theme: {
    extend: {
      colors: {
        primary: {
          light: '#6db9ff',
          DEFAULT: '#3b82f6',
          dark: '#1d4ed8',
        },
        success: '#10b981',
        warning: '#f59e0b',
        danger: '#ef4444',
        info: '#6b7280',
        brand: {
          gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          dark: '#1e2023',
        }
      },
      fontFamily: {
        outfit: ['Outfit', 'sans-serif'],
      },
      boxShadow: {
        'premium': '0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1)',
        'glass': '0 8px 32px 0 rgba(31, 38, 135, 0.07)',
      },
      backgroundImage: {
        'auth-pattern': "url('https://images.unsplash.com/photo-1517694712202-14dd9538aa97?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80')",
      }
    }
  }
})
