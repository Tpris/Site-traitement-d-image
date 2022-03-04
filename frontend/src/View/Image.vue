<script setup lang="ts">
import {defineProps, reactive, ref, toRefs, watch} from 'vue';
import { api } from '@/http-api';

const props = defineProps<{ id: number }>()
let source = ref("")

api.getImage(props.id)
    .then((data: Blob) => {
      const reader = new window.FileReader();
      reader.readAsDataURL(data);
      reader.onload = () => {
        if (reader.result as string) {
          source.value = reader.result as string;
        }
      };
    })
    .catch(e => {
      console.log(e.message);
    });

</script>

<template>
  <img :src="source" :alt="id">
</template>

<style scoped>
img{
  height: 100%;
}
</style>