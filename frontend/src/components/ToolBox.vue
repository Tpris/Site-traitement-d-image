<script setup lang="ts">
  import ToolBoxButton from "@/components/buttons/ToolBoxButton.vue";
  import {reactive, ref, UnwrapRef} from "vue";
  import {api} from "@/http-api";

  const props = defineProps<{ selectedImage: number }>()

  interface IDropBox{
    text: string
    name: string
    param:string[]
  }

  interface ICursors{
    text: string
    name: string
    param: string[]
  }

  interface Params{
    dropBoxes:IDropBox[]
    cursors:ICursors[]
  }

  class Params{
    private dropBoxes: IDropBox[]
    private cursors: ICursors[]

    constructor(dropBoxes: IDropBox[] | null, cursors: ICursors[] | null) {
      if(dropBoxes === null) this.dropBoxes = Array<IDropBox>()
      else this.dropBoxes = dropBoxes

      if(cursors === null) this.cursors = Array<ICursors>()
      else this.cursors = cursors
    }
  }
  function DropBox(text:string, name:string, param: string[]){
    this.text = text;
    this.name = name;
    this.param = param;
  }

  function Cursors(text:string, name:string, param: string[]){
    this.text = text;
    this.name = name;
    this.param = param;
  }


  class Effect {
    get type(): string {
      return this._type;
    }
    get params(): Params {
      return this._params;
    }
    get text(): string {
      return this._text;
    }

    private readonly _text: string
    private readonly _type: string
    private readonly _params : Params

    constructor(type:string) {
      this._type = type

      switch(type){
        case "filter":
            this._text = "Filtre de teinte"
            this._params = new Params(null, [
              new Cursors("Teinte", "hue", ["0", "255"]),
              new Cursors("min", "smin", ["0", "255"]),
              new Cursors("max", "smax", ["0", "255"])
            ] as ICursors[])
          break;

        case "gaussianBlur":
            this._text = "Filtre gaussien"
            this._params = new Params(
                [new DropBox("Type", "BT",["Skip", "Normalized", "Extended", "Reflect"])] as IDropBox[],
                [ new Cursors("Taille", "size", ["0", "255"]),
                  new Cursors("Ecart type","sigma", ["0", "255"])
                ] as ICursors[])
          break;

        case "meanBlur":
            this._text = "Filtre moyenneur"
            this._params = new Params(
                [new DropBox("Type", "BT",["Skip", "Normalized", "Extended", "Reflect"])] as IDropBox[],
                [new Cursors("Taille", "size", ["0", "255"])] as ICursors[]
            )
          break;

        case "luminosity":
            this._text = "Luminosité"
            this._params = new Params(null, [new Cursors("Delta", "delta",["-255", "255"])] as ICursors[])
          break

        case "sobel":
            this._text = "Sobel"
            this._params = new Params(null, null)
          break;

        case "egalisation":
            this._text = "Egalisation"
            this._params = new Params([new DropBox("HSV", "SV", ["S", "V"])] as IDropBox[], null)
          break;

        default:
            this._text = ""
            this._params = new Params(null, null)
          break;
      }
    }
  }

  const state = reactive({
    slide: false,

    selectedEffect: new Effect(""),

    listEffect: [
        new Effect("sobel"),
        new Effect("luminosity"),
        new Effect("filter"),
        new Effect("gaussianBlur"),
        new Effect("meanBlur"),
        new Effect("egalisation"),
      ]
  })

  let slide = ref(false)
  const getClassSlide = () => slide.value ? 'slide' : ''

  const slider = (e:UnwrapRef<Effect> ) => {
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
  /*  console.log(e.target.value)
    api.getImageEffect(props.selectedImage, name, [e.target.value])
        .catch(() => console.log(e))*/
  }

</script>

<template>
  <div id="container-tool-box">
    <ul id="tool-box" class="neumorphism">
      <li v-for="effect in state.listEffect" :key="effect.type">
        <tool-box-button class="tool-box-item-appear item" @click="slider(effect)">
          {{ effect.type }}
        </tool-box-button>
      </li>
    </ul>

    <div id="container-options">
      <ul id="options" class="neumorphism">
        <li>{{ state.selectedEffect.text }}</li>
        <li v-for="c in state.selectedEffect.params.cursors" :key="c">
          {{ c.text }}
          <input type="range" :min="c.param[0]" :max="c.param[1]" @mouseup="performEffect($event, state.selectedEffect.type, c.name)"/>
        </li>
        <li v-for="dB in state.selectedEffect.params.dropBoxes" :key="dB">
          <select name="pets" @change="performEffect($event, state.selectedEffect.type)">
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