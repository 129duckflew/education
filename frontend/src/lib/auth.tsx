import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react"
import { api, clearTokens, getAccessToken, setTokens } from "@/lib/api"
import type { TokenResponse, UserProfile } from "@/lib/types"

interface LoginInput {
  account: string
  password: string
}

interface RegisterInput {
  username?: string
  email?: string
  phone?: string
  password: string
  code: string
}

interface AuthContextValue {
  user: UserProfile | null
  loading: boolean
  login: (input: LoginInput) => Promise<void>
  register: (input: RegisterInput) => Promise<void>
  logout: () => void
  refreshUser: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)

  const applyTokens = useCallback((token: TokenResponse) => {
    setTokens(token.accessToken, token.refreshToken)
    setUser(token.user)
  }, [])

  const refreshUser = useCallback(async () => {
    if (!getAccessToken()) {
      setUser(null)
      return
    }
    try {
      setUser(await api.get<UserProfile>("/users/me"))
    } catch {
      clearTokens()
      setUser(null)
    }
  }, [])

  useEffect(() => {
    refreshUser().finally(() => setLoading(false))
  }, [refreshUser])

  const login = useCallback(
    async (input: LoginInput) => {
      applyTokens(await api.post<TokenResponse>("/auth/login", input))
    },
    [applyTokens],
  )

  const register = useCallback(
    async (input: RegisterInput) => {
      applyTokens(await api.post<TokenResponse>("/auth/register", input))
    },
    [applyTokens],
  )

  const logout = useCallback(() => {
    clearTokens()
    setUser(null)
  }, [])

  const value = useMemo(
    () => ({ user, loading, login, register, logout, refreshUser }),
    [user, loading, login, register, logout, refreshUser],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error("useAuth 必须在 AuthProvider 内使用")
  }
  return context
}
