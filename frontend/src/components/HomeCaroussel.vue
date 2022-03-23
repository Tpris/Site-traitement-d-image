<script setup lang="ts">
import {ImageType} from "@/image";
import {defineProps, onMounted, reactive, ref, UnwrapRef, watch} from "vue";
import Image from '@/components/ImageGetter.vue';
import {api} from "@/http-api";

const props = defineProps<{ images: ImageType[], id: number, uploaded: boolean, deleted: boolean}>()
const emit  = defineEmits(['update:modelValue', 'uploaded', 'deleted', 'nameChanged'])

const state = reactive({
  index: 0,
  nbImg: 5,
  nbImages: 0,
  currentImages: Array<ImageType>()
})

const mod= (n:number, m:number) => ((n % m) + m) % m

const getCurrentImages = async (index:number, nbImg:number) => {
  return api.getImageListByNumber(index, nbImg).then((data) => {
    let dataArray = data as unknown as [{}]
    state.nbImages = (dataArray[0] as unknown as { nbImages:number }).nbImages as number
    dataArray.shift()
    return data as unknown as Array<UnwrapRef<ImageType>>;
  }).catch(e => {
    console.log(e.message)
    return state.currentImages
  })
}

onMounted(async () => {
  state.currentImages = await getCurrentImages(state.index, state.nbImg)
  state.index = state.nbImg
})

const getPreviousImages = async () => {
  if(state.currentImages.length === 0) return
  state.index--
  state.currentImages.pop()
  state.currentImages.unshift(...await getCurrentImages( mod(state.index - state.nbImg, state.nbImages), 1))
}

const getNextImages = async () => {
  if(state.currentImages.length === 0) return
  state.index++
  state.index = mod(state.index, state.nbImages)
  state.currentImages.shift()
  state.currentImages.push(...await getCurrentImages(state.index, 1))
}

const imageClick = (image: ImageType) => emit('update:modelValue', image)

const handleUploaded = async () => {
    emit('uploaded', false)
    state.currentImages.length = 0
    state.currentImages = await getCurrentImages(state.nbImages+1 - state.nbImg, state.nbImg)
    state.index = state.nbImages - 1
    emit('update:modelValue', state.currentImages[state.currentImages.length - 1])
}

const handleDeleted = async () => {
  emit('deleted', false)
  state.currentImages.length = 0
  state.index = state.nbImg
  state.currentImages = await getCurrentImages(mod (state.nbImg - state.index,  state.nbImages), state.nbImg)
}

watch(() => props.uploaded, ((newState) => newState && handleUploaded()))
watch(() => props.deleted, ((newState) => newState && handleDeleted()))
</script>

<template>
  <div class="neumorphism container">
    <a id="arrow-left" class="arrow neumorphism neumorphism-push" @click="getPreviousImages">&lt;</a>
    <div class="container-images" :key="state.nbImages">
      <Image v-for="image in state.currentImages"
             :key="image.id"
             class="img neumorphism neumorphism-push appear"
             :class="props.id === image.id ? 'selected-image' : 'neumorphism-push'"
             @click="imageClick(image)" :id="image.id"
      />
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
  justify-content: center;
}
.container-images{
  animation: appear 700ms ease-in-out;
}

.selected-image{
  box-shadow:
      inset 7px 7px 15px rgba(55, 84, 170,.15),
      inset -7px -7px 20px rgba(255, 255, 255,1),
      0 0 4px rgba(255, 255, 255,.2);
  opacity: 50%;
}

.img{
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
  display: flex;
  align-self: center;
  cursor: pointer;
  margin-left: 2vw;
  margin-right: 2vw;
  transition: opacity 199ms ease-in-out;
}

.img:hover{
  transition: opacity 199ms ease-in-out;
  opacity: 70%;
}
</style>