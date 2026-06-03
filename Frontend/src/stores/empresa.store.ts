import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from 'src/boot/axios'

export interface Empresa {
  nit: string
  nombre: string
  direccion: string
  telefono: string
}

export const useEmpresaStore = defineStore('empresa', () => {
  const empresas = ref<Empresa[]>([])
  const loading = ref(false)

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await api.get<{ data: Empresa[] }>('/empresas')
      empresas.value = data.data
    } finally {
      loading.value = false
    }
  }

  async function crear(empresa: Empresa) {
    const { data } = await api.post<{ data: Empresa }>('/empresas', empresa)
    empresas.value.push(data.data)
    return data.data
  }

  async function actualizar(nit: string, empresa: Omit<Empresa, 'nit'>) {
    const { data } = await api.put<{ data: Empresa }>(`/empresas/${nit}`, { ...empresa, nit })
    const idx = empresas.value.findIndex((e) => e.nit === nit)
    if (idx !== -1) empresas.value[idx] = data.data
    return data.data
  }

  async function eliminar(nit: string) {
    await api.delete(`/empresas/${nit}`)
    empresas.value = empresas.value.filter((e) => e.nit !== nit)
  }

  return { empresas, loading, fetchAll, crear, actualizar, eliminar }
})
