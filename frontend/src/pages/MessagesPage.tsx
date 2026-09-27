import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { api, type PageResponse } from "@/lib/api"
import type { ConversationView, MessageView, NotificationView } from "@/lib/types"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { EmptyState, LoadingState } from "@/components/ui/spinner"

const typeLabels: Record<string, string> = {
  SYSTEM: "系统",
  QUESTION_RECEIVED: "收到提问",
  QUESTION_APPROVED: "问题通过",
  QUESTION_REJECTED: "问题未通过",
  QUESTION_FORBIDDEN: "问题被禁止",
  ANSWER_RECEIVED: "收到回答",
  ANSWER_LIKED: "回答被赞",
  ANSWER_COLLECTED: "回答被收藏",
  QUESTION_LIKED: "问题被赞",
  PROFESSOR_APPROVED: "教授认证通过",
  PROFESSOR_REJECTED: "教授认证未通过",
}

export function MessagesPage() {
  const [tab, setTab] = useState<"notifications" | "conversations">("notifications")

  return (
    <div className="space-y-5">
      <div className="flex gap-2">
        <Button
          variant={tab === "notifications" ? "default" : "outline"}
          size="sm"
          onClick={() => setTab("notifications")}
        >
          通知
        </Button>
        <Button
          variant={tab === "conversations" ? "default" : "outline"}
          size="sm"
          onClick={() => setTab("conversations")}
        >
          私信
        </Button>
      </div>
      {tab === "notifications" ? <Notifications /> : <Conversations />}
    </div>
  )
}

function Notifications() {
  const queryClient = useQueryClient()
  const navigate = useNavigate()
  const query = useQuery({
    queryKey: ["notifications"],
    queryFn: () => api.get<PageResponse<NotificationView>>("/notifications?size=30"),
  })
  const markAll = useMutation({
    mutationFn: () => api.post("/notifications/read-all"),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["notifications"] }),
  })
  const markRead = useMutation({
    mutationFn: (id: number) => api.post(`/notifications/${id}/read`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["notifications"] }),
  })

  if (query.isLoading) return <LoadingState />

  function handleClick(n: NotificationView) {
    if (!n.read) markRead.mutate(n.id)
    const link = notificationLink(n.type, n.resourceId)
    if (link) navigate(link)
  }

  return (
    <div className="space-y-3">
      <div className="flex justify-end">
        <Button variant="outline" size="sm" onClick={() => markAll.mutate()}>
          全部已读
        </Button>
      </div>
      {query.data?.list.length ? (
        query.data.list.map((n) => (
          <Card
            key={n.id}
            className={n.read ? "" : "border-primary/40 bg-primary/5"}
            role="button"
            tabIndex={0}
            onClick={() => handleClick(n)}
            onKeyDown={(e) => e.key === "Enter" && handleClick(n)}
          >
            <CardContent className="flex cursor-pointer items-center justify-between p-4 text-sm">
              <span>{typeLabels[n.type] ?? n.type}</span>
              <span className="flex items-center gap-3 text-xs text-muted-foreground">
                {!n.read && <Badge>新</Badge>}
                {new Date(n.createdAt).toLocaleString("zh-CN")}
              </span>
            </CardContent>
          </Card>
        ))
      ) : (
        <EmptyState label="暂无通知" />
      )}
    </div>
  )
}

function notificationLink(type: string, resourceId: number | null): string | null {
  if (resourceId == null) return null
  if (type.startsWith("QUESTION_") || type === "COMMENT_ON_QUESTION") {
    return `/questions/${resourceId}`
  }
  if (type.startsWith("PROFESSOR_")) {
    return "/professor/center"
  }
  return null
}

function Conversations() {
  const query = useQuery({
    queryKey: ["conversations"],
    queryFn: () => api.get<ConversationView[]>("/messages/conversations"),
  })

  if (query.isLoading) return <LoadingState />
  if (!query.data?.length) return <EmptyState label="暂无私信" />

  return (
    <div className="space-y-3">
      {query.data.map((c) => (
        <ConversationItem key={c.peerId} conversation={c} />
      ))}
    </div>
  )
}

function ConversationItem({ conversation }: { conversation: ConversationView }) {
  const queryClient = useQueryClient()
  const [content, setContent] = useState("")
  const messages = useQuery({
    queryKey: ["messages", conversation.peerId],
    queryFn: () => api.get<PageResponse<MessageView>>(`/messages/with/${conversation.peerId}?size=50`),
  })
  const send = useMutation({
    mutationFn: () => api.post("/messages", { toUserId: conversation.peerId, content }),
    onSuccess: () => {
      setContent("")
      queryClient.invalidateQueries({ queryKey: ["messages", conversation.peerId] })
    },
  })

  return (
    <Card>
      <CardContent className="space-y-3 p-5">
        <p className="font-medium">{conversation.peerName}</p>
        <div className="max-h-64 space-y-2 overflow-y-auto rounded-md border p-3 text-sm">
          {[...(messages.data?.list ?? [])].reverse().map((m) => (
            <p key={m.id}>{m.content}</p>
          ))}
        </div>
        <div className="flex gap-2">
          <Input value={content} onChange={(e) => setContent(e.target.value)} placeholder="输入消息" />
          <Button onClick={() => send.mutate()} disabled={!content || send.isPending}>
            发送
          </Button>
        </div>
      </CardContent>
    </Card>
  )
}
