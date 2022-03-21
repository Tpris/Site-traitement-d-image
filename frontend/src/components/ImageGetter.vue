<script setup lang="ts">
import {defineProps, onMounted, reactive, ref, toRefs, UnwrapRef, watch, watchEffect} from 'vue';
import { api } from '@/http-api';
import {Effect} from "@/composables/Effects";

const props = defineProps<{
  id: number,
  effects?:  UnwrapRef<Effect[]>
}>()
const emits = defineEmits(['update:modelValue'])
let source = ref("")

const updateSource = (data: Blob) => {
  const reader = new FileReader()
  reader.readAsDataURL(data)
  reader.onload = () => {
    let result: string = reader.result as string
    if (result) {
      source.value = result
      emits('update:modelValue', result)
    }
  };
}
const getImage = (id: number) => {
  api.getImage(id)
      .then((data) => updateSource(data as unknown as Blob))
      .catch(e => console.log(e.message))
}

const getImageEffect = (id: number, effects: UnwrapRef<Effect[]> | undefined) =>{
  if(!effects || effects?.length===0) return getImage(id)
  api.getImageEffect(id, effects)
      .then((data) => updateSource(data as unknown as Blob))
      .catch(e => console.log(e.message))
}

onMounted(() => getImage(props.id))

watch(() => props.id , (newId) => getImage(newId))
watchEffect(() => getImageEffect(props.id, props.effects));
</script>

<template>
  <img :src="source" :alt="props.id + ''">
</template>

<style scoped>
</style>