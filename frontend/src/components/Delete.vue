<script setup lang="ts">
import { api } from '@/http-api';
defineProps<{selectedImage: { id:number, source:string }}>()
const emit = defineEmits(['updated'])

const deleteImage = (id) => {
  api.deleteImage(id).then(() => {
    emit('updated')
  }).catch(e => {
    console.log(e.message);
  });
}
</script>

<template>
  <a v-if="selectedImage.source && selectedImage.id !== -1" @click="deleteImage(selectedImage.id)">
    Supprimer
  </a>
  <a v-else>Supprimer</a>
</template>

<style scoped>
</style>
