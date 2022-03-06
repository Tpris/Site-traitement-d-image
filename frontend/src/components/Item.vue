<template>
  <div class="neumorphism neumorphism-push" id="item"><slot></slot></div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.selected {
  color: #0777D9;
}

.unselected {
  color: black;
}

#item{
  padding: -50px;
  border-radius: 15px;
  margin-right: 5px;
  margin-left: 5px;
  width: 55px;
  height: 55px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
</style>