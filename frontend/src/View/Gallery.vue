<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
import {onMounted, reactive, UnwrapRef} from "vue";
import {api} from "../http-api";
import {ImageType} from "@/types/ImageType";

const state = reactive({
  images: Array<ImageType>(),
  nbImages: 0,
  limit: 0,
})

const getImages = async () => {
  return api.getImageList().then((data) => {
    let dataArray = data as unknown as [{}]
    state.nbImages = (dataArray[0] as unknown as { nbImages:number }).nbImages as number
    dataArray.shift()
    return data as unknown as Array<UnwrapRef<ImageType>>;
  }).catch(e => {
    console.log(e.message)
    return state.images
  })
}

onMounted(async () => {
  state.images = await getImages()
})

</script>

<template>
  <nav-bar></nav-bar>
  <div class="gallery">

    <div v-for="image in state.images" :key="image['id']" :id="image['id']" class="gallery-panel">
      <img  :src="'/images/' + image['id']" :alt="image['name']" />
    </div>

  </div>
</template>

<style scoped>
.gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(20rem, 1fr));
  grid-gap: 1rem;
  max-width: 80rem;
  margin: 5rem auto;
  padding: 0 5rem;
}

.gallery-panel img {
  width: 100%;
  height: 22vw;
  object-fit: cover;
  border-radius: 0.75rem;
}
</style>