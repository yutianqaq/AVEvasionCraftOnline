<script setup>
defineProps({
  msg: {
    type: String,
    default: ''
  }
})
</script>

<template>
  <p style="font-size: 30px; font-weight: 600">{{ msg }}</p>

  <div class="card">
    <p>技术： Vite3 + Vue3 + Vuex + Vue Router + Axios</p>

    <p>已集成黑夜模式</p>
  </div>

  <p class="read-the-docs">仅做模板使用，无其他附加功能和杂乱设计</p>
</template>

<style scoped>
.read-the-docs {
  color: #888;
}
</style>
