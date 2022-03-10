<template>
  <div class="neumorphism container">
    <div class="arrow">
      <item>&lt;</item>
    </div>
    <div class="container-images">
      <div class="img" v-for="image in props.images">
          <Image @click="$emit('update:modelValue', image.id)" :id="image.id" />
      </div>
    </div>
    <div class="arrow">
      <item>&gt;</item>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ImageType} from "@/image";
import {defineProps} from "vue";
import Image from '@/components/Image.vue';
import Item from '@/components/Item.vue'

const props = defineProps<{ images: ImageType[] }>()
defineEmits(['update:modelValue'])
</script>

<style scoped>
.container{
  width: 100%;
  height: 100%;
  border-radius: 20px;
  display: flex;
  margin: auto;
}

.img img{
  max-height: 100%;
}

.arrow{
  margin: auto;
  font-size: 2em;
}

.container-images{
  display: flex;
  justify-content: center;
}

.img{
  margin-left: 2vw;
  margin-right: 2vw;
  cursor: pointer;
}

.img:hover{
  opacity: 70%;
}
</style>