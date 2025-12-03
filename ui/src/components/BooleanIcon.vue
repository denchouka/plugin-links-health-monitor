<!-- components/BooleanIcon.vue -->
<template>
  <svg
    class="icon"
    viewBox="0 0 1024 1024"
    version="1.1"
    xmlns="http://www.w3.org/2000/svg"
    :width="size"
    :height="size"
  >
    <path :d="iconPath" :fill="iconColor"></path>
  </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  value: boolean
  size?: number
}

const props = withDefaults(defineProps<Props>(), {
  size: 20
})

// 成功图标路径和颜色
const successIcon = {
  path: 'M512 1024a512 512 0 1 1 512-512 512 512 0 0 1-512 512z m-68.096-443.904L324.096 460.8a56.32 56.32 0 0 0-79.872 79.872l159.232 159.232a54.784 54.784 0 0 0 79.872 0l324.096-324.096a56.32 56.32 0 0 0-79.872-79.872l-283.648 284.16z',
  //color: '#1C7CCB'
  color: '#2888bd'
}

// 失败图标路径和颜色
const errorIcon = {
  path: 'M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64z m187.3 564.1c19.7 19.8 19.7 51.8-0.1 71.5-9.4 9.4-22.2 14.7-35.6 14.8-13.4 0-26.2-5.3-35.6-14.8L512 583.3l-116.1 116c-18.6 20.7-50.5 22.5-71.2 3.9-20.7-18.6-22.5-50.5-3.9-71.2 1.2-1.4 2.5-2.7 3.9-3.9l116-115.9-116-116.1c-19.7-19.7-19.7-51.7 0-71.4 19.7-19.7 51.7-19.7 71.4 0l116 116 116.1-116.1c19.7-19.7 51.7-19.7 71.4 0 19.7 19.7 19.7 51.7 0 71.4L583.5 512l115.8 116.1z',
  color: '#9e9c9c'
}

// 根据布尔值选择对应的图标
const iconPath = computed(() => props.value ? successIcon.path : errorIcon.path)
const iconColor = computed(() => props.value ? successIcon.color : errorIcon.color)
</script>

<style lang="scss" scoped>
.icon {
  display: inline-block !important;
  vertical-align: middle !important;
}
</style>
