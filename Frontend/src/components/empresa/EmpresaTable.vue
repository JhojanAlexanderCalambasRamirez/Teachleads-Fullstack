<template>
  <q-table
    :rows="rows"
    :columns="columns"
    row-key="nit"
    :loading="loading"
    flat
    bordered
  >
    <template #body-cell-acciones="props">
      <q-td :props="props">
        <template v-if="showActions">
          <q-btn flat round icon="edit" color="primary" size="sm" @click="$emit('edit', props.row)" />
          <q-btn flat round icon="delete" color="negative" size="sm" @click="$emit('delete', props.row.nit)" />
        </template>
        <span v-else class="text-grey-5 text-caption">Solo lectura</span>
      </q-td>
    </template>
  </q-table>
</template>

<script setup lang="ts">
import type { Empresa } from 'src/stores/empresa.store'

defineProps<{
  rows: Empresa[]
  loading: boolean
  showActions: boolean
}>()

defineEmits<{
  edit: [empresa: Empresa]
  delete: [nit: string]
}>()

const columns = [
  { name: 'nit', label: 'NIT', field: 'nit', sortable: true, align: 'left' as const },
  { name: 'nombre', label: 'Nombre', field: 'nombre', sortable: true, align: 'left' as const },
  { name: 'direccion', label: 'Dirección', field: 'direccion', align: 'left' as const },
  { name: 'telefono', label: 'Teléfono', field: 'telefono', align: 'left' as const },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' as const }
]
</script>
