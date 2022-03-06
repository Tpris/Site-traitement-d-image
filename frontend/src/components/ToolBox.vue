<template>
  <ul class="neumorphism">
    <li class="item">
      <item></item>
    </li>
    <li class="item">
      <item></item>
    </li>
    <li class="item">
      <item></item>
    </li>
    <li class="item">
      <item></item>
    </li>
    <li class="item">
      <item></item>
    </li>
    <li class="item">
      <item></item>
    </li>
    <li class="item">
      <item></item>
    </li>
    <li class="item">
      <item></item>
    </li>
    <li class="item">
      <item></item>
    </li>
    <li class="item">
      <item></item>
    </li>

  </ul>
</template>

<script setup lang="ts">
import Item from '@/components/Item.vue'
</script>

<style scoped>
.item{
  margin-top: 20px;
}
ul{
  border-radius: 20px;
  width: 80px;
  height: 71vh;
  display: flex;
  align-items: center;
  flex-direction: column;
  overflow-y: scroll ;
}

ul::-webkit-scrollbar {
  display: none;
}
</style>