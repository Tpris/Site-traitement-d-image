<template>
  <router-link class="link neumorphism neumorphism-push" to="/">
    <slot />
  </router-link>
</template>

<style scoped>
.link{
  margin-right: 50px;
  border-radius: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 35px;
  cursor: pointer;
  text-decoration: none;
  color: #2c3e50;
}

.link:hover{
  color:#0777D9;
}
</style>