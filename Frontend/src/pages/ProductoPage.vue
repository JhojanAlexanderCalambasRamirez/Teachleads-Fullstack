<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <div class="col">
        <div class="text-h5">Productos</div>
        <div class="text-caption text-grey-6">Gestión de productos por empresa</div>
      </div>
      <div class="col-auto">
        <q-btn color="primary" icon="add" label="Nuevo Producto" @click="openDialog()" />
      </div>
    </div>

    <div class="q-mb-md">
      <q-select
        v-model="filtroEmpresa"
        :options="empresaOptions"
        option-value="nit"
        option-label="nombre"
        label="Filtrar por empresa"
        outlined
        clearable
        emit-value
        map-options
        style="max-width: 300px"
        @update:model-value="productoStore.fetchAll($event ?? undefined)"
      />
    </div>

    <q-table
      :rows="productoStore.productos"
      :columns="columns"
      row-key="codigo"
      :loading="productoStore.loading"
      flat
      bordered
    >
      <template #body-cell-precios="props">
        <q-td :props="props">
          <q-chip
            v-for="p in props.row.precios"
            :key="p.moneda"
            size="sm"
            color="blue-1"
            text-color="blue-9"
          >
            {{ p.moneda }}: {{ p.precio }}
          </q-chip>
        </q-td>
      </template>
      <template #body-cell-categorias="props">
        <q-td :props="props">
          <q-chip
            v-for="c in props.row.categorias"
            :key="c.id"
            size="sm"
            color="green-1"
            text-color="green-9"
          >
            {{ c.nombre }}
          </q-chip>
        </q-td>
      </template>
      <template #body-cell-acciones="props">
        <q-td :props="props">
          <q-btn flat round icon="edit" color="primary" size="sm" @click="openDialog(props.row)" />
          <q-btn flat round icon="delete" color="negative" size="sm" @click="confirmDelete(props.row.codigo)" />
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="dialogOpen" persistent>
      <q-card style="min-width: 560px; max-width: 700px">
        <q-card-section class="bg-primary text-white">
          <div class="text-h6">{{ isEditing ? 'Editar Producto' : 'Nuevo Producto' }}</div>
        </q-card-section>

        <q-card-section class="q-gutter-md q-pt-lg" style="max-height: 70vh; overflow-y: auto">
          <div class="row q-col-gutter-md">
            <div class="col-6">
              <q-input v-model="form.codigo" label="Código *" outlined :disable="isEditing"
                :rules="[val => !!val || 'Requerido']" />
            </div>
            <div class="col-6">
              <q-select v-model="form.empresaNit" :options="empresaOptions" option-value="nit"
                option-label="nombre" label="Empresa *" outlined emit-value map-options
                :rules="[val => !!val || 'Requerido']" />
            </div>
          </div>

          <q-input v-model="form.nombre" label="Nombre del producto *" outlined
            :rules="[val => !!val || 'Requerido']" />

          <q-input v-model="form.caracteristicas" label="Características" outlined type="textarea" rows="3" />

          <q-select v-model="form.categoriaIds" :options="productoStore.categorias"
            option-value="id" option-label="nombre" label="Categorías"
            outlined multiple emit-value map-options use-chips />

          <div class="text-subtitle2 q-mt-md">Precios en múltiples monedas</div>
          <div v-for="(precio, idx) in form.precios" :key="idx" class="row q-col-gutter-sm items-center">
            <div class="col-4">
              <q-select v-model="precio.moneda" :options="monedas" label="Moneda" outlined dense />
            </div>
            <div class="col-6">
              <q-input v-model.number="precio.precio" label="Precio" outlined dense type="number" min="0" />
            </div>
            <div class="col-2">
              <q-btn flat round icon="remove_circle" color="negative" size="sm"
                @click="form.precios.splice(idx, 1)" />
            </div>
          </div>
          <q-btn flat icon="add" label="Agregar precio" color="primary" size="sm"
            @click="form.precios.push({ moneda: 'COP', precio: 0 })" />
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn color="primary" :label="isEditing ? 'Actualizar' : 'Crear'"
            :loading="saving" @click="handleSave" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useQuasar } from 'quasar'
import { useProductoStore, type Producto } from 'src/stores/producto.store'
import { useEmpresaStore } from 'src/stores/empresa.store'

const $q = useQuasar()
const productoStore = useProductoStore()
const empresaStore = useEmpresaStore()

const dialogOpen = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const filtroEmpresa = ref<string | null>(null)
const monedas = ['COP', 'USD', 'EUR', 'GBP']

interface FormState {
  codigo: string
  nombre: string
  caracteristicas: string
  empresaNit: string
  precios: { moneda: string; precio: number }[]
  categoriaIds: number[]
}

const emptyForm = (): FormState => ({
  codigo: '',
  nombre: '',
  caracteristicas: '',
  empresaNit: '',
  precios: [{ moneda: 'COP', precio: 0 }],
  categoriaIds: []
})

const form = reactive<FormState>(emptyForm())

const empresaOptions = computed(() => empresaStore.empresas)

const columns = [
  { name: 'codigo', label: 'Código', field: 'codigo', sortable: true, align: 'left' as const },
  { name: 'nombre', label: 'Nombre', field: 'nombre', sortable: true, align: 'left' as const },
  { name: 'empresaNombre', label: 'Empresa', field: 'empresaNombre', align: 'left' as const },
  { name: 'precios', label: 'Precios', field: 'precios', align: 'left' as const },
  { name: 'categorias', label: 'Categorías', field: 'categorias', align: 'left' as const },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' as const }
]

onMounted(async () => {
  await Promise.all([
    productoStore.fetchAll(),
    productoStore.fetchCategorias(),
    empresaStore.fetchAll()
  ])
})

function openDialog(producto?: Producto) {
  isEditing.value = !!producto
  Object.assign(form, emptyForm())
  if (producto) {
    form.codigo = producto.codigo
    form.nombre = producto.nombre
    form.caracteristicas = producto.caracteristicas
    form.empresaNit = producto.empresaNit
    form.precios = producto.precios.map(p => ({ moneda: p.moneda, precio: Number(p.precio) }))
    form.categoriaIds = producto.categorias.map(c => c.id)
  }
  dialogOpen.value = true
}

async function handleSave() {
  if (!form.precios.length) {
    $q.notify({ type: 'warning', message: 'Agrega al menos un precio' })
    return
  }
  saving.value = true
  try {
    if (isEditing.value) {
      await productoStore.actualizar(form.codigo, form)
      $q.notify({ type: 'positive', message: 'Producto actualizado' })
    } else {
      await productoStore.crear(form)
      $q.notify({ type: 'positive', message: 'Producto creado' })
    }
    dialogOpen.value = false
  } catch (e: unknown) {
    const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Error al guardar'
    $q.notify({ type: 'negative', message: msg })
  } finally {
    saving.value = false
  }
}

function confirmDelete(codigo: string) {
  $q.dialog({
    title: 'Confirmar eliminación',
    message: `¿Eliminar producto ${codigo}?`,
    cancel: true
  }).onOk(async () => {
    try {
      await productoStore.eliminar(codigo)
      $q.notify({ type: 'positive', message: 'Producto eliminado' })
    } catch {
      $q.notify({ type: 'negative', message: 'Error al eliminar' })
    }
  })
}
</script>
