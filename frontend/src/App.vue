<template>
  <div id="app">
    <AppHeader />
    <main class="app-main">
      <router-view />
    </main>
    <MusicPlayer />
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
import AppHeader from '@/components/common/AppHeader.vue'
import MusicPlayer from '@/components/Player/MusicPlayer.vue'

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
  background: #f5f7fa;
}

.app-main {
  flex: 1;
  min-height: 0;
  padding-bottom: 80px; /* Space for music player */
}
</style>
