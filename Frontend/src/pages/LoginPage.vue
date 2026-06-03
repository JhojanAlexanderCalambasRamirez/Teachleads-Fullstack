<template>
  <q-page class="flex flex-center">
    <q-card class="login-card shadow-8">
      <q-card-section class="bg-primary text-white text-center q-py-lg">
        <q-icon name="business_center" size="3rem" />
        <div class="text-h5 q-mt-sm">TechLeads</div>
        <div class="text-caption">Sistema de Gestión Empresarial</div>
      </q-card-section>

      <q-card-section class="q-pa-lg">
        <q-form @submit.prevent="handleLogin" class="q-gutter-md">
          <q-input
            v-model="form.correo"
            label="Correo electrónico"
            type="email"
            outlined
            :rules="[val => !!val || 'Requerido', val => /.+@.+\..+/.test(val) || 'Correo inválido']"
          >
            <template #prepend>
              <q-icon name="email" />
            </template>
          </q-input>

          <q-input
            v-model="form.password"
            label="Contraseña"
            :type="showPassword ? 'text' : 'password'"
            outlined
            :rules="[val => !!val || 'Requerido']"
          >
            <template #prepend>
              <q-icon name="lock" />
            </template>
            <template #append>
              <q-icon
                :name="showPassword ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="showPassword = !showPassword"
              />
            </template>
          </q-input>

          <q-btn
            type="submit"
            label="Iniciar Sesión"
            color="primary"
            class="full-width"
            size="lg"
            :loading="loading"
          />
        </q-form>
      </q-card-section>

      <q-card-section class="text-center text-caption text-grey-6 q-pt-none">
        Admin: admin@techleads.com / admin123<br />
        Externo: externo@techleads.com / externo123
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { useAuthStore } from 'src/stores/auth.store'

const $q = useQuasar()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const showPassword = ref(false)
const form = reactive({ correo: '', password: '' })

async function handleLogin() {
  loading.value = true
  try {
    await authStore.login(form)
    $q.notify({ type: 'positive', message: `Bienvenido, ${authStore.nombre}` })
    router.push('/')
  } catch {
    $q.notify({ type: 'negative', message: 'Credenciales inválidas' })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 12px;
}
</style>
