import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { MessageSquare, Reply, Trash2 } from "lucide-react"
import { api } from "@/lib/api"
import type { CommentTargetType, CommentView } from "@/lib/types"
import { useAuth } from "@/lib/auth"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { cn } from "@/lib/utils"

export function CommentSection({
  targetType,
  targetId,
  title = "评论",
}: {
  targetType: CommentTargetType
  targetId: number
  title?: string
}) {
  const { user } = useAuth()
  const queryClient = useQueryClient()
  const [content, setContent] = useState("")
  const [replyTo, setReplyTo] = useState<number | null>(null)
  const [replyContent, setReplyContent] = useState("")

  const query = useQuery({
    queryKey: ["comments", targetType, targetId],
    queryFn: () =>
      api.get<CommentView[]>(`/comments?targetType=${targetType}&targetId=${targetId}`),
  })

  const invalidate = () => {
    queryClient.invalidateQueries({ queryKey: ["comments", targetType, targetId] })
    queryClient.invalidateQueries({ queryKey: ["question"] })
  }

  const create = useMutation({
    mutationFn: (payload: { content: string; parentId?: number }) =>
      api.post("/comments", { targetType, targetId, ...payload }),
    onSuccess: () => {
      setContent("")
      setReplyContent("")
      setReplyTo(null)
      invalidate()
    },
  })

  const remove = useMutation({
    mutationFn: (id: number) => api.del(`/comments/${id}`),
    onSuccess: invalidate,
  })

  const count = countComments(query.data ?? [])

  return (
    <div className="space-y-4">
      <h3 className="flex items-center gap-2 text-sm font-semibold">
        <MessageSquare className="size-4" />
        {title}（{count}）
      </h3>

      {query.data?.map((comment) => (
        <CommentItem
          key={comment.id}
          comment={comment}
          currentUserId={user?.id}
          isAdmin={user?.role === "ADMIN"}
          replyTo={replyTo}
          replyContent={replyContent}
          onReplyToggle={(id) => {
            setReplyTo(replyTo === id ? null : id)
            setReplyContent("")
          }}
          onReplyContent={setReplyContent}
          onSubmitReply={() => create.mutate({ content: replyContent, parentId: comment.id })}
          onDelete={(id) => remove.mutate(id)}
          submitting={create.isPending}
        />
      ))}

      {query.data?.length === 0 && (
        <p className="text-sm text-muted-foreground">还没有评论，来抢沙发</p>
      )}

      {user ? (
        <form
          className="space-y-2"
          onSubmit={(e) => {
            e.preventDefault()
            create.mutate({ content })
          }}
        >
          <Textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="写下你的评论..."
            className="min-h-16"
            required
          />
          <Button type="submit" size="sm" disabled={!content || create.isPending}>
            发表评论
          </Button>
        </form>
      ) : (
        <p className="text-sm text-muted-foreground">登录后可评论</p>
      )}
    </div>
  )
}

function CommentItem({
  comment,
  currentUserId,
  isAdmin,
  replyTo,
  replyContent,
  onReplyToggle,
  onReplyContent,
  onSubmitReply,
  onDelete,
  submitting,
}: {
  comment: CommentView
  currentUserId?: number
  isAdmin: boolean
  replyTo: number | null
  replyContent: string
  onReplyToggle: (id: number) => void
  onReplyContent: (value: string) => void
  onSubmitReply: () => void
  onDelete: (id: number) => void
  submitting: boolean
}) {
  const canDelete = currentUserId === comment.userId || isAdmin
  return (
    <div className="space-y-2 rounded-lg border border-border p-3">
      <div className="flex items-center justify-between text-xs text-muted-foreground">
        <span className="font-medium text-foreground">{comment.userName}</span>
        <span>{new Date(comment.createdAt).toLocaleString("zh-CN")}</span>
      </div>
      <p className="whitespace-pre-wrap text-sm">{comment.content}</p>
      <div className="flex gap-2">
        {currentUserId && (
          <Button variant="ghost" size="sm" onClick={() => onReplyToggle(comment.id)}>
            <Reply />
            回复
          </Button>
        )}
        {canDelete && (
          <Button variant="ghost" size="sm" onClick={() => onDelete(comment.id)}>
            <Trash2 />
            删除
          </Button>
        )}
      </div>

      {replyTo === comment.id && (
        <div className="space-y-2 pl-4">
          <Textarea
            value={replyContent}
            onChange={(e) => onReplyContent(e.target.value)}
            placeholder={`回复 ${comment.userName}`}
            className="min-h-14"
          />
          <Button size="sm" onClick={onSubmitReply} disabled={!replyContent || submitting}>
            回复
          </Button>
        </div>
      )}

      {comment.replies.length > 0 && (
        <div className={cn("space-y-2 border-l-2 border-border pl-4")}>
          {comment.replies.map((reply) => (
            <CommentItem
              key={reply.id}
              comment={reply}
              currentUserId={currentUserId}
              isAdmin={isAdmin}
              replyTo={replyTo}
              replyContent={replyContent}
              onReplyToggle={onReplyToggle}
              onReplyContent={onReplyContent}
              onSubmitReply={onSubmitReply}
              onDelete={onDelete}
              submitting={submitting}
            />
          ))}
        </div>
      )}
    </div>
  )
}

function countComments(comments: CommentView[]): number {
  return comments.reduce((sum, c) => sum + 1 + countComments(c.replies), 0)
}
