<script setup lang="ts">
import {reactive, ref, watch} from 'vue';
import { api } from '@/http-api';
import { ImageType } from '@/image'
import ToolBox from '@/components/ToolBox.vue'
import Carrousel from '@/components/Caroussel.vue'
import Image from "@/components/Image.vue"
import NavBar from '@/components/NavBar.vue'

const state = reactive({
  selectedImage: {
    id: -1,
    source : '',
  },
  imageList: Array<ImageType>(),
})

const getImageList = async () => {
  return api.getImageList().then((data) => {
    state.imageList = data;
  }).catch(e => {
    console.log(e.message);
  });
}

getImageList();

const download = document.getElementById("download-image");

//Peut être factoriser dans un composable
watch(() => state.selectedImage.id, (newId => {
          if(newId !== -1){
            api.getImage(newId)
                .then((data: Blob) => {
                  const reader = new window.FileReader();
                  reader.readAsDataURL(data);
                  reader.onload = () => {
                    if (reader.result as string)
                      state.selectedImage = {id: newId, source: reader.result as string }
                  };
                })
                .catch(e => {
                  console.log(e.message);
                });
          }
    })
)

const updateImageListUpload = async () => {
  await getImageList();
  const imagesData = state.imageList
  state.selectedImage.id = imagesData.length != 0 ? imagesData[imagesData.length - 1].id : -1
}
</script>

<template>
  <nav-bar @updated="updateImageListUpload" name="Home" :selectedImage="state.selectedImage"></nav-bar>
  <div id="main-content">
    <div id="toolBox">
      <tool-box></tool-box>
    </div>
    <div id="img-box-selected">
      <div class="img-box">
        <Image v-if="state.selectedImage.id !== -1" :id="state.selectedImage.id"></Image>
      </div>
    </div>
  </div>
  <div id="carrousel-box">
    <carrousel v-model="state.selectedImage.id" :images="state.imageList"></carrousel>
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
