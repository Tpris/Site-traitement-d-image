<script setup lang="ts">
import {ImageType} from "@/image";
import { onMounted, reactive, ref, UnwrapRef, watch} from "vue";
import Image from '@/components/ImageGetter.vue';
import {api} from "@/http-api";
import { Swiper, SwiperSlide } from 'swiper/vue';
import { Controller, Navigation, Pagination } from 'swiper';
import "swiper/css";
import "swiper/css/pagination";
import "swiper/css/navigation";
import { useImageStore } from '@/store.ts'
import {storeToRefs} from "pinia";

const store = useImageStore()
let { selectedImage, uploaded, deleted } = storeToRefs(store)

const state = reactive({
  size: 5,
  nbImages: 0,
  isUpdate: false,
  currentImages: Array<ImageType>()
})

const controlledSwiper = ref(null);

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

onMounted(async () => state.currentImages = await getCurrentImages(0, state.size))

const imageClick = (image: ImageType) => selectedImage.value = image

const handleUploaded = async () => {
    uploaded.value = false
    state.isUpdate = true
    selectedImage.value = [...await getCurrentImages(state.nbImages,1)][0]
    state.isUpdate = false
}

const handleSwiper = (swiper:any) => controlledSwiper.value = swiper


const handleDeleted = async () => {
  deleted.value = false
  state.currentImages = await getCurrentImages(0, state.size)
}

const loadNextImages = async () => state.currentImages.push(...await getCurrentImages(state.currentImages.length, state.size))


const handleEdge = async (swip:Swiper) => {
  if(swip.isEnd && state.currentImages.length !== state.nbImages && !state.isUpdate){
    state.isUpdate = true
    await loadNextImages()
    swip.update()
    state.isUpdate = false
  }
}

watch(() => uploaded.value, ((newState) => newState && handleUploaded()))
watch(() => deleted.value, ((newState) => newState && handleDeleted()))
</script>

<template>
  <div class="neumorphism container">
    <div class="container-images" :key="state.nbImages">
      <button id="arrow-left" class="prevArrow arrow neumorphism neumorphism-push">&lt;</button>
      <swiper
          :navigation="{ nextEl: '.nextArrow', prevEl: '.prevArrow' }"
          :slidesPerView="5"
          :speed="300"
          :loop="false"
          :slidesPerGroup="1"
          :pagination="{
            dynamicBullets: true,
          }"
          :modules="[Controller, Pagination, Navigation]"
          class="mySwiper"
          @toEdge="handleEdge"
          @swiper="handleSwiper"
      >
        <swiper-slide v-for="image in state.currentImages" class="text-center" :key="image.id">
          <Image class="img neumorphism neumorphism-push appear"
                 :class="selectedImage.id === image.id ? 'selected-image' : 'neumorphism-push'"
                 @click="imageClick(image)" :id="image.id"
          />
        </swiper-slide>
      </swiper>
      <button id="arrow-right" class="nextArrow arrow neumorphism neumorphism-push">&gt;</button>
    </div>
  </div>
</template>

<style scoped>

.swiper-slide{
  display: flex;
  justify-content: center;
  width: 90vw;
}

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
  border: none;
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
  margin-left: 2vw;
  margin-right: 2vw;
  cursor: pointer;
  transition: opacity 199ms ease-in-out;
}

.img:hover{
  transition: opacity 199ms ease-in-out;
  opacity: 70%;
}
</style>