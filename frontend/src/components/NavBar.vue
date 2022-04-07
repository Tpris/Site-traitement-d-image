<script setup lang="ts">
import {useImageStore} from "@/store";
const store = useImageStore()
import {storeToRefs} from "pinia";
import {api} from "@/http-api";
import {ref, onMounted} from "vue";
let { selectedImage, deleted, uploaded } = storeToRefs(store)

const isGallery = ref(  window.location.pathname == "/gallery")
const isMobile = ref(false)
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

onMounted(() => {
  isMobile.value = window.matchMedia('(min-width: 360px) and (max-width:640px)').matches
  window.matchMedia('(min-width: 360px) and (max-width:640px)').addEventListener('change', e => isMobile.value = e.matches)
})
</script>

<template>
  <nav class="neumorphism" id="nav-bar">
    <div id="container-logo" v-if="isMobile">
      <router-link to="/">
        <input v-bind:class=" isGallery? 'logo-nav-g': 'logo-nav'" type="image" src="../../public/logoApp.png"/>
      </router-link>
    </div>
      <router-link v-else to="/" class="no-deco-link">
        <h1 class="title">Image in dragon</h1>
      </router-link>
   
    <div v-if="!isGallery"  class="items">
      <router-link class="button link neumorphism neumorphism-push" to="/gallery">
        <span v-if="isMobile"><input type="image" src="../../public/gallery.png" class="icon-nav"/></span>
        <span v-else>Galerie</span>
      </router-link>

      <a class="button neumorphism neumorphism-push"
         v-if="selectedImage.source !== '' && selectedImage.id !== -1"
         :href="selectedImage.source"
         :download="selectedImage.name">
        <span v-if="isMobile"><input type="image" src="../../public/download.png" class="icon-nav"/></span>
        <span v-else>Télécharger</span>
      </a>
      <button class="button neumorphism neumorphism-push" v-else>
        <span v-if="isMobile"><input type="image" src="../../public/download.png" class="icon-nav"/></span>
        <span v-else>Télécharger</span>
      </button>

      <label class="button neumorphism neumorphism-push" for="file">
        <span v-if="isMobile"><input type="image" src="../../public/add.png" class="icon-nav"/></span>

        <span v-else>Ajouter</span>
      </label>
      <div id="input-upload">
        <input type="file" id="file" ref="file" @change="handleFileUpload" />
      </div>

      <button class="button neumorphism neumorphism-push" v-if="selectedImage.source && selectedImage.id !== -1" @click="deleteImage(selectedImage.id)">

        <span v-if="isMobile"><input type="image" src="../../public/delete.png" class="icon-nav" /></span>
        <span v-else>Supprimer</span>
      </button>
      <button class="button neumorphism neumorphism-push" v-else>
        <span v-if="isMobile"><input type="image" src="../../public/delete.png" class="icon-nav"/></span>
        <span v-else>Supprimer</span>
      </button>
    </div>

    <div v-else class="items">
      <router-link v-if="isGallery" class="button link neumorphism neumorphism-push" to="/">
        <span v-if="isMobile"><input type="image" src="../../public/edit.png" class="icon-nav"/></span>
        <span v-else>Editeur</span>
      </router-link>
    </div>

  </nav>
</template>

<style scoped>

.icon-nav{
    max-width: 25%;
    max-height: 25%;
    margin-top: 5%;
}

#container-logo{
    text-align: left;
    margin-left: 20px;
}

.logo-nav{
    width: 34%;
    height: 34%;
}

.logo-nav-g{
    width: 12%;
    height: 12%;
}

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

.title{
  margin-left: 10px;
}

.no-deco-link{
  text-decoration: none;
  color: inherit;
}


.items{
  display: flex;
  margin-left: auto;
  margin-right: 10px;
}

@media (min-width: 360px) and (max-width:640px){
  .button{
    width: 14vw;
    margin-right: 20px;
  }
}


</style>