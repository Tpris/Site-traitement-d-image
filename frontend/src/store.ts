import { defineStore } from 'pinia'

export const useImageStore = defineStore('main', {
    state: () => ({
        selectedImage: {
            id: -1,
            source : '',
            name: '',
            type: '',
            size: '',
            url: '',
        },
        uploaded: false,
        deleted: false,
    }),
})