<script setup lang="ts">
import { api } from '@/http-api';
import {useImageStore} from "@/store";
import {storeToRefs} from "pinia";

const store = useImageStore()

let { selectedImage, deleted } = storeToRefs(store)

const deleteImage = (id: number) => {
  api.deleteImage(id).then(() => {
    selectedImage.value = {id: -1, source : '', name: '', type:'', size:''}
    deleted.value = true
  }).catch(e => {
    console.log(e.message);
  });
}
</script>

<template>
  <button class="button neumorphism neumorphism-push" v-if="selectedImage.source && selectedImage.id !== -1" @click="deleteImage(selectedImage.id)">
    Supprimer
  </button>
  <button class="button neumorphism neumorphism-push" v-else>Supprimer</button>
</template>

<style scoped>
.button{
  font-family: Helvetica, Arial,  sans-serif;
  font-size: 16px;
  margin-right: 50px;
  border-radius: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 35px;
  cursor: pointer;
  border:none;
}
</style>