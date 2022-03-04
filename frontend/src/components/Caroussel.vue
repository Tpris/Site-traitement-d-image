<template>
  <div class="neumorphism container">
    <div id="arrow-left"> - </div>
    <div class="container-images">
      <div class="img" v-for="image in props.images">
        <Image @click="$emit('update:modelValue', image.id)" :id="image.id" />
      </div>
    </div>
    <div id="arrow-right"> + </div>
  </div>
</template>

<script setup lang="ts">
import {ImageType} from "@/image";
import {defineProps} from "vue";
import Image from '@/View/Image.vue';

const props = defineProps<{ images: ImageType[] }>()
defineEmits(['update:modelValue'])
</script>

<style scoped>
.container{
  width: 98vw;
  height: 18vh;
  border-radius: 20px;
  display: flex;
}

.container-images{
  display: flex;
  justify-content: center;
}

#arrow-left{
  margin:auto ;
}

#arrow-right{
  margin:auto;
}

.img{
  margin-left: 50px;
}
</style>