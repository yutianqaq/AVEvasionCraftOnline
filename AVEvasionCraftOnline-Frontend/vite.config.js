import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

const config_dev = loadEnv('development', './')
const config_pro = loadEnv('production', './')
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    open: false,
    proxy: {
      // 匹配到的时开发环境
      '/dev-api': {
        target: config_dev.VITE_API_URL, //后台服务地址
        changeOrigin: true,
        //重写，/api开头的替换成空字符串，即去掉接口中去掉这个字符串
        rewrite: path => path.replace(/^\/dev-api/, '')
      },
      // 拦截请求地址包含/api，匹配到的是生产环境
      '/api': {
        target: config_pro.VITE_API_URL, //后台服务地址
        changeOrigin: true,
        // 重写，/api开头的替换成空字符串，即去掉接口中去掉这个字符串
        rewrite: path => path.replace(/^\/api/, '')
      }
    }
  },
  resolve: {
    // 别名src下的资源路径都可以以@/替换
    alias: [
      {
        find: '@',
        replacement: resolve(__dirname, 'src')
      }
    ],
    // 忽略.vue后缀
    extensions: ['.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
  }
})
