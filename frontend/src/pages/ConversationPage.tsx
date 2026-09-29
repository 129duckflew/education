import { useEffect, useMemo, useRef, useState } from "react"
import { Link, useParams } from "react-router-dom"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { ArrowLeft, Send } from "lucide-react"
import { api } from "@/lib/api"
import type { ConversationView, MessagePage, MessageView } from "@/lib/types"
import { useAuth } from "@/lib/auth"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { FileUpload } from "@/components/common/FileUpload"
import { LoadingState } from "@/components/ui/spinner"
import { cn } from "@/lib/utils"

export function ConversationPage() {
  const { conversationId } = useParams()
  const id = Number(conversationId)
  const { user } = useAuth()
  const queryClient = useQueryClient()
  const [content, setContent] = useState("")
  const bottomRef = useRef<HTMLDivElement>(null)

  const query = useQuery({
    queryKey: ["conversation", id],
    queryFn: () => api.get<MessagePage>(`/conversations/${id}/messages?size=30`),
    refetchInterval: 15_000,
    enabled: Number.isFinite(id),
  })

  const conversations = useQuery({
    queryKey: ["conversations"],
    queryFn: () => api.get<ConversationView[]>("/conversations"),
  })
  const peerName = useMemo(
    () => conversations.data?.find((c) => c.id === id)?.peerName,
    [conversations.data, id],
  )

  const messages = query.data?.list ?? []
  const lastId = messages.length ? messages[messages.length - 1].id : null

  // 打开会话即标记已读
  useEffect(() => {
    if (!lastId || lastId <= 0) return
    api
      .post(`/conversations/${id}/read`, { upToMessageId: lastId })
      .then(() => queryClient.invalidateQueries({ queryKey: ["conversations"] }))
      .catch(() => undefined)
  }, [id, lastId, queryClient])

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" })
  }, [lastId])

  const send = useMutation({
    mutationFn: (payload: {
      content?: string
      type?: string
      fileId?: number
      clientMsgId: string
    }) => api.post<MessageView>(`/conversations/${id}/messages`, payload),
    onMutate: async (payload) => {
      await queryClient.cancelQueries({ queryKey: ["conversation", id] })
      const previous = queryClient.getQueryData<MessagePage>(["conversation", id])
      const optimistic: MessageView = {
        id: -Date.now(),
        conversationId: id,
        senderId: user?.id ?? 0,
        senderName: user?.nickname ?? user?.realName ?? "我",
        type: (payload.type as MessageView["type"]) ?? "TEXT",
        content: payload.content ?? null,
        file: null,
        replyToId: null,
        clientMsgId: payload.clientMsgId,
        status: "NORMAL",
        createdAt: new Date().toISOString(),
      }
      queryClient.setQueryData<MessagePage>(["conversation", id], (old) => ({
        list: [...(old?.list ?? []), optimistic],
        hasMore: old?.hasMore ?? false,
      }))
      return { previous }
    },
    onError: (_error, _payload, context) => {
      if (context) {
        queryClient.setQueryData(["conversation", id], context.previous)
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ["conversation", id] })
      queryClient.invalidateQueries({ queryKey: ["conversations"] })
    },
  })

  const older = useMutation({
    mutationFn: (beforeId: number) =>
      api.get<MessagePage>(`/conversations/${id}/messages?beforeId=${beforeId}&size=30`),
    onSuccess: (page) => {
      queryClient.setQueryData<MessagePage>(["conversation", id], (old) => ({
        list: [...page.list, ...(old?.list ?? [])],
        hasMore: page.hasMore,
      }))
    },
  })

  const recall = useMutation({
    mutationFn: (messageId: number) =>
      api.post(`/conversations/${id}/messages/${messageId}/recall`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["conversation", id] })
      queryClient.invalidateQueries({ queryKey: ["conversations"] })
    },
  })

  function submitText() {
    const text = content.trim()
    if (!text) return
    send.mutate({ content: text, type: "TEXT", clientMsgId: crypto.randomUUID() })
    setContent("")
  }

  if (query.isLoading) return <LoadingState />

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2">
        <Button variant="ghost" size="icon" asChild>
          <Link to="/messages" title="返回">
            <ArrowLeft />
          </Link>
        </Button>
        <h1 className="text-lg font-semibold">{peerName ?? "私信"}</h1>
      </div>

      <div className="min-h-[45vh] space-y-3 overflow-y-auto rounded-lg border border-border p-4">
        {query.data?.hasMore && (
          <div className="flex justify-center">
            <Button
              variant="outline"
              size="sm"
              disabled={older.isPending}
              onClick={() => older.mutate(messages[0].id)}
            >
              加载更早
            </Button>
          </div>
        )}
        {messages.length === 0 && (
          <p className="py-8 text-center text-sm text-muted-foreground">还没有消息，打个招呼吧</p>
        )}
        {messages.map((message) => (
          <MessageBubble
            key={message.id}
            message={message}
            mine={message.senderId === user?.id}
            onRecall={() => recall.mutate(message.id)}
          />
        ))}
        <div ref={bottomRef} />
      </div>

      <form
        className="flex items-center gap-2"
        onSubmit={(e) => {
          e.preventDefault()
          submitText()
        }}
      >
        <FileUpload
          accept="image/*"
          label="图片"
          onChange={(file) =>
            send.mutate({ type: "IMAGE", fileId: file.id, content: file.originalName, clientMsgId: crypto.randomUUID() })
          }
        />
        <FileUpload
          label="文件"
          onChange={(file) =>
            send.mutate({ type: "FILE", fileId: file.id, content: file.originalName, clientMsgId: crypto.randomUUID() })
          }
        />
        <Input
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="输入消息"
          className="flex-1"
        />
        <Button type="submit" disabled={!content.trim() || send.isPending}>
          <Send />
          发送
        </Button>
      </form>
    </div>
  )
}

function MessageBubble({
  message,
  mine,
  onRecall,
}: {
  message: MessageView
  mine: boolean
  onRecall: () => void
}) {
  const recalled = message.status === "RECALLED"
  const pending = message.id < 0
  return (
    <div className={cn("flex", mine ? "justify-end" : "justify-start")}>
      <div className="flex max-w-[78%] items-end gap-2">
        {mine && !recalled && !pending && (
          <button
            type="button"
            onClick={onRecall}
            className="text-xs text-muted-foreground hover:text-destructive"
          >
            撤回
          </button>
        )}
        <div
          className={cn(
            "rounded-lg px-3 py-2 text-sm",
            mine ? "bg-primary text-primary-foreground" : "bg-muted",
            pending && "opacity-60",
          )}
        >
          {!mine && <p className="mb-1 text-xs opacity-70">{message.senderName}</p>}
          {recalled ? (
            <span className="italic opacity-70">[已撤回]</span>
          ) : message.type === "IMAGE" && message.file ? (
            <img src={message.file.url} alt={message.file.originalName} className="max-h-60 rounded" />
          ) : message.type === "FILE" && message.file ? (
            <a href={message.file.url} target="_blank" rel="noreferrer" className="underline">
              {message.file.originalName}
            </a>
          ) : (
            <p className="whitespace-pre-wrap break-words">{message.content}</p>
          )}
          <span className="mt-1 block text-right text-[10px] opacity-60">
            {pending ? "发送中..." : new Date(message.createdAt).toLocaleTimeString("zh-CN")}
          </span>
        </div>
      </div>
    </div>
  )
}
