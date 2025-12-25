<template>
  <div class="info-icon-container" @mouseenter="showTitle" @mouseleave="hideTitle">
    <svg t="1766673092577" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="10827" width="20" height="20"><path d="M512 166.6c190.5 0 345.4 154.9 345.4 345.4S702.5 857.4 512 857.4 166.6 702.5 166.6 512 321.5 166.6 512 166.6m0-81.3c-235.6 0-426.7 191-426.7 426.7s191 426.7 426.7 426.7 426.7-191 426.7-426.7S747.7 85.3 512 85.3" fill="#666666" p-id="10828"></path><path d="M512 256c29.5 0 53.3 23.9 53.3 53.3s-23.9 53.3-53.3 53.3c-29.5 0-53.3-23.9-53.3-53.3S482.5 256 512 256z m0 170.7c23.6 0 42.7 19.1 42.7 42.7v256c0 23.6-19.1 42.7-42.7 42.7s-42.7-19.1-42.7-42.7v-256c0-23.6 19.1-42.7 42.7-42.7z" fill="#00A2FF" p-id="10829"></path></svg>
    <!-- Tooltip -->
    <div v-if="isVisible" class="info-tooltip">
      {{ title }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: ''
  }
})

const isVisible = ref(false)

const showTitle = () => {
  isVisible.value = true
}

const hideTitle = () => {
  isVisible.value = false
}
</script>

<style scoped>
.info-icon-container {
  display: inline-block;
  vertical-align: middle;
  cursor: pointer;
  position: relative;
}

.info-tooltip {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background-color: #333;
  color: white;
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 1000;
  margin-bottom: 5px;
  pointer-events: none; /* 防止 tooltip 干扰 hover */
}

/* 可选：添加小三角形箭头 */
.info-tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  margin-left: -5px;
  border-width: 5px;
  border-style: solid;
  border-color: #333 transparent transparent transparent;
}
</style>
