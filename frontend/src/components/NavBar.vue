<script setup lang="ts">
import {useImageStore} from "@/store";
const store = useImageStore()
import {storeToRefs} from "pinia";
import {api} from "@/http-api";
import {ref} from "vue";
let { selectedImage, deleted, uploaded } = storeToRefs(store)

const deleteImage = (id: number) => {
  api.deleteImage(id).then(() => {
    selectedImage.value = {id: -1, source : '', name: '', type:'', size:'', url:''}
    deleted.value = true
  }).catch(e => console.log(e.message));
}

const target = ref<HTMLInputElement>();

const submitFile = ()  =>{
  if(!target.value || !target.value?.files) return
  const file = target.value?.files[0];
  let formData = new FormData();
  formData.append("file", file);
  api.createImage(formData).then(() => {
    target.value = {} as HTMLInputElement
    uploaded.value = true
  }).catch(e => console.log(e.message));
}

const handleFileUpload = (event: Event) => {
  target.value = (event.target as HTMLInputElement)
  submitFile()
}

</script>

<template>
  <nav class="neumorphism" id="nav-bar">
    <h1 id="title">Image in dragon</h1>
    <div id="items">
      <router-link class="button link neumorphism neumorphism-push" to="/">
        Accueil
      </router-link>

      <a class="button neumorphism neumorphism-push"
         v-if="selectedImage.source !== '' && selectedImage.id !== -1"
         :href="selectedImage.source"
         :download="selectedImage.name">
        Télécharger
      </a>
      <button class="button neumorphism neumorphism-push" v-else>Télécharger</button>

      <label class="button neumorphism neumorphism-push" for="file">
        Ajouter
      </label>
      <div id="input-upload">
        <input type="file" id="file" ref="file" @change="handleFileUpload" />
      </div>

      <button class="button neumorphism neumorphism-push" v-if="selectedImage.source && selectedImage.id !== -1" @click="deleteImage(selectedImage.id)">
        Supprimer
      </button>
      <button class="button neumorphism neumorphism-push" v-else>Supprimer</button>
    </div>
  </nav>
</template>

<style scoped>
#nav-bar{
  display: flex;
  height: 70px;
  width: 100vw;
  align-items: center;
}

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
  text-decoration: none;
  color: #2c3e50;
  border:none;
}

.button:hover{
  color: #0777D9;
}

#input-upload{
  display: none;
}

#title{
  margin-left: 10px;
}

#items{
  display: flex;
  margin-left: auto;
  margin-right: 10px;
}
</style>