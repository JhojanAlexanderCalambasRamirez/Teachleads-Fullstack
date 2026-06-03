import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from 'src/boot/axios'

interface LoginResponse {
  token: string
  correo: string
  nombre: string
  rol: 'ADMIN' | 'EXTERNO'
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const correo = ref<string | null>(localStorage.getItem('correo'))
  const nombre = ref<string | null>(localStorage.getItem('nombre'))
  const rol = ref<string | null>(localStorage.getItem('rol'))

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => rol.value === 'ADMIN')
  const isExterno = computed(() => rol.value === 'EXTERNO')

  async function login(credentials: { correo: string; password: string }) {
    const { data } = await api.post<{ data: LoginResponse }>('/auth/login', credentials)
    const loginData = data.data

    token.value = loginData.token
    correo.value = loginData.correo
    nombre.value = loginData.nombre
    rol.value = loginData.rol

    localStorage.setItem('token', loginData.token)
    localStorage.setItem('correo', loginData.correo)
    localStorage.setItem('nombre', loginData.nombre)
    localStorage.setItem('rol', loginData.rol)
  }

  function logout() {
    token.value = null
    correo.value = null
    nombre.value = null
    rol.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('correo')
    localStorage.removeItem('nombre')
    localStorage.removeItem('rol')
  }

  return { token, correo, nombre, rol, isAuthenticated, isAdmin, isExterno, login, logout }
})
