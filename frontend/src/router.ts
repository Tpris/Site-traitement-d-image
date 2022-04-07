import { createWebHistory, createRouter } from "vue-router";
import { RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "home",
    component: () => import("./View/Home.vue"),
    props: true
  },
  {
    path: "/image/:id",
    name: "image",
    component: () => import("./components/ImageGetter.vue"),
    props: ({ params }) => ({ id: Number(params.id) || 0 })
  },
  {
    path: "/login",
    name: "login",
    component: () => import("./components/Login.vue"),
    props: true
  },
  {
    path: "/register",
    name: "register",
    component: () => import("./components/Register.vue"),
    props: true
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;