<template>
  <div style="display: flex; align-items: center;">
    <i v-if="icon" :class="icon" style="margin-right: 8px;"></i>
    <span><slot v-if="!title">{{ title }}</slot></span>
  </div>
</template>

<script>
export default {
  name: 'Item',
  props: {
    icon: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    }
  }
}
</script>
