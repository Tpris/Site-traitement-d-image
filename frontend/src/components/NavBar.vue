<script setup lang="ts">
import NavBarUploadButton from "@/components/buttons/NavBarUploadButton.vue"
import NavBarDeleteButton from "@/components/buttons/NavBarDeleteButton.vue";
import NavBarDownloadButton from "@/components/buttons/NavBarDownloadButton.vue"

defineProps<{ name: "", selectedImage: { id:number, source:string }}>()
defineEmits(['updated'])
</script>

<template>
  <nav class="neumorphism" id="nav-bar">
    <h1 id="title">Notre super projet</h1>
    <div id="items">
      <router-link class="link neumorphism neumorphism-push" :class="name === 'home' ? 'selected' : 'unselected'" to="/">
        Accueil
      </router-link>
      <NavBarDownloadButton :selected-image="selectedImage"></NavBarDownloadButton>
      <NavBarUploadButton @updated="$emit('updated')"></NavBarUploadButton>
      <NavBarDeleteButton @updated="$emit('updated')" :selected-image="selectedImage"></NavBarDeleteButton>
    </div>
  </nav>
</template>

<style scoped>
.selected {
  color: #0777D9;
}

.unselected {
  color: black;
}

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

.link{
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

.link:hover{
  color:#0777D9;
}

.appear{
  animation: appear 650ms ease-in-out;
}

@keyframes appear {
  From {
    box-shadow: unset;
  }
  To {
    box-shadow:
        inset 0 0 15px rgba(55, 84, 170,0),
        inset 0 0 20px rgba(255, 255, 255,0),
        7px 7px 15px rgba(55, 84, 170,.15),
        -7px -7px 20px rgba(255, 255, 255,1),
        inset 0 0 4px rgba(255, 255, 255,.2);
  }
}

</style>