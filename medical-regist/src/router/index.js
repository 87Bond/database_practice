import Vue from 'vue'
import Router from 'vue-router'
// 👇 修改导入的组件名
import LoginRegister from '@/views/LoginRegister'
import RegistrationPage from '@/views/RegistrationPage' // 改这里
import MessagePage from '@/views/MessagePage' // 改这里
import ProfilePage from '@/views/ProfilePage' // 改这里

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      redirect: '/login-register'
    },
    {
      path: '/login-register',
      name: 'LoginRegister',
      component: LoginRegister
    },
    {
      path: '/registration',
      name: 'RegistrationPage', // 改这里
      component: RegistrationPage // 改这里
    },
    {
      path: '/message',
      name: 'MessagePage', // 改这里
      component: MessagePage // 改这里
    },
    {
      path: '/profile',
      name: 'ProfilePage', // 改这里
      component: ProfilePage // 改这里
    }
  ]
})