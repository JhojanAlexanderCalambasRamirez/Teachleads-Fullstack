<template>
  <q-page class="q-pa-md">
    <div class="row items-center q-mb-md">
      <div class="col">
        <div class="text-h5">Empresas</div>
        <div class="text-caption text-grey-6">Gestión de empresas registradas</div>
      </div>
      <div class="col-auto" v-if="authStore.isAdmin">
        <q-btn color="primary" icon="add" label="Nueva Empresa" @click="openDialog()" />
      </div>
    </div>

    <q-table
      :rows="empresaStore.empresas"
      :columns="columns"
      row-key="nit"
      :loading="empresaStore.loading"
      flat
      bordered
    >
      <template #body-cell-acciones="props">
        <q-td :props="props">
          <template v-if="authStore.isAdmin">
            <q-btn flat round icon="edit" color="primary" size="sm" @click="openDialog(props.row)" />
            <q-btn flat round icon="delete" color="negative" size="sm" @click="confirmDelete(props.row.nit)" />
          </template>
          <span v-else class="text-grey-5 text-caption">Solo lectura</span>
        </q-td>
      </template>
    </q-table>

    <!-- Dialog crear/editar -->
    <q-dialog v-model="dialogOpen" persistent>
      <q-card style="min-width: 480px">
        <q-card-section class="bg-primary text-white">
          <div class="text-h6">{{ isEditing ? 'Editar Empresa' : 'Nueva Empresa' }}</div>
        </q-card-section>

        <q-card-section class="q-gutter-md q-pt-lg">
          <q-input
            v-model="form.nit"
            label="NIT *"
            outlined
            :disable="isEditing"
            :rules="[val => !!val || 'Requerido']"
          />
          <q-input
            v-model="form.nombre"
            label="Nombre de la empresa *"
            outlined
            :rules="[val => !!val || 'Requerido']"
          />
          <q-input
            v-model="form.direccion"
            label="Dirección *"
            outlined
            :rules="[val => !!val || 'Requerido']"
          />
          <q-input
            v-model="form.telefono"
            label="Teléfono *"
            outlined
            :rules="[val => !!val || 'Requerido']"
          />
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn
            color="primary"
            :label="isEditing ? 'Actualizar' : 'Crear'"
            :loading="saving"
            @click="handleSave"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import { useEmpresaStore, type Empresa } from 'src/stores/empresa.store'
import { useAuthStore } from 'src/stores/auth.store'

const $q = useQuasar()
const empresaStore = useEmpresaStore()
const authStore = useAuthStore()

const dialogOpen = ref(false)
const isEditing = ref(false)
const saving = ref(false)

const form = reactive<Empresa>({
  nit: '',
  nombre: '',
  direccion: '',
  telefono: ''
})

const columns = [
  { name: 'nit', label: 'NIT', field: 'nit', sortable: true, align: 'left' as const },
  { name: 'nombre', label: 'Nombre', field: 'nombre', sortable: true, align: 'left' as const },
  { name: 'direccion', label: 'Dirección', field: 'direccion', align: 'left' as const },
  { name: 'telefono', label: 'Teléfono', field: 'telefono', align: 'left' as const },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' as const }
]

onMounted(() => empresaStore.fetchAll())

function openDialog(empresa?: Empresa) {
  isEditing.value = !!empresa
  if (empresa) {
    Object.assign(form, empresa)
  } else {
    Object.assign(form, { nit: '', nombre: '', direccion: '', telefono: '' })
  }
  dialogOpen.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (isEditing.value) {
      await empresaStore.actualizar(form.nit, form)
      $q.notify({ type: 'positive', message: 'Empresa actualizada' })
    } else {
      await empresaStore.crear(form)
      $q.notify({ type: 'positive', message: 'Empresa creada' })
    }
    dialogOpen.value = false
  } catch (e: unknown) {
    const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Error al guardar'
    $q.notify({ type: 'negative', message: msg })
  } finally {
    saving.value = false
  }
}

function confirmDelete(nit: string) {
  $q.dialog({
    title: 'Confirmar eliminación',
    message: `¿Eliminar empresa con NIT ${nit}? Esta acción no se puede deshacer.`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await empresaStore.eliminar(nit)
      $q.notify({ type: 'positive', message: 'Empresa eliminada' })
    } catch {
      $q.notify({ type: 'negative', message: 'Error al eliminar' })
    }
  })
}
</script>
