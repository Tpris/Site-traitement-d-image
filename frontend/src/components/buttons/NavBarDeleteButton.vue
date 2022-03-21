<script setup lang="ts">
import { api } from '@/http-api';
defineProps<{selectedImage: { id:number, source:string, name:string }}>()
const emit = defineEmits(['updated'])

const deleteImage = (id: number) => {
  api.deleteImage(id).then(() => {
    emit('updated')
  }).catch(e => {
    console.log(e.message);
  });
}
</script>

<template>
  <a class="button neumorphism neumorphism-push"  v-if="selectedImage.source && selectedImage.id !== -1" @click="deleteImage(selectedImage.id)">
    Supprimer
  </a>
  <a class="button neumorphism neumorphism-push" v-else>Supprimer</a>
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
}
</style>