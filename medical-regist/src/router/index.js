import Vue from 'vue'
import Router from 'vue-router'
// 👇 修改导入的组件名
import LoginRegister from '@/views/LoginRegister'
import RegistrationPage from '@/views/RegistrationPage' // 改这里
import MessagePage from '@/views/MessagePage' // 改这里
import ProfilePage from '@/views/ProfilePage' // 改这里
import DoctorPanel from '@/views/DoctorPanel'
import AdminPanel from '@/views/AdminPanel'

Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: '/',
      redirect: () => {
        const infoStr = localStorage.getItem('userInfo')
        return infoStr ? '/profile' : '/login-register'
      }
    },
    {
      path: '/login-register',
      name: 'LoginRegister',
      component: LoginRegister
    },
    {
      path: '/registration',
      name: 'RegistrationPage', // 改这里
      component: RegistrationPage, // 改这里
      meta: { roles: ['patient'] }
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
    },
    {
      path: '/doctor-panel',
      name: 'DoctorPanel',
      component: DoctorPanel,
      meta: { roles: ['doctor'] }
    },
    {
      path: '/admin-panel',
      name: 'AdminPanel',
      component: AdminPanel,
      meta: { roles: ['admin'] }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem('userInfo')
  const user = userStr ? JSON.parse(userStr) : null
  const roles = to.meta && to.meta.roles
  const isPublic = to.path === '/login-register'

  // 未登录：只能访问登录/注册页
  if (!user && !isPublic) {
    return next('/login-register')
  }

  // 已登录：不再访问登录/注册页，统一跳到个人中心
  if (user && to.path === '/login-register') {
    return next('/profile')
  }

  // 角色权限校验
  if (roles && roles.length) {
    if (!user || !roles.includes(user.role)) {
      alert('无权限访问该页面，请先使用对应账号登录')
      return next('/login-register')
    }
  }
  next()
})

export default router
