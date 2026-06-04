<template>
  <q-page class="q-pa-md">
    <!-- Header -->
    <div class="row items-center q-mb-md">
      <div class="col">
        <div class="text-h5">Inventario</div>
        <div class="text-caption text-grey-6">
          Productos por empresa
          <span v-if="selectedRows.length" class="text-primary q-ml-sm">
            · {{ selectedRows.length }} seleccionado(s)
          </span>
        </div>
      </div>
      <div class="col-auto q-gutter-sm">
        <q-btn color="primary" icon="add" label="Agregar" @click="addDialogOpen = true" />
      </div>
    </div>

    <!-- Filtro empresa -->
    <div class="row items-center q-mb-md q-gutter-sm">
      <q-select
        v-model="filtroEmpresa"
        :options="empresaStore.empresas"
        option-value="nit"
        option-label="nombre"
        label="Filtrar por empresa"
        outlined
        dense
        clearable
        emit-value
        map-options
        style="min-width: 250px"
        @update:model-value="cargarInventario"
      />
      <q-btn
        v-if="selectedRows.length"
        flat
        dense
        icon="deselect"
        label="Limpiar selección"
        color="grey-6"
        @click="selectedRows = []"
      />
    </div>

    <!-- Acciones PDF / Email - contextuales -->
    <div class="row q-mb-md q-gutter-sm">
        <q-btn
          outline
          color="secondary"
          icon="download"
          :label="selectedRows.length ? `Descargar selección (${selectedRows.length})` : 'Descargar todo'"
          @click="() => handleDownloadPdf(selectedRows.length > 0)"
          :loading="downloadingPdf"
        />
        <q-btn
          outline
          color="orange"
          icon="email"
          :label="selectedRows.length ? `Enviar selección (${selectedRows.length})` : 'Enviar todo por email'"
          @click="() => openEmailDialog(selectedRows.length > 0)"
        />
    </div>

    <!-- Tabla con selección -->
    <q-table
      :rows="inventarioStore.items"
      :columns="columns"
      row-key="id"
      :loading="inventarioStore.loading"
      flat
      bordered
      selection="multiple"
      v-model:selected="selectedRows"
    >
      <template #body-cell-acciones="props">
        <q-td :props="props">
          <q-btn flat round icon="delete" color="negative" size="sm"
            @click="confirmDelete(props.row.id)" />
        </q-td>
      </template>
    </q-table>

    <!-- Dialog agregar -->
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
          <q-select v-model="addForm.productoCodigo" :options="productoStore.productos"
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
      <q-card style="min-width: 420px">
        <q-card-section class="bg-orange text-white">
          <div class="text-h6">
            <q-icon name="email" class="q-mr-sm" />
            Enviar Inventario por Email
          </div>
        </q-card-section>
        <q-card-section class="q-gutter-md q-pt-lg">
          <q-input v-model="emailForm.destinatario" label="Correo destinatario *"
            type="email" outlined
            :rules="[val => !!val || 'Requerido', val => /.+@.+\..+/.test(val) || 'Email inválido']" />

          <q-banner
            v-if="emailForm.soloSeleccionados && selectedRows.length"
            class="bg-blue-1 text-blue-9 rounded-borders"
            dense
          >
            <template #avatar><q-icon name="check_box" /></template>
            Se enviarán <strong>{{ selectedRows.length }} item(s) seleccionado(s)</strong>
          </q-banner>
          <q-banner
            v-else-if="!emailForm.soloSeleccionados"
            class="bg-grey-2 rounded-borders"
            dense
          >
            <template #avatar><q-icon name="inventory_2" /></template>
            Se enviará <strong>todo el inventario</strong>
            {{ filtroEmpresa ? `de la empresa filtrada` : '' }}
          </q-banner>
        </q-card-section>
        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn color="orange" label="Enviar PDF" :loading="sendingEmail" @click="handleSendEmail" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useInventarioStore, type InventarioItem } from 'src/stores/inventario.store'
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
const selectedRows = ref<InventarioItem[]>([])

const addForm = reactive({ empresaNit: '', productoCodigo: '', cantidad: 1 })
const emailForm = reactive({ destinatario: '', soloSeleccionados: false })

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

function cargarInventario() {
  selectedRows.value = []
  inventarioStore.fetchAll(filtroEmpresa.value ?? undefined)
}

function openEmailDialog(soloSeleccionados: boolean) {
  emailForm.destinatario = ''
  emailForm.soloSeleccionados = soloSeleccionados
  emailDialogOpen.value = true
}

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

async function handleDownloadPdf(soloSeleccionados?: boolean) {
  const usarSeleccion = soloSeleccionados === true ||
    (soloSeleccionados === undefined && selectedRows.value.length > 0)

  const ids = usarSeleccion ? selectedRows.value.map(r => r.id) : undefined

  downloadingPdf.value = true
  try {
    await inventarioStore.descargarPdf(
      ids,
      ids ? undefined : (filtroEmpresa.value ?? undefined)
    )
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
    const ids = emailForm.soloSeleccionados
      ? selectedRows.value.map(r => r.id)
      : undefined

    await inventarioStore.enviarPdf(
      emailForm.destinatario,
      ids,
      ids ? undefined : (filtroEmpresa.value ?? undefined)
    )
    $q.notify({ type: 'positive', message: `Email enviado a ${emailForm.destinatario}` })
    emailDialogOpen.value = false
  } catch {
    $q.notify({ type: 'negative', message: 'Error enviando email. Verifica credenciales de correo.' })
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
      selectedRows.value = selectedRows.value.filter(r => r.id !== id)
      $q.notify({ type: 'positive', message: 'Item eliminado' })
    } catch {
      $q.notify({ type: 'negative', message: 'Error al eliminar' })
    }
  })
}
</script>
