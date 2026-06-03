<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <div class="col">
        <div class="text-h5">Inventario</div>
        <div class="text-caption text-grey-6">Productos por empresa en inventario</div>
      </div>
      <div class="col-auto q-gutter-sm">
        <q-btn color="secondary" icon="download" label="Descargar PDF"
          @click="handleDownloadPdf" :loading="downloadingPdf" />
        <q-btn color="orange" icon="email" label="Enviar por Email"
          @click="emailDialogOpen = true" />
        <q-btn color="primary" icon="add" label="Agregar al inventario"
          @click="addDialogOpen = true" />
      </div>
    </div>

    <div class="q-mb-md">
      <q-select
        v-model="filtroEmpresa"
        :options="empresaStore.empresas"
        option-value="nit"
        option-label="nombre"
        label="Filtrar por empresa"
        outlined
        clearable
        emit-value
        map-options
        style="max-width: 300px"
        @update:model-value="inventarioStore.fetchAll($event ?? undefined)"
      />
    </div>

    <q-table
      :rows="inventarioStore.items"
      :columns="columns"
      row-key="id"
      :loading="inventarioStore.loading"
      flat
      bordered
    >
      <template #body-cell-acciones="props">
        <q-td :props="props">
          <q-btn flat round icon="delete" color="negative" size="sm"
            @click="confirmDelete(props.row.id)" />
        </q-td>
      </template>
    </q-table>

    <!-- Agregar producto al inventario -->
    <q-dialog v-model="addDialogOpen" persistent>
      <q-card style="min-width: 400px">
        <q-card-section class="bg-primary text-white">
          <div class="text-h6">Agregar al Inventario</div>
        </q-card-section>
        <q-card-section class="q-gutter-md q-pt-lg">
          <q-select v-model="addForm.empresaNit" :options="empresaStore.empresas"
            option-value="nit" option-label="nombre" label="Empresa *"
            outlined emit-value map-options
            :rules="[val => !!val || 'Requerido']" />
          <q-select v-model="addForm.productoCodigo" :options="productoOptions"
            option-value="codigo" option-label="nombre" label="Producto *"
            outlined emit-value map-options
            :rules="[val => !!val || 'Requerido']" />
          <q-input v-model.number="addForm.cantidad" label="Cantidad *"
            outlined type="number" min="1"
            :rules="[val => val > 0 || 'Debe ser mayor a 0']" />
        </q-card-section>
        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn color="primary" label="Agregar" :loading="saving" @click="handleAdd" />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- Dialog enviar email -->
    <q-dialog v-model="emailDialogOpen" persistent>
      <q-card style="min-width: 400px">
        <q-card-section class="bg-orange text-white">
          <div class="text-h6">Enviar Inventario por Email</div>
        </q-card-section>
        <q-card-section class="q-gutter-md q-pt-lg">
          <q-input v-model="emailForm.destinatario" label="Correo destinatario *"
            type="email" outlined
            :rules="[val => !!val || 'Requerido', val => /.+@.+\..+/.test(val) || 'Email inválido']" />
          <q-select v-model="emailForm.empresaNit" :options="empresaStore.empresas"
            option-value="nit" option-label="nombre" label="Filtrar empresa (opcional)"
            outlined emit-value map-options clearable />
        </q-card-section>
        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn color="orange" label="Enviar" :loading="sendingEmail" @click="handleSendEmail" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useQuasar } from 'quasar'
import { useInventarioStore } from 'src/stores/inventario.store'
import { useEmpresaStore } from 'src/stores/empresa.store'
import { useProductoStore } from 'src/stores/producto.store'

const $q = useQuasar()
const inventarioStore = useInventarioStore()
const empresaStore = useEmpresaStore()
const productoStore = useProductoStore()

const addDialogOpen = ref(false)
const emailDialogOpen = ref(false)
const saving = ref(false)
const sendingEmail = ref(false)
const downloadingPdf = ref(false)
const filtroEmpresa = ref<string | null>(null)

const addForm = reactive({ empresaNit: '', productoCodigo: '', cantidad: 1 })
const emailForm = reactive({ destinatario: '', empresaNit: null as string | null })

const productoOptions = computed(() => productoStore.productos)

const columns = [
  { name: 'empresaNit', label: 'NIT Empresa', field: 'empresaNit', sortable: true, align: 'left' as const },
  { name: 'empresaNombre', label: 'Empresa', field: 'empresaNombre', sortable: true, align: 'left' as const },
  { name: 'productoCodigo', label: 'Código', field: 'productoCodigo', align: 'left' as const },
  { name: 'productoNombre', label: 'Producto', field: 'productoNombre', sortable: true, align: 'left' as const },
  { name: 'productoCaracteristicas', label: 'Características', field: 'productoCaracteristicas', align: 'left' as const },
  { name: 'cantidad', label: 'Cantidad', field: 'cantidad', sortable: true, align: 'center' as const },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' as const }
]

onMounted(async () => {
  await Promise.all([
    inventarioStore.fetchAll(),
    empresaStore.fetchAll(),
    productoStore.fetchAll()
  ])
})

async function handleAdd() {
  saving.value = true
  try {
    await inventarioStore.agregar(addForm)
    $q.notify({ type: 'positive', message: 'Inventario actualizado' })
    addDialogOpen.value = false
    Object.assign(addForm, { empresaNit: '', productoCodigo: '', cantidad: 1 })
  } catch (e: unknown) {
    const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Error'
    $q.notify({ type: 'negative', message: msg })
  } finally {
    saving.value = false
  }
}

async function handleDownloadPdf() {
  downloadingPdf.value = true
  try {
    await inventarioStore.descargarPdf(filtroEmpresa.value ?? undefined)
    $q.notify({ type: 'positive', message: 'PDF descargado' })
  } catch {
    $q.notify({ type: 'negative', message: 'Error generando PDF' })
  } finally {
    downloadingPdf.value = false
  }
}

async function handleSendEmail() {
  sendingEmail.value = true
  try {
    await inventarioStore.enviarPdf(emailForm.destinatario, emailForm.empresaNit ?? undefined)
    $q.notify({ type: 'positive', message: `Email enviado a ${emailForm.destinatario}` })
    emailDialogOpen.value = false
  } catch {
    $q.notify({ type: 'negative', message: 'Error enviando email' })
  } finally {
    sendingEmail.value = false
  }
}

function confirmDelete(id: number) {
  $q.dialog({
    title: 'Confirmar',
    message: '¿Eliminar este item del inventario?',
    cancel: true
  }).onOk(async () => {
    try {
      await inventarioStore.eliminar(id)
      $q.notify({ type: 'positive', message: 'Item eliminado' })
    } catch {
      $q.notify({ type: 'negative', message: 'Error al eliminar' })
    }
  })
}
</script>
