<script setup lang="ts">
import NavBarUploadButton from "@/components/buttons/NavBarUploadButton.vue"
import NavBarDeleteButton from "@/components/buttons/NavBarDeleteButton.vue";
import NavBarDownloadButton from "@/components/buttons/NavBarDownloadButton.vue"
import NavBarRouterButton from "@/components/buttons/NavBarRouterButton.vue";

defineProps<{ name: "", selectedImage: { id:number, source:string, name:string }}>()
defineEmits(['uploaded', 'deleted'])
</script>

<template>
  <nav class="neumorphism" id="nav-bar">
    <h1 id="title">Notre super projet</h1>
    <div id="items">
      <NavBarRouterButton :name="name">Accueil</NavBarRouterButton>
      <NavBarDownloadButton :selected-image="selectedImage"></NavBarDownloadButton>
      <NavBarUploadButton @updated="$emit('uploaded')"></NavBarUploadButton>
      <NavBarDeleteButton @updated="$emit('deleted')" :selected-image="selectedImage"></NavBarDeleteButton>
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

#title{
  margin-left: 10px;
}

#items{
  display: flex;
  margin-left: auto;
  margin-right: 10px;
}
</style>