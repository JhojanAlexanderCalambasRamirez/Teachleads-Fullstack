<template>
  <q-page class="q-pa-md">
    <PageHeader title="Empresas" subtitle="Gestión de empresas registradas">
      <template #actions v-if="authStore.isAdmin">
        <q-btn color="primary" icon="add" label="Nueva Empresa" @click="openDialog()" />
      </template>
    </PageHeader>

    <EmpresaTable
      :rows="empresaStore.empresas"
      :loading="empresaStore.loading"
      :show-actions="authStore.isAdmin"
      @edit="openDialog"
      @delete="confirmDelete"
    />

    <EmpresaFormDialog
      v-model="dialogOpen"
      :is-editing="isEditing"
      :initial-data="selectedEmpresa"
      :loading="saving"
      @submit="handleSave"
    />
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useQuasar } from 'quasar'
import PageHeader from 'src/components/shared/PageHeader.vue'
import EmpresaTable from 'src/components/empresa/EmpresaTable.vue'
import EmpresaFormDialog from 'src/components/empresa/EmpresaFormDialog.vue'
import { useEmpresaStore, type Empresa } from 'src/stores/empresa.store'
import { useAuthStore } from 'src/stores/auth.store'

const $q = useQuasar()
const empresaStore = useEmpresaStore()
const authStore = useAuthStore()

const dialogOpen = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const selectedEmpresa = ref<Empresa | null>(null)

onMounted(() => empresaStore.fetchAll())

function openDialog(empresa?: Empresa) {
  isEditing.value = !!empresa
  selectedEmpresa.value = empresa ?? null
  dialogOpen.value = true
}

async function handleSave(form: Empresa) {
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
