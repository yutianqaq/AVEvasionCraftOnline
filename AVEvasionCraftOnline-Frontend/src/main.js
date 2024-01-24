import './style.css'

import router from './router'
import store from './store'

import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

import 'element-plus/theme-chalk/dark/css-vars.css' // ep-dark-css
import './style-dark.css' // dark-style
import 'normalize.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'


const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.use(router).use(store).use(ElementPlus).mount('#app')