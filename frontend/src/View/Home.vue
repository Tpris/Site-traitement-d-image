<script setup lang="ts">
import {reactive} from 'vue';
import { ImageType } from '@/image'
import ToolBox from '@/components/ToolBox.vue'
import Carrousel from '@/components/HomeCaroussel.vue'
import Image from "@/components/ImageGetter.vue"
import NavBar from '@/components/NavBar.vue'
import {Effect} from "@/composables/Effects";

const state = reactive({
  selectedImage: {
    id: -1,
    source : '',
    name: '',
  },
  effects: [] as Effect[],
  imageList: Array<ImageType>(),
  uploaded: false,
  deleted: false,
})

const performFilter = (effects: Effect[]) => state.effects = effects
const handleDeleted = () => {
  state.selectedImage = {id: -1, source : '', name: ''}
  state.deleted = true
}

</script>

<template>
  <nav-bar @uploaded="state.uploaded = true" @deleted="handleDeleted" :selectedImage="state.selectedImage"></nav-bar>
  <div id="main-content">
    <div id="toolBox">
      <tool-box :selected-image="state.selectedImage.id" :id="state.selectedImage.id" @applyFilter="(effects) => performFilter(effects)"></tool-box>
    </div>
    <div id="img-box-selected">
      <div class="img-box" :key="state.selectedImage.id">
        <Image v-if="state.selectedImage.id !== -1" v-model="state.selectedImage.source" :id="state.selectedImage.id" :effects="state.effects"></Image>
      </div>
    </div>
  </div>
  <div id="carrousel-box">
    <carrousel v-model="state.selectedImage"
               :id="state.selectedImage.id"
               :images="state.imageList"
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
