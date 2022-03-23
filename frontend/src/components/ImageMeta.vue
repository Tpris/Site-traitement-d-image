<script setup lang="ts">
  defineProps<{selectedImage: { id:number, source:string, name:string, type:string, size:string }}>()
</script>

<template>
  <ul id="meta-img" v-if="selectedImage.id !== -1">
    <li class="meta" :key="selectedImage.id"><span class="meta-name">Nom: </span> {{ selectedImage.name }}</li>
    <li class="meta" :key="selectedImage.id"><span class="meta-name">Type: </span> {{ selectedImage.type }}</li>
    <li class="meta" :key="selectedImage.id"><span class="meta-name">Taille: </span>{{ selectedImage.size }}</li>
  </ul>
</template>

<style scoped>
#meta-img{
  display: flex;
  flex-direction: column;
  text-align: left;
  border-radius: 20px;
  background-color:rgba(44, 62, 80, 0.1);
  color: black;
  height: 2vh;
  width: 2vw;
  position:sticky;
  padding: 2vh 2vw;
  right: 2vw;
  top: 5vw;
  animation: appear-opacity 650ms ease-in-out;
}

.meta{
  margin-bottom: 0.5vh;
  margin-top: 0.5vh;
  animation: appear-opacity 650ms ease-in-out;
}

.meta-name{
  font-weight: bold;
}

@keyframes appear-opacity {
  From {
    opacity: 0;
  }
  To {
    opacity: 100%;
  }
}
</style>
