<script setup lang="ts">
import {ref, watch} from 'vue';
import { api } from '@/http-api';
import { ImageType } from '@/image'
import ToolBox from '@/components/ToolBox.vue'
import Carrousel from '@/components/Caroussel.vue'
import Image from "@/View/Image.vue"
import NavBar from '@/components/NavBar.vue'

const selectedId = ref(-1);
const imageList = ref<ImageType[]>([]);
getImageList();

async function getImageList() {
  api.getImageList().then((data) => {
    imageList.value = data;
  }).catch(e => {
    console.log(e.message);
  });
}

const download = document.getElementById("download-image");

watch(selectedId, (newId) => {
  api.getImage(newId)
      .then((data: Blob) => {
        const reader = new window.FileReader();
        reader.readAsDataURL(data);
        reader.onload = () => {
          const download = document.getElementById("download-image");
          if (reader.result as string && download) {
            download.setAttribute("href", reader.result as string)
            download.setAttribute("download", imageList.value.find((elem) => elem.id  === newId).name);
          }
        };
      })
      .catch(e => {
        console.log(e.message);
      });
})

const updateImageListUpload = async () => {
  await getImageList().then( () =>{
        const imagesData = imageList.value
        selectedId.value = imagesData[imagesData.length - 1].id
  })
}
</script>

<template>
  <nav-bar @updated="updateImageListUpload" name="Home"></nav-bar>
  <div id="main-content">
    <div id="toolBox">
      <tool-box></tool-box>
    </div>
    <div id="img-box-selected">
        <Image v-if="selectedId !== -1" :id="selectedId"></Image>
    </div>
  </div>
  <div id="carrousel-box">
    <carrousel v-model="selectedId" :images="imageList"></carrousel>
  </div>
</template>

<style scoped>
#img-box-selected{
  margin: auto;
}
#main-content{
  display: flex;
  margin-left: 20px;
}

#toolBox{
  margin-right: 50px;
  margin-top: 1.5vh;
}

#carrousel-box{
  margin-top: 1.5vh;
  display: flex;
  align-items: center;
  flex-direction: column;
}
</style>
