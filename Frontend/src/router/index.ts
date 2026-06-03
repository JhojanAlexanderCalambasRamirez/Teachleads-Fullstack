import { route } from 'quasar/wrappers'
import { createRouter, createWebHistory } from 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean
    requiresAdmin?: boolean
  }
}

const routes = [
  {
    path: '/login',
    component: () => import('src/layouts/AuthLayout.vue'),
    children: [
      { path: '', component: () => import('src/pages/LoginPage.vue') }
    ]
  },
  {
    path: '/',
    component: () => import('src/layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/empresas' },
      {
        path: 'empresas',
        component: () => import('src/pages/EmpresaPage.vue')
      },
      {
        path: 'productos',
        component: () => import('src/pages/ProductoPage.vue'),
        meta: { requiresAdmin: true }
      },
      {
        path: 'inventario',
        component: () => import('src/pages/InventarioPage.vue'),
        meta: { requiresAdmin: true }
      }
    ]
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('src/pages/ErrorNotFound.vue')
  }
]

export default route(function () {
  const router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,
    history: createWebHistory()
  })

  router.beforeEach((to) => {
    const token = localStorage.getItem('token')
    const rol = localStorage.getItem('rol')
    if (to.meta.requiresAuth && !token) {
      return { path: '/login' }
    }
    if (to.meta.requiresAdmin && rol !== 'ADMIN') {
      return { path: '/empresas' }
    }
  })

  return router
})
