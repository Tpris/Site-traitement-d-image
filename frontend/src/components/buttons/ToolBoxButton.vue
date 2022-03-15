<template>
  <a id="button" class="neumorphism neumorphism-push">
    <slot></slot>
  </a>
</template>

<style scoped>
.selected {
  color: #0777D9;
}

.unselected {
  color: black;
}

#button{
  border-radius: 15px;
  width: 25px;
  height: 25px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
</style>