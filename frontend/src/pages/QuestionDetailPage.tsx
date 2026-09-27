import { useState, type FormEvent } from "react"
import { useParams } from "react-router-dom"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Heart, MessageSquare, Star } from "lucide-react"
import { api } from "@/lib/api"
import type { AnswerView, QuestionDetail } from "@/lib/types"
import { useAuth } from "@/lib/auth"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Textarea } from "@/components/ui/textarea"
import { LoadingState } from "@/components/ui/spinner"
import { cn } from "@/lib/utils"

export function QuestionDetailPage() {
  const { id } = useParams()
  const questionId = Number(id)
  const { user } = useAuth()
  const queryClient = useQueryClient()

  const query = useQuery({
    queryKey: ["question", questionId],
    queryFn: () => api.get<QuestionDetail>(`/questions/${questionId}`),
  })

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ["question", questionId] })

  const toggleQuestionLike = useMutation({
    mutationFn: (liked: boolean) =>
      liked
        ? api.del(`/questions/${questionId}/like`)
        : api.post(`/questions/${questionId}/like`),
    onSuccess: invalidate,
  })

  if (query.isLoading) return <LoadingState />
  if (query.isError || !query.data) {
    return <p className="py-16 text-center text-muted-foreground">问题不存在或无权查看</p>
  }

  const { question, answers } = query.data

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <div className="flex items-start justify-between gap-4">
            <CardTitle className="text-xl">{question.title}</CardTitle>
            <Button
              variant={question.liked ? "default" : "outline"}
              size="sm"
              disabled={!user}
              onClick={() => toggleQuestionLike.mutate(question.liked)}
            >
              <Heart className={cn(question.liked && "fill-current")} />
              {question.likeCount}
            </Button>
          </div>
          <div className="flex flex-wrap gap-2">
            {question.areaNames.map((name) => (
              <Badge key={name} variant="outline">
                {name}
              </Badge>
            ))}
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <p className="whitespace-pre-wrap text-sm leading-relaxed">{question.description}</p>
          <p className="text-xs text-muted-foreground">
            提问者：{question.authorName} · {new Date(question.createdAt).toLocaleString("zh-CN")}
          </p>
        </CardContent>
      </Card>

      <div className="space-y-3">
        <h2 className="flex items-center gap-2 text-lg font-semibold">
          <MessageSquare className="size-4" />
          回答（{answers.length}）
        </h2>
        {answers.map((answer) => (
          <AnswerItem key={answer.id} answer={answer} onChanged={invalidate} canInteract={!!user} />
        ))}
        {answers.length === 0 && (
          <p className="py-6 text-center text-sm text-muted-foreground">暂无回答</p>
        )}
      </div>

      {user?.role === "PROFESSOR" && (
        <AnswerForm questionId={questionId} onSubmitted={invalidate} />
      )}
    </div>
  )
}

function AnswerItem({
  answer,
  onChanged,
  canInteract,
}: {
  answer: AnswerView
  onChanged: () => void
  canInteract: boolean
}) {
  const like = useMutation({
    mutationFn: () =>
      answer.liked ? api.del(`/answers/${answer.id}/like`) : api.post(`/answers/${answer.id}/like`),
    onSuccess: onChanged,
  })
  const collect = useMutation({
    mutationFn: () =>
      answer.collected
        ? api.del(`/answers/${answer.id}/collect`)
        : api.post(`/answers/${answer.id}/collect`),
    onSuccess: onChanged,
  })

  return (
    <Card>
      <CardContent className="space-y-3 p-5">
        <div className="flex items-center justify-between">
          <span className="text-sm font-medium">{answer.professorName}</span>
          <span className="text-xs text-muted-foreground">
            {new Date(answer.createdAt).toLocaleString("zh-CN")}
          </span>
        </div>
        <p className="whitespace-pre-wrap text-sm leading-relaxed">{answer.content}</p>
        <div className="flex gap-2">
          <Button
            variant="ghost"
            size="sm"
            disabled={!canInteract}
            onClick={() => like.mutate()}
            className={cn(answer.liked && "text-primary")}
          >
            <Heart className={cn(answer.liked && "fill-current")} />
            {answer.likeCount}
          </Button>
          <Button
            variant="ghost"
            size="sm"
            disabled={!canInteract}
            onClick={() => collect.mutate()}
            className={cn(answer.collected && "text-primary")}
          >
            <Star className={cn(answer.collected && "fill-current")} />
            收藏 {answer.collectCount}
          </Button>
        </div>
      </CardContent>
    </Card>
  )
}

function AnswerForm({ questionId, onSubmitted }: { questionId: number; onSubmitted: () => void }) {
  const [content, setContent] = useState("")
  const mutation = useMutation({
    mutationFn: () => api.post("/answers", { questionId, content }),
    onSuccess: () => {
      setContent("")
      onSubmitted()
    },
  })

  function onSubmit(event: FormEvent) {
    event.preventDefault()
    mutation.mutate()
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">我来回答</CardTitle>
      </CardHeader>
      <CardContent>
        <form className="space-y-3" onSubmit={onSubmit}>
          <Textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="分享你的建议..."
            required
          />
          {mutation.isError && (
            <p className="text-sm text-destructive">
              {mutation.error instanceof Error ? mutation.error.message : "提交失败"}
            </p>
          )}
          <Button type="submit" disabled={mutation.isPending}>
            提交回答
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}
