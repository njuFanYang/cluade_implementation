<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
/**
 * Root application component
 *
 * @description Main container for the application, provides router-view for page rendering
 *
 * @author MusicShare Team
 * @version 1.0.0
 */

import { onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

onMounted(() => {
  // Fetch user profile if logged in
  if (userStore.isLoggedIn) {
    userStore.fetchUserProfile().catch(() => {
      // Token might be expired, clear local data
      userStore.logout()
    })
  }
})
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>
