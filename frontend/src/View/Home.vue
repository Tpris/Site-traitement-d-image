<script setup lang="ts">
import {reactive} from 'vue';
import { ImageType } from '@/image'
import ToolBox from '@/components/ToolBox.vue'
import Carrousel from '@/components/HomeCaroussel.vue'
import Image from "@/components/ImageGetter.vue"
import NavBar from '@/components/NavBar.vue'
import {Effect} from "@/composables/Effects";
import ImageMeta from "@/components/ImageMeta.vue";
import { useImageStore } from '@/store.ts'
import {storeToRefs} from "pinia";
const store = useImageStore()
let { selectedImage } = storeToRefs(store)

const state = reactive({
  effects: [] as Effect[],
  imageList: Array<ImageType>(),
  uploaded: false,
  deleted: false,
})

const performFilter = (effects: Effect[]) => state.effects = effects
const handleDeleted = () => {
  selectedImage = {id: -1, source : '', name: '', type:'', size:''}
  state.deleted = true
}

</script>

<template>
  <nav-bar @uploaded="state.uploaded = true" @deleted="handleDeleted"></nav-bar>
  <div id="main-content">
    <div id="toolBox">
      <tool-box @applyFilter="(effects) => performFilter(effects)"></tool-box>
    </div>
    <div id="img-box-selected">
      <div class="img-box" :key="selectedImage.id">
        <Image v-if="selectedImage.id !== -1" :id="selectedImage.id" :effects="state.effects"></Image>
      </div>
    </div>
    <image-meta :selectedImage="selectedImage"></image-meta>
  </div>
  <div id="carrousel-box">
    <carrousel :images="state.imageList"
               :uploaded="state.uploaded"
               :deleted="state.deleted"
                @uploaded="state.uploaded = false"
                @deleted="state.deleted = false"
    >
    </carrousel>
  </div>
</template>

<style scoped>
#img-box-selected{
  margin: auto;
  height: 70vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.img-box img{
  max-height: 70vh;
  animation: appear-opacity 650ms ease-in-out;
}

.img-box{
  animation: appear-opacity 500ms ease-in-out;
}

@keyframes appear-opacity {
  From {
    opacity: 0;
  }
  To {
    opacity: 100%;
  }
}


#main-content{
  display: flex;
  width: 100vw;
}

#toolBox{
  margin-top: 1.5vh;
  margin-left: 1vw;
}

#carrousel-box{
  margin-top: 1.5vh;
  margin-left: auto;
  margin-right: auto;
  width: 98vw;
  height: 15vh;
}
</style>
