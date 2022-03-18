import axios, { AxiosResponse, AxiosError } from 'axios';
import { ImageType } from '@/image';
import {IEffect, IParams, ICursors, IDropBox} from "@/composables/Effects";

const instance = axios.create({
  baseURL: "/",
  timeout: 15000,
});

const responseBody = (response: AxiosResponse) => response.data;

const requests = {
  get: (url: string, param: {}) => instance.get(url, param).then(responseBody),
  post: (url: string, body: {}) => instance.post(url, body, { headers: { "Content-Type": "multipart/form-data" }, }).then(responseBody),
  put: (url: string, body: {}) => instance.put(url, body).then(responseBody),
  delete: (url: string) => instance.delete(url).then(responseBody)
};

export const api = {
  getImageList: (): Promise<ImageType[]> => requests.get('images', {}),
  getImageListByNumber: (index: number, size: number): Promise<ImageType[]> => requests.get('images', { index: index, size:size }),
  getImage: (id: number): Promise<Blob> => requests.get(`images/${id}`, { responseType: "blob" }),

  getImageEffect: (id: number, effects:IEffect[]): Promise<Blob> => {
    let params = {}
    let algorithm : string = ""

    effects.forEach((e, index) => {
      if (index !== 0) algorithm += '-'
      algorithm += e.type
      for (let paramsKey in e.params) {
        e.params[paramsKey].forEach((p) => {
          if (params[p.name]) params[p.name] += "-" + p.value
          else params[p.name] = p.value
        })
      }
    })

    return requests.get(`images/${id}`, {
      responseType: "blob",
      params : { algorithm, ...params }
    })
  },

  createImage: (form: FormData): Promise<ImageType> => requests.post('images', form),
  deleteImage: (id: number): Promise<void> => requests.delete(`images/${id}`),
};