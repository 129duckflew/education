import type { ReactNode } from "react"
import { Navigate, useLocation } from "react-router-dom"
import { useAuth } from "@/lib/auth"
import { LoadingState } from "@/components/ui/spinner"
import type { UserRole } from "@/lib/types"

export function RequireAuth({ children, role }: { children: ReactNode; role?: UserRole }) {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return <LoadingState />
  }
  if (!user) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />
  }
  if (role && user.role !== role) {
    return <Navigate to="/" replace />
  }
  return <>{children}</>
}
