import { defineConfig } from '#q-app/wrappers'

export default defineConfig((ctx) => {
  return {
    boot: ['pinia', 'axios'],

    css: ['app.scss'],

    extras: ['roboto-font', 'material-icons', 'mdi-v7'],

    build: {
      target: {
        browser: ['es2022', 'firefox115', 'chrome115', 'safari14'],
        node: 'node20'
      },
      vueRouterMode: 'history',
      typescript: {
        strict: true
      }
    },

    devServer: {
      port: 9000,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },

    framework: {
      config: {
        notify: { position: 'top-right', timeout: 3000 }
      },
      plugins: ['Notify', 'Dialog', 'Loading', 'LocalStorage']
    },

    animations: 'all',

    ssr: { pwa: false, prodPort: 3000, middlewares: ['render'] },
    pwa: {
      workboxMode: 'GenerateSW',
      injectPwaMetaTags: true
    }
  }
})
