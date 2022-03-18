<script setup lang="ts">
  import ToolBoxButton from "@/components/buttons/ToolBoxButton.vue";
  import {reactive, ref, UnwrapRef} from "vue";
  import {api} from "@/http-api";

  const props = defineProps<{ selectedImage: number }>()
  const emits = defineEmits(["applyFilter"])
  interface IDropBox{
    text: string
    name: string
    param:string[]
    value: string
  }

  interface ICursors{
    text: string
    name: string
    param: string[]
    value: number
  }

  interface IEffect{
    type: string
    text: string
    params: Params
  }

  interface Params{
    dropBoxes:IDropBox[]
    cursors:ICursors[]
  }

 function Params(dropBoxes: IDropBox[] | null, cursors: ICursors[] | null){
      if(dropBoxes === null) this.dropBoxes = Array<IDropBox>()
      else this.dropBoxes = dropBoxes

      if(cursors === null) this.cursors = Array<ICursors>()
      else this.cursors = cursors
  }

  function DropBox(text:string, name:string, param: string[]){
    this.text = text;
    this.name = name;
    this.param = param;
    this.value = "";
  }

  function Cursors(text:string, name:string, param: string[]){
    this.text = text;
    this.name = name;
    this.param = param;
    this.value = 0;
  }

  function Effect(type:string) {
      this.type = type

      switch(type){
        case "filter":
          this.text = "Filtre de teinte"
          this.params = new Params(null, [
            new Cursors("Teinte", "hue", ["0", "255"]),
            new Cursors("min", "smin", ["0", "255"]),
            new Cursors("max", "smax", ["0", "255"])
          ] as ICursors[])
          break;

        case "gaussianBlur":
          this.text = "Filtre gaussien"
          this.params = new Params(
              [new DropBox("Type", "BT",["Skip", "Normalized", "Extended", "Reflect"])] as IDropBox[],
              [ new Cursors("Taille", "size", ["0", "255"]),
                new Cursors("Ecart type","sigma", ["0", "255"])
              ] as ICursors[])
          break;

        case "meanBlur":
          this.text = "Filtre moyenneur"
          this.params = new Params(
              [new DropBox("Type", "BT",["Skip", "Normalized", "Extended", "Reflect"])] as IDropBox[],
              [new Cursors("Taille", "size", ["0", "255"])] as ICursors[]
          )
          break;

        case "luminosity":
          this.text = "Luminosité"
          this.params = new Params(null, [new Cursors("Delta", "delta",["-255", "255"])] as ICursors[])
          break

        case "sobel":
          this.text = "Sobel"
          this.params = new Params(null, null)
          break;

        case "egalisation":
          this.text = "Egalisation"
          this.params = new Params([new DropBox("HSV", "SV", ["S", "V"])] as IDropBox[], null)
          break;

        default:
          this.text = ""
          this.params = new Params(null, null)
          break;
      }
  }

  const state = reactive({
    slide: false,

    selectedEffect: new Effect("") as IEffect,

    listEffect: [
        new Effect("sobel"),
        new Effect("luminosity"),
        new Effect("filter"),
        new Effect("gaussianBlur"),
        new Effect("meanBlur"),
        new Effect("egalisation"),
      ] as IEffect[]
  })

  let slide = ref(false)
  const getClassSlide = () => slide.value ? 'slide' : ''

  const slider = (e:UnwrapRef<IEffect> | null ) => {
    if(e != null) state.selectedEffect = e
    let slideItem =  document.getElementById('container-options')
    if(!slide.value){
      slideItem.style.width = '30vw'
      slide.value = true
    }else{
      slideItem.style.width = '0'
      slide.value = false
    }
  }

  const performEffect = (type: string, e: any | null, effect : UnwrapRef<IEffect> | null) => {
    if(effect !== null && state.selectedEffect.type !== effect?.type)
      state.selectedEffect = effect
    let param = Array<{}>()
    if(type === "egalisation" && e !== null)
      type += e.target.value
    else{
      state.selectedEffect.params.cursors.forEach((p) => param.push({name: p.name, value:p.value}))
      state.selectedEffect.params.dropBoxes.forEach((p) => param.push({name: p.name, value:p.value}))
    }
    emits("applyFilter", type, param)
  }

  const activeButton = (effect: IEffect) => {
    if(effect.type === state.selectedEffect.type){
      return "neumorphism-activate"
    }
    else return ""
  }

</script>

<template>
  <div id="container-tool-box">
    <ul id="tool-box" class="neumorphism">
      <li v-for="effect in state.listEffect" :key="effect.type">
        <tool-box-button
            v-if="effect.params.dropBoxes.length === 0 && effect.params.cursors.length === 0"
            class="tool-box-item-appear item"
            :class="activeButton(effect)"
            @click="performEffect(effect.type, null, effect)"
        >
          {{ effect.type }}
        </tool-box-button>
        <tool-box-button
            v-else
            class="tool-box-item-appear item"
            :class="activeButton(effect)"
            @click="slider(effect)"
        >
          {{ effect.type }}
        </tool-box-button>
      </li>
    </ul>

    <div id="container-options">
      <ul id="options" class="neumorphism">
        <li>{{ state.selectedEffect.text }}</li>
        <li v-for="c in state.selectedEffect.params.cursors" :key="c">
          {{ c.text }}
          <input type="range" v-model="c.value" :min="c.param[0]" :max="c.param[1]" @mouseup="performEffect(state.selectedEffect.type, $event, null)"/>
        </li>
        <li v-for="dB in state.selectedEffect.params.dropBoxes" :key="dB">
          <select name="pets" v-model="dB.value" @change="performEffect(state.selectedEffect.type, $event, null)">
            <option value="">Choisir un {{ dB.text }}</option>
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