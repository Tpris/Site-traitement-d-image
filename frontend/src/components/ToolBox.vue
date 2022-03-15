<script setup lang="ts">
  import ToolBoxButton from "@/components/buttons/ToolBoxButton.vue";
</script>

<template>
  <div id="container-tool-box">
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

    <div id="container-options">
      <ul id="options" class="neumorphism">
        <li>
          a
        </li>
        <li>
          a
        </li>
        <li>
          a
        </li>
        <li>
          a
        </li>
      </ul>
      <a id="arrow-left" class="neumorphism neumorphism-push">&lt;</a>
    </div>
  </div>


</template>

<style scoped>
.item{
  margin-top: 20px;
}

#container-tool-box{
  display: flex;
}

#tool-box{
  border-radius: 20px;
  width: 80px;
  height: 71vh;
  display: flex;
  align-items: center;
  flex-direction: column;
  overflow-y: scroll ;
  z-index: 2;
}

#tool-box::-webkit-scrollbar {
  display: none;
}

#options{
  display: flex;
  flex-direction: column;
  width: 20vw;
  height: 71vh;
  border-radius: 20px;
  position: relative;
  z-index: 0;
}

#container-options{
  display: flex;
  align-items: center;
}

#arrow-left{
  font-size: 1.5em;
  margin-right: 2vw;
  position: relative;
  right: 3vw;
  margin-top: auto;
  margin-bottom: auto;
  border-radius: 15px;
  width: 45px;
  height: 45px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 1;
}
</style>