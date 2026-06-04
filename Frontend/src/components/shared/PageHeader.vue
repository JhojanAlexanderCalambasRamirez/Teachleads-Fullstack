<template>
  <div class="row items-center q-mb-md">
    <div class="col">
      <div class="text-h5">{{ title }}</div>
      <div class="text-caption text-grey-6">{{ subtitle }}</div>
    </div>
    <div class="col-auto" v-if="$slots.actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  subtitle?: string
}>()
</script>
