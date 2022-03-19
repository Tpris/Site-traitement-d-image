<script setup lang="ts">
  import ToolBoxButton from "@/components/buttons/ToolBoxButton.vue";
  import {effect, onMounted, reactive, ref, watch} from "vue";
  import useEffects, {EffectTypes, ICursors, IDropBox, IEffect} from "@/composables/Effects";

  const {Effect} = useEffects()
  const props = defineProps<{ id: number }>()
  const emits = defineEmits(["applyFilter"])

  const state = reactive({
    selectedEffect: new Effect("") as IEffect,
    appliedEffects: Array<IEffect>(),
    listEffect: [
        new Effect(EffectTypes.Sobel),
        new Effect(EffectTypes.Luminosity),
        new Effect(EffectTypes.Filter),
        new Effect(EffectTypes.GaussianBlur),
        new Effect(EffectTypes.MeanBlur),
        new Effect(EffectTypes.EgalisationS),
        new Effect(EffectTypes.EgalisationV),
      ] as IEffect[],
  })

  watch(() => props.id, () =>{
    closeSlider()
    state.appliedEffects.length = 0
    state.selectedEffect = new Effect("") as IEffect
    reloadEffectsImage()
  })

  const hasParam = (e: IEffect) => e.params.dropBoxes.length !== 0 || e.params.cursors.length !== 0

  const openSlider = () => document.getElementById('container-options').style.width = '30vw'
  const closeSlider = () => document.getElementById('container-options').style.width = '0'
  const activeButton = () => "neumorphism-activate"

  const selectEffect = (e: IEffect) => state.selectedEffect = e
  const addEffects = (effect) => state.appliedEffects.push(effect)
  const findEffect = (type:string) => state.listEffect.find((e) => e.type === type)
  const removeEffect = (type: string) => state.appliedEffects = state.appliedEffects.filter((e) => e.type !== type )
  const isAppliedEffect = (type:string) => state.appliedEffects.find((e) => e.type === type)
  const reloadEffectsImage = () => emits("applyFilter", state.appliedEffects)

  const removeEffectAndRefresh = (type: string) =>{
    removeEffect(type)
    reloadEffectsImage()
  }

  const handleEffect = (effect: IEffect) =>{
    if(props.id === -1) return
    openSlider()
    selectEffect(effect)
    if(!isAppliedEffect(effect.type)) performEffect(effect, null, null)
  }

  const handleEffectNoParam = (effect: IEffect) => {
    if(props.id === -1) return
    closeSlider()
    selectEffect(effect)
    if(isAppliedEffect(effect.type)) removeEffect(effect.type)
    else addEffects(effect)
    reloadEffectsImage()
  }

  const performEffect = (effect: IEffect, e: any | null, param: ICursors | IDropBox| null) => {
    if (param) param.value = e.target.value
    removeEffect(effect.type)
    addEffects(effect)
    reloadEffectsImage()
  }

  watch(() => findEffect(EffectTypes.Filter), (newEffect) => {
    let minCursors = newEffect.params.cursors[1]
    let maxCursors =  newEffect.params.cursors[2]
    minCursors.param[1] = maxCursors.value + ""
    if(minCursors.value > maxCursors.value)
      minCursors.value = maxCursors.value
  }, {deep:true})
</script>

<template>

  <div id="container-tool-box">
    <ul id="tool-box" class="neumorphism">
      <li v-for="effect in state.listEffect" :key="effect.type">
        <tool-box-button
            class="tool-box-item-appear item"
            :class="isAppliedEffect(effect.type) && activeButton()"
            @click="hasParam(effect) ? handleEffect(effect): handleEffectNoParam(effect)"
        >
          {{ effect.type }}
        </tool-box-button>
      </li>
    </ul>
    <div id="container-options">
      <ul id="options" class="neumorphism">
        <li v-if="hasParam(state.selectedEffect)">{{ state.selectedEffect.text }}</li>
          <li v-for="(c) in state.selectedEffect.params.cursors" :key="c">
            {{ c.text }}
            <input type="range" :min="c.param[0]" :max="c.param[1]" :value="c.value" :step="c.step" @mouseup="performEffect(state.selectedEffect, $event, c)"/>
            {{ c.value }}
          </li>
        <li v-for="dB in state.selectedEffect.params.dropBoxes" :key="dB">
          <select name="pets" v-model="dB.value" @change="performEffect(state.selectedEffect, $event, dB)">
            <option value="">Choisir un {{ dB.text }}</option>
            <option  v-for="v in dB.param" :value="v">{{ v }}</option>
          </select>
        </li>
        <li v-if="hasParam(state.selectedEffect)">
          <tool-box-button v-if="isAppliedEffect(state.selectedEffect.type)" @click="removeEffectAndRefresh(state.selectedEffect.type)">Enlever le filtre</tool-box-button>
          <tool-box-button v-else @click="performEffect(state.selectedEffect,null, null)">Appliquer le filtre</tool-box-button>
        </li>
      </ul>
      <a id="arrow-left" class="neumorphism neumorphism-push" @click="closeSlider()" >&lt;</a>
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