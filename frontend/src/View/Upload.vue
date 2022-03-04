<script setup lang="ts">
import { ref } from 'vue';
import { api } from '@/http-api';

const target = ref<HTMLInputElement>();
const emit = defineEmits(['updated'])

async function submitFile() {
  if (target.value !== null && target.value !== undefined && target.value.files !== null) {
    const file = target.value.files[0];
    if (file === undefined)
      return;
    let formData = new FormData();
    formData.append("file", file);
    api.createImage(formData).then(() => {
      if (target.value !== undefined)
        target.value.value = '';
    }).catch(e => {
      console.log(e.message);
    });
  }
}

async function handleFileUpload(event: Event) {
  target.value = (event.target as HTMLInputElement);
  await submitFile().then(() => {
    emit("updated");
  })
}
</script>

<template>
  <label for="file">
    <a>Upload</a>
  </label>
  <div id="input-upload">
      <input type="file" id="file" ref="file" @change="handleFileUpload" />
  </div>
</template>

<style scoped>
#input-upload{
  display: none;
}

label{
  height: 100%;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
</style>
