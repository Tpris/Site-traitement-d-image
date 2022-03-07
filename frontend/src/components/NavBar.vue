<template>
  <nav class="neumorphism">
    <h1 id="title">Notre super projet</h1>
    <div id="items">
      <div class="neumorphism neumorphism-push link-sizing">
        <router-link :class="name === 'Home' ? 'selected' : 'unselected'" to="/">Accueil</router-link>
      </div>
      <div class="neumorphism neumorphism-push link-sizing">
        <a :class="name === 'Portfolio' ? 'selected' : 'unselected'" id="download-image">Download</a>
      </div>
      <div class="neumorphism neumorphism-push link-sizing">
        <upload @updated="$emit('updated')" ></upload>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import Upload from "@/View/Upload.vue"
import {ref, watch} from "vue";
import {api} from "@/http-api";

defineProps<{ name: ""}>()
const emits = defineEmits(['updated'])

const uploaded= ref(false);
watch(uploaded, () => {
  emits("updated");
})
</script>

<style>
.link-sizing a,label{
  border-radius: 50px;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.selected {
  color: #0777D9;
}

.unselected {
  color: black;
}

nav{
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

#items div{
  margin-right: 50px;
  border-radius: 50px;
  width: 100px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
}

#items div:hover a{
  color:#0777D9;
}

#items div a{
  text-decoration: none;
}
</style>