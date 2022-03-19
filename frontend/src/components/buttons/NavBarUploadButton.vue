<script setup lang="ts">
import { ref } from 'vue';
import { api } from '@/http-api';

const target = ref<HTMLInputElement>();
const emit = defineEmits(['updated'])

const submitFile = ()  =>{
  if (target.value !== null && target.value !== undefined && target.value.files !== null) {
    const file = target.value.files[0];
    if (file === undefined)
      return;
    let formData = new FormData();
    formData.append("file", file);
    api.createImage(formData).then(() => {
      if (target.value !== undefined)
        target.valu.value = '';
      emit('updated')
    }).catch(e => {
      console.log(e.message);
    });
  }
}

const handleFileUpload = (event: Event) => {
  target.value = (event.target as HTMLInputElement)
  submitFile()
}
</script>

<template>
    <div>
      <label id="button" class="neumorphism neumorphism-push" for="file">
        Upload
      </label>
      <div id="input-upload">
        <input type="file" id="file" ref="file" @change="handleFileUpload" />
      </div>
    </div>
</template>

<style scoped>
#input-upload{
  display: none;
}

#button{
  margin-right: 50px;
  border-radius: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 35px;
  cursor: pointer;
}

#button:hover{
  color: #0777D9;
}
</style>