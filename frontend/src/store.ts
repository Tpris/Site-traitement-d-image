import { defineStore } from 'pinia'

export const useImageStore = defineStore('main', {
    state: () => ({
        selectedImage: {
            id: -1,
            source : '',
            name: '',
            type: '',
            size: '',
        },
    }),
    getters: {
        getSelectedImage(state) {
            // autocompletion! ✨
            return state.selectedImage
        },
        getSelectedId(state) {
            return state.selectedImage.id
        },
    },
    actions: {
        setSelectedImage(image){
            this.selectedImage = image
        }
        // any amount of arguments, return a promise or not
       /* addTodo(text) {
            // you can directly mutate the state
            this.todos.push({ text, id: this.nextId++, isFinished: false })
        },*/
    },
})