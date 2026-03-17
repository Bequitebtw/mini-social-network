<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'

const ACCESS_TOKEN_KEY = 'socialnetwork.accessToken'
const OAUTH_PROVIDERS = [
  { id: 'google', label: 'Google' },
  { id: 'discord', label: 'Discord' },
  { id: 'github', label: 'GitHub' },
]

const accessToken = ref(localStorage.getItem(ACCESS_TOKEN_KEY) ?? '')
const currentPath = ref(window.location.pathname || '/login')
const isLoading = ref(false)
const isRefreshingToken = ref(false)
const showLoginPassword = ref(false)
const showRegisterPassword = ref(false)
const showRegisterConfirmPassword = ref(false)

const message = reactive({
  type: 'info',
  text: '',
})

const loginForm = reactive({
  login: '',
  password: '',
})

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const searchState = reactive({
  query: '',
  loading: false,
})

const profile = ref(null)
const foundUser = ref(null)

const isAuthenticated = computed(() => Boolean(accessToken.value && profile.value))
const isRegisterPage = computed(() => currentPath.value === '/register')
const isProfilePage = computed(() => currentPath.value === '/profile')
const isOAuthCallbackPage = computed(() => currentPath.value === '/oauth/callback')
const isAuthPage = computed(() => !isAuthenticated.value && !isOAuthCallbackPage.value)
const activeUser = computed(() => foundUser.value)
const avatarText = computed(() => (profile.value?.username || 'SN').slice(0, 2).toUpperCase())

function setMessage(type, text) {
  message.type = type
  message.text = text
}

function clearMessage() {
  message.text = ''
}

function storeAccessToken(token) {
  accessToken.value = token ?? ''

  if (accessToken.value) {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken.value)
  } else {
    localStorage.removeItem(ACCESS_TOKEN_KEY)
  }
}

function normalizePath(pathname) {
  if (pathname === '/register') return '/register'
  if (pathname === '/profile') return '/profile'
  if (pathname === '/oauth/callback') return '/oauth/callback'
  if (pathname === '/') return '/'
  return '/login'
}

function go(path, replace = false) {
  const safePath = isAuthenticated.value
    ? normalizePath(path)
    : path === '/register'
      ? '/register'
      : '/login'

  const method = replace ? 'replaceState' : 'pushState'
  window.history[method]({}, '', safePath)
  currentPath.value = safePath
}

function syncPath() {
  currentPath.value = normalizePath(window.location.pathname)

  if (!isAuthenticated.value && !isOAuthCallbackPage.value && currentPath.value !== '/register') {
    currentPath.value = '/login'
    window.history.replaceState({}, '', '/login')
  }
}

function formatCreatedAt(value) {
  if (!value) {
    return 'Недоступно'
  }

  const [datePart] = String(value).split('T')
  return datePart || 'Недоступно'
}

function mapServerMessage(rawMessage, context = 'general') {
  if (!rawMessage) {
    if (context === 'search') return 'Пользователь не найден.'
    return 'Не удалось выполнить запрос.'
  }

  const normalized = String(rawMessage).toLowerCase()

  if (normalized.includes('bad credentials')) return 'Неверный логин, почта или пароль.'
  if (normalized.includes('login') && normalized.includes('not found')) {
    return context === 'search' ? 'Пользователь с таким username не найден.' : 'Аккаунт с такими данными не найден.'
  }
  if (normalized.includes('user') && normalized.includes('not found')) return 'Пользователь с таким username не найден.'
  if (normalized.includes('refresh token')) return 'Сессия истекла. Войдите снова.'
  if (normalized.includes('token') && normalized.includes('not found')) return 'Ссылка больше недействительна.'
  if (normalized.includes('expired')) return 'Ссылка или сессия уже истекла.'
  if (normalized.includes('email') && normalized.includes('exist')) return 'Аккаунт с такой почтой уже существует.'
  if (normalized.includes('username') && normalized.includes('exist')) return 'Этот username уже занят.'

  return rawMessage
}

function mapValidationError(errorText) {
  const normalized = String(errorText).toLowerCase()
  if (normalized.includes('email')) return 'Введите корректную почту.'
  if (normalized.includes('username')) return 'Проверьте username.'
  if (normalized.includes('password')) return 'Проверьте пароль.'
  return 'Проверьте заполнение полей.'
}

function extractErrorMessage(payload, context = 'general') {
  if (payload?.errors?.length) {
    return mapValidationError(payload.errors[0])
  }

  return mapServerMessage(payload?.message, context)
}

async function parseResponse(response) {
  const text = await response.text()
  return text ? JSON.parse(text) : null
}

async function refreshAccessToken() {
  if (isRefreshingToken.value) return false

  isRefreshingToken.value = true

  try {
    const response = await fetch('/api/auth/refresh', {
      method: 'POST',
      credentials: 'include',
      headers: accessToken.value
        ? {
            Authorization: `Bearer ${accessToken.value}`,
          }
        : {},
    })

    const payload = await parseResponse(response)

    if (!response.ok || !payload?.data?.accessToken) {
      storeAccessToken('')
      return false
    }

    storeAccessToken(payload.data.accessToken)
    return true
  } catch {
    storeAccessToken('')
    return false
  } finally {
    isRefreshingToken.value = false
  }
}

async function apiRequest(path, options = {}, retryOnUnauthorized = true) {
  const { headers, body, skipAuth = false, errorContext = 'general', ...rest } = options

  const requestHeaders = {
    ...(body ? { 'Content-Type': 'application/json' } : {}),
    ...(headers ?? {}),
  }

  if (!skipAuth && accessToken.value) {
    requestHeaders.Authorization = `Bearer ${accessToken.value}`
  }

  const response = await fetch(path, {
    credentials: 'include',
    headers: requestHeaders,
    body: body ? JSON.stringify(body) : undefined,
    ...rest,
  })

  const payload = await parseResponse(response)

  if (response.status === 401 && retryOnUnauthorized && accessToken.value) {
    const refreshed = await refreshAccessToken()
    if (refreshed) {
      return apiRequest(path, options, false)
    }
  }

  if (!response.ok) {
    throw new Error(extractErrorMessage(payload, errorContext))
  }

  return payload
}

async function loadMyProfile() {
  const payload = await apiRequest('/api/users/profile', { method: 'GET' })
  profile.value = payload?.data ?? null
}

async function bootstrapSession() {
  if (!accessToken.value) {
    storeAccessToken('')
    profile.value = null
    foundUser.value = null
    return
  }

  try {
    await loadMyProfile()
  } catch {
    const refreshed = await refreshAccessToken()

    if (refreshed) {
      try {
        await loadMyProfile()
      } catch {
        storeAccessToken('')
        profile.value = null
      }
    } else {
      storeAccessToken('')
      profile.value = null
    }
  }
}

async function submitLogin() {
  isLoading.value = true
  clearMessage()

  try {
    const payload = await apiRequest('/api/auth/login', {
      method: 'POST',
      body: loginForm,
      skipAuth: true,
      errorContext: 'login',
    })

    storeAccessToken(payload?.data?.accessToken ?? '')
    await loadMyProfile()
    foundUser.value = null
    go('/', true)
    setMessage('success', 'Вы вошли в аккаунт.')
  } catch (error) {
    storeAccessToken('')
    profile.value = null
    setMessage('error', error.message)
  } finally {
    isLoading.value = false
  }
}

async function submitRegistration() {
  if (registerForm.password !== registerForm.confirmPassword) {
    setMessage('error', 'Пароли не совпадают.')
    return
  }

  isLoading.value = true
  clearMessage()

  try {
    await apiRequest('/api/auth/registration', {
      method: 'POST',
      body: {
        username: registerForm.username,
        email: registerForm.email,
        password: registerForm.password,
      },
      skipAuth: true,
      errorContext: 'register',
    })

    loginForm.login = registerForm.email
    loginForm.password = ''
    registerForm.username = ''
    registerForm.email = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''

    go('/login', true)
    setMessage('success', 'Проверьте почту, чтобы завершить регистрацию.')
  } catch (error) {
    setMessage('error', error.message)
  } finally {
    isLoading.value = false
  }
}

async function confirmAccount(token) {
  if (!token) return

  isLoading.value = true
  clearMessage()

  try {
    await apiRequest(`/api/auth/confirm?token=${encodeURIComponent(token)}`, {
      method: 'GET',
      skipAuth: true,
    })

    window.history.replaceState({}, '', '/login')
    currentPath.value = '/login'
    setMessage('success', 'Почта подтверждена. Теперь можно войти.')
  } catch (error) {
    setMessage('error', error.message)
  } finally {
    isLoading.value = false
  }
}

async function handleOAuthCallback(token) {
  if (!token) {
    go('/login', true)
    setMessage('error', 'Не удалось завершить вход.')
    return
  }

  isLoading.value = true
  clearMessage()

  try {
    storeAccessToken(token)
    await loadMyProfile()
    foundUser.value = null
    go('/', true)
    setMessage('success', 'Вход выполнен.')
  } catch (error) {
    storeAccessToken('')
    profile.value = null
    go('/login', true)
    setMessage('error', error.message)
  } finally {
    isLoading.value = false
  }
}

function startOAuth(provider) {
  window.location.href = `/oauth2/authorization/${provider}`
}

async function searchUser() {
  if (!isAuthenticated.value) return

  const username = searchState.query.trim().replace(/^@/, '')
  if (!username) {
    foundUser.value = null
    clearMessage()
    return
  }

  searchState.loading = true
  clearMessage()

  try {
    const payload = await apiRequest(`/api/users/${encodeURIComponent(username)}/profile`, {
      method: 'GET',
      errorContext: 'search',
    })

    foundUser.value = payload?.data ?? null
  } catch (error) {
    foundUser.value = null
    setMessage('error', error.message)
  } finally {
    searchState.loading = false
  }
}

async function logout() {
  isLoading.value = true
  clearMessage()

  try {
    await apiRequest('/api/auth/logout', { method: 'POST' })
  } catch {
    // ignore logout transport failures and clear local session anyway
  } finally {
    storeAccessToken('')
    profile.value = null
    foundUser.value = null
    searchState.query = ''
    go('/login', true)
    setMessage('info', 'Вы вышли из аккаунта.')
    isLoading.value = false
  }
}

function handlePopState() {
  syncPath()
}

onMounted(async () => {
  const url = new URL(window.location.href)
  const token = url.searchParams.get('token')
  const oauthAccessToken = url.searchParams.get('accessToken')
  const screen = url.searchParams.get('screen')

  if (token) {
    await confirmAccount(token)
    return
  }

  if (normalizePath(window.location.pathname) === '/oauth/callback') {
    await handleOAuthCallback(oauthAccessToken)
    return
  }

  await bootstrapSession()

  if (!isAuthenticated.value) {
    const startPath = screen === 'register' || window.location.pathname === '/register' ? '/register' : '/login'
    window.history.replaceState({}, '', startPath)
    currentPath.value = startPath
  } else {
    syncPath()
  }

  window.addEventListener('popstate', handlePopState)
})

onUnmounted(() => {
  window.removeEventListener('popstate', handlePopState)
})
</script>

<template>
  <div class="app-shell">
    <main v-if="isOAuthCallbackPage" class="callback-screen">
      <div class="callback-card">
        <div class="spinner"></div>
      </div>
    </main>

    <main v-else-if="isAuthPage" class="auth-shell">
      <section class="auth-backdrop"></section>

      <section class="auth-panel">
        <div class="brand-row">
          <button class="brand" type="button" @click="go('/login', true)">Socialy</button>
          <button class="switch-link" type="button" @click="isRegisterPage ? go('/login') : go('/register')">
            {{ isRegisterPage ? 'Войти' : 'Регистрация' }}
          </button>
        </div>

        <h1 class="auth-title">{{ isRegisterPage ? 'Создать аккаунт' : 'Вход' }}</h1>

        <div v-if="message.text" :class="['notice', message.type]">
          {{ message.text }}
        </div>

        <div class="oauth-section">
          <p class="oauth-label">{{ isRegisterPage ? 'Регистрация через' : 'Войти через' }}</p>

          <div class="oauth-grid">
            <button
              v-for="provider in OAUTH_PROVIDERS"
              :key="provider.id"
              class="oauth-btn"
              type="button"
              @click="startOAuth(provider.id)"
            >
              <span class="oauth-icon" :class="provider.id">
                <svg v-if="provider.id === 'google'" viewBox="0 0 24 24" aria-hidden="true">
                  <path fill="currentColor" d="M21.8 12.2c0-.7-.1-1.4-.2-2H12v3.8h5.5a4.7 4.7 0 0 1-2 3.1v2.6h3.3c1.9-1.8 3-4.4 3-7.5Z"/>
                  <path fill="currentColor" d="M12 22c2.7 0 5-.9 6.7-2.3l-3.3-2.6c-.9.6-2 .9-3.4.9-2.6 0-4.8-1.7-5.5-4.1H3.1v2.7A10 10 0 0 0 12 22Z"/>
                  <path fill="currentColor" d="M6.5 13.9a6 6 0 0 1 0-3.8V7.4H3.1a10 10 0 0 0 0 9.1l3.4-2.6Z"/>
                  <path fill="currentColor" d="M12 5.9c1.5 0 2.8.5 3.8 1.5l2.8-2.8C17 3.1 14.7 2 12 2A10 10 0 0 0 3.1 7.4l3.4 2.7c.7-2.4 2.9-4.2 5.5-4.2Z"/>
                </svg>
                <svg v-else-if="provider.id === 'discord'" viewBox="0 0 24 24" aria-hidden="true">
                  <path fill="currentColor" d="M20.3 5.4A16.7 16.7 0 0 0 16 4l-.2.4c1.7.4 2.5 1 2.5 1a11 11 0 0 0-6.3-1.8c-2.2 0-4.3.6-6.3 1.8 0 0 .9-.7 2.7-1L8.1 4c-1.5.2-2.9.7-4.3 1.4C1 9.5.2 13.4.5 17.3a16.8 16.8 0 0 0 5.2 2.6l1.1-1.8c-.6-.2-1.1-.5-1.6-.8.1.1.3.2.4.2 2 1 4.2 1.5 6.4 1.5s4.4-.5 6.4-1.5c.1 0 .3-.1.4-.2-.5.3-1 .6-1.6.8l1.1 1.8a16.7 16.7 0 0 0 5.2-2.6c.4-4.5-.7-8.4-3.6-11.9ZM9.5 14.9c-.8 0-1.4-.8-1.4-1.7 0-1 .6-1.7 1.4-1.7.8 0 1.4.8 1.4 1.7 0 1-.6 1.7-1.4 1.7Zm5 0c-.8 0-1.4-.8-1.4-1.7 0-1 .6-1.7 1.4-1.7.8 0 1.4.8 1.4 1.7 0 1-.6 1.7-1.4 1.7Z"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" aria-hidden="true">
                  <path fill="currentColor" d="M12 .5A12 12 0 0 0 8.2 24c.6.1.8-.2.8-.6v-2.3c-3.3.7-4-1.4-4-1.4-.6-1.4-1.3-1.8-1.3-1.8-1.1-.7 0-.7 0-.7 1.2.1 1.9 1.2 1.9 1.2 1 1.8 2.8 1.2 3.4.9.1-.8.4-1.2.7-1.5-2.6-.3-5.4-1.3-5.4-6A4.7 4.7 0 0 1 5.4 8c-.1-.3-.5-1.5.1-3.1 0 0 1-.3 3.2 1.2a11 11 0 0 1 5.8 0c2.2-1.5 3.2-1.2 3.2-1.2.6 1.6.2 2.8.1 3.1a4.7 4.7 0 0 1 1.2 3.2c0 4.7-2.8 5.7-5.4 6 .4.3.8 1 .8 2.1v3.1c0 .4.2.7.8.6A12 12 0 0 0 12 .5Z"/>
                </svg>
              </span>
              <span>{{ provider.label }}</span>
            </button>
          </div>
        </div>

        <div class="divider"><span>или</span></div>

        <form v-if="isRegisterPage" class="form-grid" @submit.prevent="submitRegistration">
          <label class="field">
            <span>Username</span>
            <input v-model.trim="registerForm.username" type="text" autocomplete="username" required />
          </label>

          <label class="field">
            <span>Почта</span>
            <input v-model.trim="registerForm.email" type="email" autocomplete="email" required />
          </label>

          <label class="field">
            <span>Пароль</span>
            <div class="password-field">
              <input
                v-model="registerForm.password"
                :type="showRegisterPassword ? 'text' : 'password'"
                autocomplete="new-password"
                required
              />
              <button class="password-toggle" type="button" @click="showRegisterPassword = !showRegisterPassword">
                {{ showRegisterPassword ? 'Скрыть' : 'Показать' }}
              </button>
            </div>
          </label>

          <label class="field">
            <span>Повторите пароль</span>
            <div class="password-field">
              <input
                v-model="registerForm.confirmPassword"
                :type="showRegisterConfirmPassword ? 'text' : 'password'"
                autocomplete="new-password"
                required
              />
              <button class="password-toggle" type="button" @click="showRegisterConfirmPassword = !showRegisterConfirmPassword">
                {{ showRegisterConfirmPassword ? 'Скрыть' : 'Показать' }}
              </button>
            </div>
          </label>

          <p v-if="registerForm.confirmPassword && registerForm.password !== registerForm.confirmPassword" class="inline-error">
            Пароли не совпадают
          </p>

          <button class="primary-btn" type="submit" :disabled="isLoading">
            {{ isLoading ? 'Подождите...' : 'Продолжить' }}
          </button>
        </form>

        <form v-else class="form-grid" @submit.prevent="submitLogin">
          <label class="field">
            <span>Логин или почта</span>
            <input v-model.trim="loginForm.login" type="text" autocomplete="username" required />
          </label>

          <label class="field">
            <span>Пароль</span>
            <div class="password-field">
              <input
                v-model="loginForm.password"
                :type="showLoginPassword ? 'text' : 'password'"
                autocomplete="current-password"
                required
              />
              <button class="password-toggle" type="button" @click="showLoginPassword = !showLoginPassword">
                {{ showLoginPassword ? 'Скрыть' : 'Показать' }}
              </button>
            </div>
          </label>

          <button class="primary-btn" type="submit" :disabled="isLoading">
            {{ isLoading ? 'Подождите...' : 'Войти' }}
          </button>
        </form>
      </section>
    </main>

    <template v-else>
      <header class="topbar">
        <button class="profile-pill" type="button" @click="go('/profile')">
          <span class="avatar">{{ avatarText }}</span>
        </button>
      </header>

      <main v-if="isProfilePage" class="profile-page">
        <section class="profile-card">
          <div class="profile-cover"></div>
          <div class="profile-header">
            <div class="profile-avatar">{{ avatarText }}</div>
            <div class="profile-identity">
              <h2>{{ profile?.username }}</h2>
              <p>{{ profile?.email }}</p>
            </div>
          </div>

          <div class="profile-stats">
            <article>
              <span>Username</span>
              <strong>@{{ profile?.username }}</strong>
            </article>
            <article>
              <span>Почта</span>
              <strong>{{ profile?.email }}</strong>
            </article>
            <article>
              <span>Дата создания</span>
              <strong>{{ formatCreatedAt(profile?.createdAt) }}</strong>
            </article>
          </div>

          <button class="ghost-btn logout-profile" type="button" @click="logout">
            Выйти
          </button>
        </section>
      </main>

      <main v-else class="home-page">
        <section class="search-stage">
          <form class="search-panel" @submit.prevent="searchUser">
            <span>@</span>
            <input v-model.trim="searchState.query" type="text" placeholder="username" />
          </form>
        </section>

        <section v-if="activeUser" class="result-card">
          <div class="result-head">
            <div class="result-avatar">
              {{ (activeUser.username || 'SN').slice(0, 2).toUpperCase() }}
            </div>
            <div>
              <h3>@{{ activeUser.username }}</h3>
              <p>{{ activeUser.email }}</p>
            </div>
          </div>

          <div class="result-meta">
            <span>Дата создания</span>
            <strong>{{ formatCreatedAt(activeUser.createdAt) }}</strong>
          </div>
        </section>

        <div v-if="message.text" :class="['notice', message.type]">
          {{ message.text }}
        </div>
      </main>
    </template>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  color: #f8fafc;
}

.callback-screen,
.auth-shell,
.home-page,
.profile-page {
  min-height: calc(100vh - 64px);
}

.callback-screen,
.search-stage {
  display: grid;
  place-items: center;
}

.callback-card,
.auth-backdrop,
.auth-panel,
.topbar,
.search-panel,
.result-card,
.profile-card {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(10, 10, 14, 0.78);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.34);
  backdrop-filter: blur(18px);
}

.callback-card,
.auth-backdrop,
.auth-panel,
.result-card,
.profile-card {
  border-radius: 32px;
}

.callback-card {
  display: grid;
  place-items: center;
  width: 120px;
  height: 120px;
}

.spinner {
  width: 42px;
  height: 42px;
  border: 3px solid rgba(255, 255, 255, 0.14);
  border-top-color: #8b5cf6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.auth-shell {
  display: grid;
  grid-template-columns: minmax(280px, 1fr) minmax(360px, 480px);
  gap: 24px;
}

.auth-backdrop {
  background:
    radial-gradient(circle at top left, rgba(139, 92, 246, 0.2), transparent 30%),
    radial-gradient(circle at bottom right, rgba(37, 99, 235, 0.2), transparent 28%),
    linear-gradient(160deg, rgba(8, 8, 12, 0.92), rgba(15, 23, 42, 0.78));
}

.auth-panel {
  padding: 28px;
}

.brand-row,
.topbar,
.profile-header,
.result-head {
  display: flex;
  align-items: center;
}

.brand-row,
.result-head {
  justify-content: space-between;
}

.brand,
.switch-link,
.ghost-btn,
.primary-btn,
.oauth-btn,
.profile-pill,
.password-toggle {
  border: none;
  border-radius: 18px;
  font: inherit;
  cursor: pointer;
  transition:
    transform 0.18s ease,
    opacity 0.18s ease,
    background 0.18s ease;
}

.brand {
  background: transparent;
  color: #ffffff;
  font-size: 1.35rem;
  font-weight: 800;
}

.switch-link,
.ghost-btn,
.oauth-btn,
.profile-pill,
.password-toggle {
  background: rgba(255, 255, 255, 0.04);
  color: #d4d4d8;
}

.switch-link,
.ghost-btn {
  min-height: 46px;
  padding: 0 16px;
}

.auth-title {
  margin: 28px 0 18px;
  font-size: 2rem;
}

.oauth-section {
  display: grid;
  gap: 12px;
}

.oauth-label {
  margin: 0;
  color: #a1a1aa;
  font-size: 0.95rem;
}

.oauth-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.oauth-btn {
  display: grid;
  gap: 10px;
  place-items: center;
  min-height: 84px;
  padding: 12px;
}

.oauth-icon {
  display: grid;
  place-items: center;
  width: 38px;
  height: 38px;
  border-radius: 12px;
  color: #fff;
}

.oauth-icon.google {
  background: linear-gradient(135deg, #ea4335, #fbbc05);
}

.oauth-icon.discord {
  background: linear-gradient(135deg, #5865f2, #7c83ff);
}

.oauth-icon.github {
  background: linear-gradient(135deg, #24292f, #3f444b);
}

.oauth-icon svg {
  width: 20px;
  height: 20px;
}

.divider {
  display: grid;
  place-items: center;
  margin: 20px 0;
}

.divider span {
  padding: 0 12px;
  color: #71717a;
  text-transform: uppercase;
  font-size: 0.78rem;
  letter-spacing: 0.12em;
}

.form-grid {
  display: grid;
  gap: 14px;
}

.field {
  display: grid;
  gap: 8px;
}

.field span,
.profile-stats span,
.result-meta span {
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #71717a;
}

.field input,
.search-panel,
.password-field {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(18, 18, 24, 0.96);
}

.field input {
  min-height: 54px;
  padding: 0 16px;
  border-radius: 18px;
  color: #f8fafc;
  font: inherit;
}

.password-field {
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  gap: 10px;
  min-height: 54px;
  padding: 0 8px 0 0;
  border-radius: 18px;
}

.password-field input {
  min-height: 52px;
  border: none;
  background: transparent;
}

.password-field input:focus {
  outline: none;
}

.password-toggle {
  min-height: 38px;
  padding: 0 14px;
  border-radius: 14px;
}

.field input:focus,
.search-panel:focus-within,
.password-field:focus-within {
  outline: 2px solid rgba(139, 92, 246, 0.22);
  border-color: rgba(139, 92, 246, 0.4);
}

.inline-error {
  margin: 0;
  color: #fb7185;
}

.primary-btn {
  min-height: 54px;
  background: linear-gradient(135deg, #7c3aed, #2563eb);
  color: #fff;
  font-weight: 700;
}

.notice {
  margin-top: 18px;
  padding: 14px 16px;
  border-radius: 18px;
  font-size: 0.95rem;
}

.notice.info {
  background: rgba(59, 130, 246, 0.12);
  color: #93c5fd;
}

.notice.success {
  background: rgba(16, 185, 129, 0.12);
  color: #6ee7b7;
}

.notice.error {
  background: rgba(244, 63, 94, 0.12);
  color: #fda4af;
}

.topbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 24px;
  padding: 14px 18px;
  border-radius: 24px;
}

.profile-pill {
  display: grid;
  place-items: center;
  width: 52px;
  height: 52px;
}

.avatar,
.profile-avatar,
.result-avatar {
  display: grid;
  place-items: center;
  border-radius: 50%;
  color: #fff;
  font-weight: 800;
  background: linear-gradient(135deg, #7c3aed, #2563eb);
}

.avatar {
  width: 34px;
  height: 34px;
  font-size: 0.82rem;
}

.home-page {
  display: grid;
  gap: 20px;
  align-content: start;
}

.search-stage {
  min-height: calc(100vh - 220px);
}

.search-panel {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 12px;
  align-items: center;
  width: min(100%, 720px);
  min-height: 82px;
  padding: 0 24px;
  border-radius: 28px;
}

.search-panel span {
  font-size: 1.6rem;
  color: #71717a;
  font-weight: 800;
}

.search-panel input {
  width: 100%;
  border: none;
  background: transparent;
  outline: none;
  color: #fff;
  font-size: 1.25rem;
}

.result-card,
.profile-card {
  padding: 24px;
}

.result-card {
  max-width: 720px;
  margin: -120px auto 0;
}

.result-head,
.profile-header {
  gap: 16px;
}

.result-avatar {
  width: 64px;
  height: 64px;
  font-size: 1.1rem;
}

.result-head h3,
.profile-identity h2 {
  margin: 0;
  color: #fff;
}

.result-head p,
.profile-identity p {
  margin: 4px 0 0;
  color: #a1a1aa;
}

.result-meta {
  display: grid;
  gap: 8px;
  margin-top: 22px;
}

.result-meta strong,
.profile-stats strong {
  color: #fff;
}

.profile-page {
  display: grid;
}

.profile-cover {
  height: 220px;
  margin: -24px -24px 0;
  border-radius: 32px 32px 0 0;
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.12), transparent 24%),
    linear-gradient(135deg, #111827, #312e81 52%, #7c3aed);
}

.profile-header {
  margin-top: -56px;
}

.profile-avatar {
  width: 104px;
  height: 104px;
  border: 5px solid rgba(10, 10, 14, 0.82);
  font-size: 1.8rem;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-top: 28px;
}

.profile-stats article {
  padding: 18px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.03);
}

.logout-profile {
  margin-top: 20px;
}

.brand:hover,
.switch-link:hover,
.ghost-btn:hover,
.primary-btn:hover,
.oauth-btn:hover,
.profile-pill:hover,
.password-toggle:hover {
  transform: translateY(-1px);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1024px) {
  .auth-shell,
  .profile-stats,
  .oauth-grid {
    grid-template-columns: 1fr;
  }

  .search-panel {
    min-height: 72px;
  }

  .result-card {
    margin-top: 0;
  }
}

@media (max-width: 640px) {
  .brand-row,
  .profile-header,
  .result-head {
    display: grid;
    justify-content: start;
  }

  .search-panel input {
    font-size: 1rem;
  }

  .password-field {
    grid-template-columns: 1fr;
    padding: 0;
  }

  .password-toggle {
    width: 100%;
    border-radius: 0 0 18px 18px;
  }
}
</style>
