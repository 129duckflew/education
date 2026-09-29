import { useEffect } from "react"
import { useQueryClient } from "@tanstack/react-query"
import { getAccessToken } from "@/lib/api"
import { useAuth } from "@/lib/auth"

/**
 * 订阅统一实时流（SSE）：通知 / 私信消息 / 已读回执共用一条连接。
 * 收到事件时让相关查询失效，配合轮询兜底。
 */
export function useRealtimeStream() {
  const { user } = useAuth()
  const queryClient = useQueryClient()

  useEffect(() => {
    if (!user) return
    const token = getAccessToken()
    if (!token) return

    const source = new EventSource(`/api/stream?token=${encodeURIComponent(token)}`)

    const refreshNotifications = () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] })
    }
    const refreshConversations = () => {
      queryClient.invalidateQueries({ queryKey: ["conversations"] })
      queryClient.invalidateQueries({ queryKey: ["conversation"] })
    }
    const refreshAll = () => {
      refreshNotifications()
      refreshConversations()
    }

    source.addEventListener("notification", refreshNotifications)
    source.addEventListener("message", refreshConversations)
    source.addEventListener("read", refreshConversations)
    source.addEventListener("connected", refreshAll)
    return () => source.close()
  }, [user, queryClient])
}
