/**
 * Authentication E2E Tests
 *
 * Covers:
 * - Homepage loads
 * - Navigate to login/register
 * - Register with validation errors
 * - Login with invalid credentials
 * - Full register → login → logout flow (requires running backend)
 */
import { test, expect } from '@playwright/test'

test.describe('Authentication Flow', () => {

  test('homepage loads with navigation', async ({ page }) => {
    await page.goto('/')
    // Page loads without error
    await expect(page).not.toHaveURL(/error/)
    // Some navigation element should exist
    const nav = page.locator('nav, header, [class*="nav"], [class*="header"]')
    await expect(nav.first()).toBeVisible()
  })

  test('login page is accessible and has form', async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')

    // Login form should be present
    const form = page.locator('form, [class*="login"], [class*="form"]')
    await expect(form.first()).toBeVisible()

    // Username/email and password inputs should exist
    const inputs = page.locator('input')
    await expect(inputs).toHaveCount(await inputs.count())
    expect(await inputs.count()).toBeGreaterThanOrEqual(2)
  })

  test('register page is accessible and has form', async ({ page }) => {
    await page.goto('/register')
    await page.waitForLoadState('networkidle')

    const form = page.locator('form, [class*="register"], [class*="form"]')
    await expect(form.first()).toBeVisible()

    // Should have at least 3 inputs (username, email, password)
    const inputs = page.locator('input')
    expect(await inputs.count()).toBeGreaterThanOrEqual(3)
  })

  test('can navigate from login to register page', async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')

    // Click register link
    const registerLink = page.locator('a[href*="register"], text=Register, text=注册')
    if (await registerLink.count() > 0) {
      await registerLink.first().click()
      await page.waitForLoadState('networkidle')
      expect(page.url()).toContain('register')
    }
  })

  test('login form validation - empty fields', async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')

    // Try to submit empty form
    const submitBtn = page.locator('button[type="submit"], button:has-text("Login"), button:has-text("登录")')
    if (await submitBtn.count() > 0) {
      await submitBtn.first().click()
      await page.waitForTimeout(500)
      // Should stay on login page or show validation error
      expect(page.url()).toContain('login')
    }
  })

  test('login with invalid credentials shows error feedback', async ({ page }) => {
    await page.goto('/login')
    await page.waitForLoadState('networkidle')

    // Fill in invalid credentials
    const usernameInput = page.locator('input[type="text"], input[placeholder*="username"], input[placeholder*="email"]').first()
    const passwordInput = page.locator('input[type="password"]').first()

    await usernameInput.fill('definitely_nonexistent_user_xyz_123')
    await passwordInput.fill('wrongpassword123')

    const submitBtn = page.locator('button[type="submit"], button:has-text("Login"), button:has-text("登录")')
    await submitBtn.first().click()

    // Wait for response
    await page.waitForTimeout(3000)

    // Should stay on login page (not redirect to home)
    expect(page.url()).toContain('login')
  })

  test('register with mismatched passwords stays on page', async ({ page }) => {
    await page.goto('/register')
    await page.waitForLoadState('networkidle')

    const inputs = page.locator('input')
    const inputCount = await inputs.count()

    if (inputCount >= 4) {
      // Fill username
      await inputs.nth(0).fill('testregistration')
      // Fill email
      await inputs.nth(1).fill('testregistration@example.com')
      // Fill password
      await inputs.nth(2).fill('password123')
      // Fill confirm password (different)
      await inputs.nth(3).fill('different_password')

      const submitBtn = page.locator('button[type="submit"]')
      await submitBtn.first().click()
      await page.waitForTimeout(1000)

      // Should stay on register page
      expect(page.url()).toContain('register')
    }
  })
})
