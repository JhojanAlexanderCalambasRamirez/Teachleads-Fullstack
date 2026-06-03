import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from 'src/boot/axios'

export interface ProductoPrecio {
  id?: number
  moneda: string
  precio: number
}

export interface Categoria {
  id: number
  nombre: string
}

export interface Producto {
  codigo: string
  nombre: string
  caracteristicas: string
  empresaNit: string
  empresaNombre?: string
  precios: ProductoPrecio[]
  categorias: Categoria[]
}

export interface ProductoRequest {
  codigo: string
  nombre: string
  caracteristicas: string
  empresaNit: string
  precios: ProductoPrecio[]
  categoriaIds: number[]
}

export const useProductoStore = defineStore('producto', () => {
  const productos = ref<Producto[]>([])
  const categorias = ref<Categoria[]>([])
  const loading = ref(false)

  async function fetchAll(empresaNit?: string) {
    loading.value = true
    try {
      const params = empresaNit ? { empresaNit } : {}
      const { data } = await api.get<{ data: Producto[] }>('/productos', { params })
      productos.value = data.data
    } finally {
      loading.value = false
    }
  }

  async function fetchCategorias() {
    const { data } = await api.get<{ data: Categoria[] }>('/categorias')
    categorias.value = data.data
  }

  async function crear(producto: ProductoRequest) {
    const { data } = await api.post<{ data: Producto }>('/productos', producto)
    productos.value.push(data.data)
    return data.data
  }

  async function actualizar(codigo: string, producto: ProductoRequest) {
    const { data } = await api.put<{ data: Producto }>(`/productos/${codigo}`, producto)
    const idx = productos.value.findIndex((p) => p.codigo === codigo)
    if (idx !== -1) productos.value[idx] = data.data
    return data.data
  }

  async function eliminar(codigo: string) {
    await api.delete(`/productos/${codigo}`)
    productos.value = productos.value.filter((p) => p.codigo !== codigo)
  }

  return { productos, categorias, loading, fetchAll, fetchCategorias, crear, actualizar, eliminar }
})
