<script setup lang="ts">
import {defineProps, onMounted, reactive, ref, toRefs, watch, watchEffect} from 'vue';
import { api } from '@/http-api';
import {IEffect} from "@/composables/Effects";

const props = defineProps<{
  id: number,
  effects?:  IEffect[]
}>()

let source = ref("")

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

const getImageEffect = (id, effects: IEffect[] | undefined) =>{
  console.log(effects)
  if(!effects) return
  api.getImageEffect(id, effects)
      .then((data: Blob) => {
        const reader = new window.FileReader();
        reader.readAsDataURL(data);
        reader.onload = () => {
          if (reader.result as string)
            source.value = reader.result as string
        };
      })
      .catch(e => {
        console.log(e.message);
      });
}

onMounted(() => getImage(props.id))

watch(() => props.id , (newId) => getImage(newId))

watchEffect(() => {
  getImageEffect(props.id, props.effects)
});


</script>

<template>
  <img :src="source" :alt="props.id">
</template>

<style scoped>
</style>