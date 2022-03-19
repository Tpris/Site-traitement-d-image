<script setup lang="ts">
import {ImageType} from "@/image";
import {defineProps, onMounted, reactive, ref, UnwrapRef, watch} from "vue";
import Image from '@/components/ImageGetter.vue';
import {api} from "@/http-api";
import {stat} from "fs";

const props = defineProps<{ images: ImageType[], id: number, updated: boolean}>()
const emit  = defineEmits(['update:modelValue', 'updated', 'nameChanged'])

const state = reactive({
  index: 0,
  nbImg: 5,
  currentImages: Array<ImageType>()
})

const getCurrentImages = () => {
  api.getImageListByNumber(state.index, state.nbImg).then((data) => {
    state.currentImages = data as Array<UnwrapRef<ImageType>>;
  }).catch(e =>
    console.log(e.message));
}

onMounted(() => {
  getCurrentImages()
  /* for (let i = 0; i < state.nbImg && props.images[i]; i++)
    state.currentImages[i] = props.images[i * state.index + i]*/
})

const resetCurrentImages = (newImages) => {
  getCurrentImages()
  state.currentImages = Array()
  for (let i = 0; i < state.nbImg && newImages[(state.nbImg * state.index) + i]; i++)
    state.currentImages[i] = newImages[(state.nbImg * state.index) + i]
}

const getPreviousImages = () =>{
  if(state.index !== 0)
    state.index--
}

const getNextImages = () =>{
  if(((state.index + 1) * state.nbImg)  + state.nbImg < props.images.length + state.nbImg)
    state.index++;
}

const imageClick = (image: ImageType) => emit('update:modelValue', image)

watch(() => props.id, (newId => {
      if(props.updated){
        let newIndex = props.images.findIndex((img) => img.id == newId)
        if(props.images.length - 1 < (state.index) * state.nbImg) {
          state.index--
          emit('updated', false)
          return
        }
        while (newIndex >= (state.index + 1) * state.nbImg)
          state.index++
        emit('updated', false)
      }
    })
)

//watch(() => props.images, (newImages => resetCurrentImages(newImages)))
watch(() => state.index, () => getCurrentImages())

//watch(() => props.images, (newImages => resetCurrentImages(newImages)))
//watch(() => state.index, () => resetCurrentImages(props.images))
</script>

<template>
  <div class="neumorphism container">
    <a id="arrow-left" class="arrow neumorphism neumorphism-push" @click="getPreviousImages">&lt;</a>
    <div class="container-images">
      <div class="img" v-for="image in state.currentImages" :key="image.id">
          <Image class="neumorphism appear" :class="props.id === image.id ? 'selected-image' : 'neumorphism-push'" @click="imageClick(image)" :id="image.id" />
      </div>
    </div>
      <a id="arrow-right" class="arrow neumorphism neumorphism-push" @click="getNextImages">&gt;</a>
  </div>
</template>

<style scoped>
.container{
  width: 100%;
  height: 100%;
  border-radius: 20px;
  display: flex;
  margin: auto;
  animation: appear 700ms ease-in-out;
}

.selected-image{
  box-shadow:
      inset 7px 7px 15px rgba(55, 84, 170,.15),
      inset -7px -7px 20px rgba(255, 255, 255,1),
      0 0 4px rgba(255, 255, 255,.2);
  opacity: 50%;
}

.img img{
  max-height: 90%;
  border-radius: 10px;
}

.arrow{
  margin-top: auto;
  margin-bottom: auto;
  border-radius: 15px;
  width: 60px;
  height: 60px;
  font-size: 2em;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.arrow div{
  animation: appear 700ms ease-in-out;
}

#arrow-left{
  margin-left: 2vw;
}

#arrow-right{
  margin-right: 2vw;
}

.container-images{
  display: flex;
  justify-content: center;
  width: 90vw;
}

@keyframes appear {
  From {
    opacity: 0;
    box-shadow: unset;
  }
  To {
    opacity: 100%;
    box-shadow:
        inset 0 0 15px rgba(55, 84, 170,0),
        inset 0 0 20px rgba(255, 255, 255,0),
        7px 7px 15px rgba(55, 84, 170,.15),
        -7px -7px 20px rgba(255, 255, 255,1),
        inset 0 0 4px rgba(255, 255, 255,.2);
  }
}

.img .appear{
  animation: appear 650ms ease-in-out;
}
.img{
  margin-left: 2vw;
  margin-right: 2vw;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: opacity 199ms ease-in-out;
}

.img .neumorphism-push:hover{
  transition: opacity 199ms ease-in-out;
  opacity: 70%;
}
</style>