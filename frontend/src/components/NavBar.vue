<script setup lang="ts">
import NavBarUploadButton from "@/components/buttons/NavBarUploadButton.vue"
import NavBarDeleteButton from "@/components/buttons/NavBarDeleteButton.vue";
import NavBarDownloadButton from "@/components/buttons/NavBarDownloadButton.vue"
import NavBarRouterButton from "@/components/buttons/NavBarRouterButton.vue";
</script>

<template>
  <nav class="neumorphism" id="nav-bar">
    <h1 id="title">Image in dragon</h1>
    <div id="items">
      <NavBarRouterButton>Accueil</NavBarRouterButton>
      <NavBarDownloadButton></NavBarDownloadButton>
      <NavBarUploadButton></NavBarUploadButton>
      <NavBarDeleteButton></NavBarDeleteButton>
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