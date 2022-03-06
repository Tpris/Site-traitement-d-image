<script setup lang="ts">
import {defineProps, onMounted, reactive, ref, toRefs, watch} from 'vue';
import { api } from '@/http-api';

const props = defineProps<{ id: number }>()
let source = ref("")

//Peut être factoriser dans un composable
const getImage = (id) => {
  api.getImage(id)
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
}

onMounted(() => getImage(props.id))

watch(() => props.id , (newId) => getImage(newId))

</script>

<template>
  <img :src="source" :alt="props.id">
</template>

<style scoped>
</style>