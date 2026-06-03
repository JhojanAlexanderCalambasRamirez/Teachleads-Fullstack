<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeftDrawer" />
        <q-toolbar-title>
          <q-icon name="business_center" class="q-mr-sm" />
          TechLeads
        </q-toolbar-title>
        <div class="q-mr-md text-caption">
          {{ authStore.nombre }} ({{ authStore.rol }})
        </div>
        <q-btn flat icon="logout" label="Salir" @click="handleLogout" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered>
      <q-list>
        <q-item-label header class="text-grey-8">Navegación</q-item-label>

        <q-item clickable v-ripple to="/empresas" exact>
          <q-item-section avatar>
            <q-icon name="business" />
          </q-item-section>
          <q-item-section>Empresas</q-item-section>
        </q-item>

        <template v-if="authStore.isAdmin">
          <q-item clickable v-ripple to="/productos" exact>
            <q-item-section avatar>
              <q-icon name="inventory_2" />
            </q-item-section>
            <q-item-section>Productos</q-item-section>
          </q-item>

          <q-item clickable v-ripple to="/inventario" exact>
            <q-item-section avatar>
              <q-icon name="warehouse" />
            </q-item-section>
            <q-item-section>Inventario</q-item-section>
          </q-item>
        </template>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from 'src/stores/auth.store'

const authStore = useAuthStore()
const router = useRouter()
const leftDrawerOpen = ref(false)

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
