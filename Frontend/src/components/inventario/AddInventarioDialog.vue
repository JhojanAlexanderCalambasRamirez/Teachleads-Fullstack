<template>
  <q-dialog :model-value="modelValue" persistent @update:model-value="$emit('update:modelValue', $event)">
    <q-card style="min-width: 400px">
      <q-card-section class="bg-primary text-white">
        <div class="text-h6">Agregar al Inventario</div>
      </q-card-section>
      <q-card-section class="q-gutter-md q-pt-lg">
        <q-select
          v-model="form.empresaNit"
          :options="empresas"
          option-value="nit"
          option-label="nombre"
          label="Empresa *"
          outlined
          emit-value
          map-options
          :rules="[val => !!val || 'Requerido']"
        />
        <q-select
          v-model="form.productoCodigo"
          :options="productos"
          option-value="codigo"
          option-label="nombre"
          label="Producto *"
          outlined
          emit-value
          map-options
          :rules="[val => !!val || 'Requerido']"
        />
        <q-input
          v-model.number="form.cantidad"
          label="Cantidad *"
          outlined
          type="number"
          min="1"
          :rules="[val => val > 0 || 'Debe ser mayor a 0']"
        />
      </q-card-section>
      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat label="Cancelar" @click="$emit('update:modelValue', false)" />
        <q-btn color="primary" label="Agregar" :loading="loading" @click="$emit('submit', form)" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import type { Empresa } from 'src/stores/empresa.store'
import type { Producto } from 'src/stores/producto.store'

defineProps<{
  modelValue: boolean
  empresas: Empresa[]
  productos: Producto[]
  loading: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'submit': [form: { empresaNit: string; productoCodigo: string; cantidad: number }]
}>()

const form = reactive({ empresaNit: '', productoCodigo: '', cantidad: 1 })
</script>
