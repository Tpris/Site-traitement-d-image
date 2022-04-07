<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
import {onMounted, reactive, UnwrapRef} from "vue";
import {api} from "../http-api";
import {ImageType} from "@/types/ImageType";
import ToolBoxButton from "@/components/buttons/ToolBoxButton.vue";

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
  <div>
      <h2 class="" for="search"> Chercher une image:</h2>
      <div class=""> 
        <form>
           <input type="search" placeholder="Entrez le nom de l'image" class="items"/>
            <input type="checkbox" name="png" value="" class="items"/><label for="PNG">PNG</label>
            <input type="checkbox" name="jpg" value="" class="items"/><label for="JPG">JPG</label>
            <input type="submit" name="search" value="Chercher" class="button neumorphism neumorphism-push items"/>
        </form>
      </div>
  </div>
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

.button{
  font-family: Helvetica, Arial,  sans-serif;
  font-size: 16px;
  border-radius: 50px;
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

.container-items{
  display: flex;
}

.items{
  margin: 10px;
}

@media (max-width: 825px){
  .gallery-panel img {
    width: 100%;
    height: 60vw;
    object-fit: cover;
    border-radius: 0.75rem;
  }
}

@media (min-width: 825px) and (max-width: 1160px){
  .gallery-panel img {
    width: 100%;
    height: 40vw;
    object-fit: cover;
    border-radius: 0.75rem;
  }
}

@media (min-width: 1910px){

  .gallery {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(30rem, 1fr));
    grid-gap: 1rem;
    max-width: 100rem;
    margin: 5rem auto;
    padding: 0 5rem;
  }
}

</style>