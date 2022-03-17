<script setup lang="ts">
  import ToolBoxButton from "@/components/buttons/ToolBoxButton.vue";
  import {reactive, ref, UnwrapRef} from "vue";
  import {api} from "@/http-api";

  const props = defineProps<{ selectedImage: number }>()

  interface IDropBox{
    name: string
    param:string[]
  }

  interface ICursors{
    name: string
    param: string[]
  }

  interface Params{
    dropBoxes:IDropBox[]
    cursors:ICursors[]
  }

  interface IEffect{
    name: string
    param: Params
  }

  function DropBox(name:string, param: string[]){
    this.name = name;
    this.param = param;
  }

  function Cursors(name:string, param: string[]){
    this.name = name;
    this.param = param;
  }

 function Effect(name:string, param: Params){
    this.name = name;
    this.param = param;
  }

  const state = reactive({
    slide: false,

    selectedEffect: new Effect("", {
        dropBoxes: [] as IDropBox[],
        cursors:[] as ICursors[]
      } as Params) as IEffect,

    listEffect: [
        new Effect("outline", {
          dropBoxes: [] as IDropBox[],
          cursors: [] as ICursors[]
        } as Params),

        new Effect("egalisation", {
          dropBoxes: [new DropBox("HSV", ["S", "V"])],
          cursors: [] as ICursors[]
        } as Params),

        new Effect("luminosity", {
          dropBoxes: [] as IDropBox[],
          cursors: [new Cursors("Delta", ["-255", "255"])],
        } as Params),

        new Effect("Tean filter", {
          dropBoxes: [] as IDropBox[],
          cursors: [
              new Cursors("name", ["0", "255"]),
              new Cursors("min", ["0", "255"]),
              new Cursors("max", ["0", "255"])
              ]
        } as Params),

        new Effect("Gaussian Blur", {
          dropBoxes:[new DropBox("Type", ["Skip", "Normalized", "Extended", "Reflect"])],
          cursors: [
            new Cursors("Taille", ["0", "255"]),
            new Cursors("Ecart type", ["0", "255"]),
          ]
        } as Params),

        new Effect("Mean Filter", {
          dropBoxes:[new DropBox("Type", ["Skip", "Normalized", "Extended", "Reflect"])],
          cursors: [
            new Cursors("Taille", ["0", "255"]),
          ]
        } as Params),
      ]
  })

  let slide = ref(false)
  const getClassSlide = () => slide.value ? 'slide' : ''

  const slider = (e:UnwrapRef<IEffect> ) => {
    state.selectedEffect = e
    let slideItem =  document.getElementById('container-options')
    if(!slide.value){
      slideItem.style.width = '30vw'
      slide.value = true
    }else{
      slideItem.style.width = '0'
      slide.value = false
    }
  }

  const performEffectCursor = (e: any, name: string, nameField: string) => {
    console.log(e.target.value)
    /*api.getImageEffect(props.selectedImage, name, [e.target.value])
        .catch(() => console.log(e))*/
  }

</script>

<template>
  {{state.selectedEffect.param.cursors}}
  <div id="container-tool-box">
    <ul id="tool-box" class="neumorphism">
      <li v-for="effect in state.listEffect" :key="effect.name">
        <tool-box-button class="tool-box-item-appear item" @click="slider(effect)">
          {{ effect.name }}
        </tool-box-button>
      </li>
    </ul>

    <div id="container-options">
      <ul id="options" class="neumorphism">
        <li v-for="c in state.selectedEffect.param.cursors" :key="c">
          {{ c.name }}
          <input type="range" :min="c.param[0]" :max="c.param[1]" @mouseup="performEffect($event, state.selectedEffect.name, c.name)"/>
        </li>
        <li v-for="dB in state.selectedEffect.param.dropBoxes" :key="dB">
          <select name="pets" id="pet-select" @change="performEffect($event, state.selectedEffect.name)">
            <option value="">--Please choose an option--</option>
            <option  v-for="v in dB.param" :value="v">{{ v }}</option>
          </select>
        </li>
      </ul>
      <a id="arrow-left" class="neumorphism neumorphism-push" @click="slider()" >&lt;</a>
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
  position: relative;
  width: 0;
  transition: 550ms width ease-in-out;
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