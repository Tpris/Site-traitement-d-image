<script setup lang="ts">
  import ToolBoxButton from "@/components/buttons/ToolBoxButton.vue";
</script>

<template>
  <ul id="tool-box" class="neumorphism">
    <li class="item">
      <tool-box-button class="tool-box-item-appear">1</tool-box-button>
    </li>
    <li class="item">
      <tool-box-button class="tool-box-item-appear">1</tool-box-button>
    </li>
    <li class="item">
      <tool-box-button class="tool-box-item-appear">1</tool-box-button>
    </li>
    <li class="item">
      <tool-box-button class="tool-box-item-appear">1</tool-box-button>
    </li>
    <li class="item">
      <tool-box-button class="tool-box-item-appear">1</tool-box-button>
    </li>
    <li class="item">
      <tool-box-button class="tool-box-item-appear">1</tool-box-button>
    </li>
    <li class="item">
      <tool-box-button class="tool-box-item-appear">1</tool-box-button>
    </li>
    <li class="item">
      <tool-box-button class="tool-box-item-appear">1</tool-box-button>
    </li>
    <li class="item">
      <tool-box-button class="tool-box-item-appear">1</tool-box-button>
    </li>
    <li class="item">
      <tool-box-button class="tool-box-item-appear">1</tool-box-button>
    </li>
  </ul>

  
</template>

<style scoped>
.item{
  margin-top: 20px;
}

#tool-box{
  border-radius: 20px;
  width: 80px;
  height: 71vh;
  display: flex;
  align-items: center;
  flex-direction: column;
  overflow-y: scroll ;
}

#tool-box::-webkit-scrollbar {
  display: none;
}
</style>