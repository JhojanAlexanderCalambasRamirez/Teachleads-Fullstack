<template>
  <q-dialog :model-value="modelValue" persistent @update:model-value="$emit('update:modelValue', $event)">
    <q-card style="min-width: 420px">
      <q-card-section class="bg-orange text-white">
        <div class="text-h6">
          <q-icon name="email" class="q-mr-sm" />
          Enviar Inventario por Email
        </div>
      </q-card-section>
      <q-card-section class="q-gutter-md q-pt-lg">
        <q-input
          v-model="destinatario"
          label="Correo destinatario *"
          type="email"
          outlined
          :rules="[val => !!val || 'Requerido', val => /.+@.+\..+/.test(val) || 'Email inválido']"
        />
        <q-banner v-if="soloSeleccionados && selectedCount" class="bg-blue-1 text-blue-9 rounded-borders" dense>
          <template #avatar><q-icon name="check_box" /></template>
          Se enviarán <strong>{{ selectedCount }} item(s) seleccionado(s)</strong>
        </q-banner>
        <q-banner v-else class="bg-grey-2 rounded-borders" dense>
          <template #avatar><q-icon name="inventory_2" /></template>
          Se enviará <strong>todo el inventario</strong>
        </q-banner>
      </q-card-section>
      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat label="Cancelar" @click="$emit('update:modelValue', false)" />
        <q-btn color="orange" label="Enviar PDF" :loading="loading" @click="$emit('submit', destinatario)" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineProps<{
  modelValue: boolean
  soloSeleccionados: boolean
  selectedCount: number
  loading: boolean
}>()

defineEmits<{
  'update:modelValue': [value: boolean]
  'submit': [destinatario: string]
}>()

const destinatario = ref('')
</script>
