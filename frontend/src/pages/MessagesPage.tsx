import { useState } from "react"
import { Link } from "react-router-dom"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { api, type PageResponse } from "@/lib/api"
import type { ConversationView, NotificationView } from "@/lib/types"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
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
  COMMENT_ON_QUESTION: "问题收到评论",
  COMMENT_ON_ANSWER: "回答收到评论",
  REPLY_TO_COMMENT: "评论收到回复",
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

function Conversations() {
  const query = useQuery({
    queryKey: ["conversations"],
    queryFn: () => api.get<ConversationView[]>("/conversations"),
    refetchInterval: 30_000,
  })

  if (query.isLoading) return <LoadingState />
  if (!query.data?.length) {
    return <EmptyState label="暂无私信。可在教授主页点击「私信 TA」发起会话。" />
  }

  return (
    <div className="space-y-3">
      {query.data.map((c) => (
        <Link key={c.id} to={`/messages/${c.id}`}>
          <Card className={c.unread > 0 ? "border-primary/40 bg-primary/5" : ""}>
            <CardContent className="flex items-center justify-between gap-3 p-4">
              <div className="min-w-0">
                <p className="flex items-center gap-2 text-sm font-medium">
                  {c.peerName}
                  {c.pinned && <Badge variant="outline">置顶</Badge>}
                </p>
                <p className="line-clamp-1 text-xs text-muted-foreground">
                  {c.lastMessage ?? "开始对话"}
                </p>
              </div>
              <div className="flex shrink-0 flex-col items-end gap-1 text-xs text-muted-foreground">
                {c.lastAt && <span>{new Date(c.lastAt).toLocaleDateString("zh-CN")}</span>}
                {c.unread > 0 && <Badge>{c.unread > 99 ? "99+" : c.unread}</Badge>}
              </div>
            </CardContent>
          </Card>
        </Link>
      ))}
    </div>
  )
}
