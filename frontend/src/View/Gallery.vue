<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
import {onMounted, reactive, UnwrapRef} from "vue";
import {api} from "../http-api";
import {ImageType} from "@/types/ImageType";

const state = reactive({
  images: Array<ImageType>()
})

const getImages = async () => {
  return api.getImageList().then((data) => {
    let dataArray = data as unknown as [{}]
    dataArray.shift()
    return data as unknown as Array<UnwrapRef<ImageType>>;
  }).catch(e => {
    console.log(e.message)
    return state.images
  })
}

onMounted(async () => {
  state.images = await getImages()
  console.log(state.images)
  console.log(state.images.length)
})

</script>

<template>
  <nav-bar></nav-bar>
  <div id="main-content">
   <!--  <div v-for="image in state.images" class="text-center" :key="image.id">
      <Image class="img neumorphism neumorphism-push appear"
             @click="imageClick(image)" :id="image.id"
      />
    </div> -->
  </div>
</template>

<style scoped>

</style>