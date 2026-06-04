<template>
  <q-dialog :model-value="modelValue" persistent @update:model-value="$emit('update:modelValue', $event)">
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
        <q-btn flat label="Cancelar" @click="$emit('update:modelValue', false)" />
        <q-btn
          color="primary"
          :label="isEditing ? 'Actualizar' : 'Crear'"
          :loading="loading"
          @click="$emit('submit', form)"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue'
import type { Empresa } from 'src/stores/empresa.store'

interface Props {
  modelValue: boolean
  isEditing?: boolean
  initialData?: Empresa | null
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isEditing: false,
  initialData: null,
  loading: false
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'submit': [form: Empresa]
}>()

const form = reactive<Empresa>({ nit: '', nombre: '', direccion: '', telefono: '' })

watch(() => props.initialData, (data) => {
  if (data) Object.assign(form, data)
  else Object.assign(form, { nit: '', nombre: '', direccion: '', telefono: '' })
}, { immediate: true })
</script>
