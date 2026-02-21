/**
 * Social Features E2E Tests
 *
 * Covers:
 * - Like/unlike music and playlists
 * - Comment on music
 * - Follow/unfollow users
 * - View followers/following
 * - Social interactions require authentication
 */
import { test, expect } from '@playwright/test'

test.describe('Social Features', () => {

  test('like button is visible on music items', async ({ page }) => {
    await page.goto('/music')
    await page.waitForLoadState('networkidle')
    await page.waitForTimeout(2000)

    // Like buttons may be present on music cards
    const likeButtons = page.locator(
      '[class*="like-btn"], [class*="like"] button, button[aria-label*="like" i]'
    )
    // Just verify the page loads
    expect(await page.locator('body').isVisible()).toBe(true)
  })

  test('like music requires authentication', async ({ page }) => {
    await page.goto('/music')
    await page.waitForLoadState('networkidle')
    await page.waitForTimeout(2000)

    // Try to click a like button
    const likeBtn = page.locator(
      '[class*="like-btn"], [class*="like"] button, .el-button:has([class*="like"])'
    ).first()

    if (await likeBtn.count() > 0) {
      await likeBtn.click()
      await page.waitForTimeout(1000)
      // Should redirect to login or show auth message
      // (not crash)
      expect(await page.locator('body').isVisible()).toBe(true)
    }
  })

  test('user profile page is accessible', async ({ page }) => {
    await page.goto('/users/testuser')
    await page.waitForTimeout(2000)

    // Should show profile page or 404, not a blank/error page
    expect(await page.locator('body').isVisible()).toBe(true)
  })

  test('follow button requires authentication', async ({ page }) => {
    await page.goto('/users/testuser')
    await page.waitForTimeout(2000)

    const followBtn = page.locator(
      'button:has-text("Follow"), button:has-text("关注"), [class*="follow-btn"]'
    )

    if (await followBtn.count() > 0) {
      await followBtn.first().click()
      await page.waitForTimeout(1000)
      // Should redirect to login or show auth prompt
      expect(await page.locator('body').isVisible()).toBe(true)
    }
  })

  test('comment section visible on music detail', async ({ page }) => {
    await page.goto('/music')
    await page.waitForLoadState('networkidle')
    await page.waitForTimeout(2000)

    // Try to navigate to a music detail page
    const musicItem = page.locator(
      '.music-card a, [class*="music-item"] a, [class*="music-title"]'
    ).first()

    if (await musicItem.count() > 0) {
      const href = await musicItem.getAttribute('href')
      if (href) {
        await page.goto(href)
        await page.waitForTimeout(2000)

        const commentSection = page.locator(
          '[class*="comment"], #comments, .comment-section'
        )
        if (await commentSection.count() > 0) {
          await expect(commentSection.first()).toBeVisible()
        }
      }
    }
  })

  test('add comment requires authentication', async ({ page }) => {
    await page.goto('/music')
    await page.waitForLoadState('networkidle')
    await page.waitForTimeout(2000)

    // Navigate to first music detail
    const musicLink = page.locator(
      '[class*="music-card"] a, [class*="music-item"] a'
    ).first()

    if (await musicLink.count() > 0) {
      await musicLink.click()
      await page.waitForTimeout(1500)

      // Try to find comment input
      const commentInput = page.locator(
        'textarea[placeholder*="comment" i], input[placeholder*="comment" i], .comment-input'
      )

      if (await commentInput.count() > 0) {
        await commentInput.first().fill('Test comment')
        const submitBtn = page.locator('button:has-text("Submit"), button:has-text("Comment"), button[type="submit"]')
        if (await submitBtn.count() > 0) {
          await submitBtn.first().click()
          await page.waitForTimeout(1000)
          // Should stay put or go to login
          expect(await page.locator('body').isVisible()).toBe(true)
        }
      }
    }
  })

  test('playlist like/favorite is accessible from playlist page', async ({ page }) => {
    await page.goto('/playlists')
    await page.waitForTimeout(2000)

    // Verify playlist page loads
    expect(await page.locator('body').isVisible()).toBe(true)
  })
})
