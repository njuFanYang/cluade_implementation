/**
 * Application entry point
 *
 * @description Initializes Vue application with plugins and global configurations
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import '@/styles/global.scss'

// Create Vue application instance
const app = createApp(App)

// Register Element Plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// Use plugins
app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// Mount application
app.mount('#app')
