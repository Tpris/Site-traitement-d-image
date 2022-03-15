<script setup lang="ts">
defineProps<{ selectedImage: { id:number, source:string }}>()
</script>

<template>
  <a class="button neumorphism neumorphism-push"
     v-if="selectedImage.source && selectedImage.id !== -1"
     :href="selectedImage.source"
     :download="selectedImage.id">
    Download
  </a>
  <a class="button neumorphism neumorphism-push" v-else>Download</a>
</template>

<style scoped>
.button{
  margin-right: 50px;
  border-radius: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 35px;
  cursor: pointer;
  text-decoration: none;
  color: #2c3e50;
}

.button:hover{
  color: #0777D9;
}
</style>