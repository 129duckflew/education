import { useEffect } from "react"
import { useQueryClient } from "@tanstack/react-query"
import { getAccessToken } from "@/lib/api"
import { useAuth } from "@/lib/auth"

/**
 * 订阅通知 SSE。新建通知时让相关查询失效（配合 30s 轮询兜底）。
 */
export function useNotificationStream() {
  const { user } = useAuth()
  const queryClient = useQueryClient()

  useEffect(() => {
    if (!user) return
    const token = getAccessToken()
    if (!token) return

    const source = new EventSource(`/api/notifications/stream?token=${encodeURIComponent(token)}`)
    const refresh = () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] })
      queryClient.invalidateQueries({ queryKey: ["notifications", "unread"] })
    }
    source.addEventListener("notification", refresh)
    source.addEventListener("connected", refresh)
    return () => source.close()
  }, [user, queryClient])
}
