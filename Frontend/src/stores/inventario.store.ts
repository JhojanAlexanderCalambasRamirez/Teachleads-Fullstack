import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from 'src/boot/axios'

export interface InventarioItem {
  id: number
  empresaNit: string
  empresaNombre: string
  productoCodigo: string
  productoNombre: string
  productoCaracteristicas: string
  cantidad: number
}

export const useInventarioStore = defineStore('inventario', () => {
  const items = ref<InventarioItem[]>([])
  const loading = ref(false)

  async function fetchAll(empresaNit?: string) {
    loading.value = true
    try {
      const params = empresaNit ? { empresaNit } : {}
      const { data } = await api.get<{ data: InventarioItem[] }>('/inventario', { params })
      items.value = data.data
    } finally {
      loading.value = false
    }
  }

  async function agregar(payload: { empresaNit: string; productoCodigo: string; cantidad: number }) {
    const { data } = await api.post<{ data: InventarioItem }>('/inventario', payload)
    const idx = items.value.findIndex(
      (i) => i.empresaNit === payload.empresaNit && i.productoCodigo === payload.productoCodigo
    )
    if (idx !== -1) {
      items.value[idx] = data.data
    } else {
      items.value.push(data.data)
    }
    return data.data
  }

  async function eliminar(id: number) {
    await api.delete(`/inventario/${id}`)
    items.value = items.value.filter((i) => i.id !== id)
  }

  async function descargarPdf(ids?: number[], empresaNit?: string) {
    const params: Record<string, unknown> = {}
    if (ids && ids.length > 0) {
      params.ids = ids
    } else if (empresaNit) {
      params.empresaNit = empresaNit
    }

    const response = await api.get('/inventario/pdf', {
      params,
      responseType: 'blob'
    })

    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'inventario.pdf')
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  }

  async function enviarPdf(destinatario: string, ids?: number[], empresaNit?: string) {
    const body: Record<string, unknown> = { destinatario }
    if (ids && ids.length > 0) {
      body.ids = ids
    } else if (empresaNit) {
      body.empresaNit = empresaNit
    }
    await api.post('/inventario/enviar-pdf', body)
  }

  return { items, loading, fetchAll, agregar, eliminar, descargarPdf, enviarPdf }
})
