<template>
 <div class="neumorphism">
     <item></item>
     <item></item>
     <item></item>
     <item></item>
     <item></item>
     <item></item>
     <item></item>
     <item></item>
     <item></item>
 </div>
</template>

<script setup lang="ts">
import Item from '@/components/Item.vue'
</script>

<style scoped>
div{
  border-radius: 20px;
  width: 80px;
  height: 71vh;
  display: flex;
  align-items: center;
  flex-direction: column;
}
</style>