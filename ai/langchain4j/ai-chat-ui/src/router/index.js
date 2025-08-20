import Vue from 'vue'
import VueRouter from 'vue-router'
import ChatWindow from '@/views/ChatWindow.vue'
import Test from '@/views/Test.vue'
import Test2 from '@/views/Test2.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'index',
    component: ChatWindow
  },
  {
    path: '/test',
    name: 'Test',
    component: Test
  },
  {
    path: '/test2',
    name: 'Test2',
    component: Test2
  },
]

const router = new VueRouter({
//   mode: 'history',
//   base: process.env.BASE_URL,
  routes
})

export default router