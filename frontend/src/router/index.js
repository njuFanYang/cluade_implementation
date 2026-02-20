/**
 * Vue Router configuration
 *
 * @description Defines application routes and navigation guards for
 *              authentication and authorization.
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

/**
 * Route definitions
 *
 * meta fields:
 * - title: Page title
 * - requiresAuth: Requires authentication
 * - roles: Required user roles (array)
 */
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: 'Home' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue'),
    meta: { title: 'Login' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue'),
    meta: { title: 'Register' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { title: 'My Profile', requiresAuth: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/user/Settings.vue'),
    meta: { title: 'Settings', requiresAuth: true }
  },
  {
    path: '/music/:id',
    name: 'MusicDetail',
    component: () => import('@/views/music/MusicDetail.vue'),
    meta: { title: 'Music Detail' }
  },
  {
    path: '/upload',
    name: 'Upload',
    component: () => import('@/views/music/Upload.vue'),
    meta: { title: 'Upload Music', requiresAuth: true, roles: ['MUSICIAN', 'ADMIN'] }
  },
  {
    path: '/playlist/:id',
    name: 'PlaylistDetail',
    component: () => import('@/views/playlist/PlaylistDetail.vue'),
    meta: { title: 'Playlist' }
  },
  {
    path: '/playlist/create',
    name: 'CreatePlaylist',
    component: () => import('@/views/playlist/Create.vue'),
    meta: { title: 'Create Playlist', requiresAuth: true }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/Search.vue'),
    meta: { title: 'Search' }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('@/views/admin/Dashboard.vue'),
    meta: { title: 'Admin Dashboard', requiresAuth: true, roles: ['ADMIN'] }
  },
  {
    path: '/admin/audit',
    name: 'AdminAudit',
    component: () => import('@/views/admin/AuditList.vue'),
    meta: { title: 'Content Audit', requiresAuth: true, roles: ['ADMIN'] }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('@/views/admin/UserManage.vue'),
    meta: { title: 'User Management', requiresAuth: true, roles: ['ADMIN'] }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '404 Not Found' }
  }
]

// Create router instance
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// Navigation guard
router.beforeEach((to, from, next) => {
  // Set page title
  document.title = to.meta.title ? `${to.meta.title} - MusicShare` : 'MusicShare'

  // Check authentication
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

  if (to.meta.requiresAuth && !token) {
    // Redirect to login if authentication required
    ElMessage.warning('Please login first')
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else if (to.meta.roles && to.meta.roles.length > 0) {
    // Check user role
    const userRole = userInfo.role
    if (to.meta.roles.includes(userRole)) {
      next()
    } else {
      ElMessage.error('Access denied')
      next('/')
    }
  } else {
    next()
  }
})

export default router
