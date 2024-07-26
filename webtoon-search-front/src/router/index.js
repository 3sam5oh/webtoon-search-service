import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/Home.vue'
import CallbackView from '../views/CallbackView.vue'

const routes = [
  {
    path: '/',
    name: '/',
    component: HomeView
  },
  {
    path: '/callback',
    name: '/callback',
    component: CallbackView
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
