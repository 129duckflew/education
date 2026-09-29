import { useState, type FormEvent } from "react"
import { useNavigate, useParams } from "react-router-dom"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { MessageSquare, Star } from "lucide-react"
import { api } from "@/lib/api"
import type { ProfessorDetail } from "@/lib/types"
import { useAuth } from "@/lib/auth"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Textarea } from "@/components/ui/textarea"
import { LoadingState } from "@/components/ui/spinner"

export function ProfessorDetailPage() {
  const { id } = useParams()
  const professorId = Number(id)
  const { user } = useAuth()
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  const query = useQuery({
    queryKey: ["professor", professorId],
    queryFn: () => api.get<ProfessorDetail>(`/professors/${professorId}`),
  })

  const openConversation = useMutation({
    mutationFn: () => api.post<number>("/conversations", { peerId: professorId }),
    onSuccess: (conversationId) => navigate(`/messages/${conversationId}`),
    onError: (error) =>
      window.alert(error instanceof Error ? error.message : "无法发起私信"),
  })

  if (query.isLoading) return <LoadingState />
  if (query.isError || !query.data) {
    return <p className="py-16 text-center text-muted-foreground">教授不存在</p>
  }

  const { summary, educations, reviews } = query.data

  return (
    <div className="space-y-5">
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle className="text-xl">{summary.realName}</CardTitle>
            <div className="flex items-center gap-3">
              {user && user.id !== professorId && (
                <Button
                  size="sm"
                  variant="outline"
                  disabled={openConversation.isPending}
                  onClick={() => openConversation.mutate()}
                >
                  <MessageSquare />
                  私信 TA
                </Button>
              )}
              <span className="flex items-center gap-1 text-sm text-muted-foreground">
                <Star className="size-4 fill-current text-amber-500" />
                {summary.rating.toFixed(1)}（{summary.reviewCount} 条评价）
              </span>
            </div>
          </div>
          <p className="text-sm text-muted-foreground">{summary.jobRankName}</p>
        </CardHeader>
        <CardContent className="space-y-3">
          <div className="flex flex-wrap gap-1">
            {summary.areaNames.map((name) => (
              <Badge key={name} variant="outline">
                {name}
              </Badge>
            ))}
          </div>
          <p className="whitespace-pre-wrap text-sm leading-relaxed">{summary.introduction}</p>
          <p className="text-sm">付费咨询价：￥{summary.consultPrice}</p>
        </CardContent>
      </Card>

      {educations.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle className="text-base">教育经历</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2 text-sm">
            {educations.map((e) => (
              <div key={e.id} className="flex flex-wrap items-center gap-2">
                <span className="font-medium">{e.schoolName}</span>
                {e.majorName && <span>{e.majorName}</span>}
                {e.degreeName && <Badge variant="secondary">{e.degreeName}</Badge>}
                <span className="text-muted-foreground">
                  {e.startDate} ~ {e.endDate}
                </span>
              </div>
            ))}
          </CardContent>
        </Card>
      )}

      <Card>
        <CardHeader>
          <CardTitle className="text-base">评价（{reviews.length}）</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {reviews.map((r) => (
            <div key={r.id} className="border-b border-border pb-3 last:border-0">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">{r.userName}</span>
                <span className="text-sm text-amber-500">{"★".repeat(r.rating)}</span>
              </div>
              <p className="text-sm text-muted-foreground">{r.content}</p>
            </div>
          ))}
          {reviews.length === 0 && (
            <p className="text-sm text-muted-foreground">暂无评价</p>
          )}
          {user && user.id !== professorId && (
            <ReviewForm
              professorId={professorId}
              onDone={() => queryClient.invalidateQueries({ queryKey: ["professor", professorId] })}
            />
          )}
        </CardContent>
      </Card>
    </div>
  )
}

function ReviewForm({ professorId, onDone }: { professorId: number; onDone: () => void }) {
  const [rating, setRating] = useState(5)
  const [content, setContent] = useState("")
  const mutation = useMutation({
    mutationFn: () => api.post(`/professors/${professorId}/reviews`, { rating, content }),
    onSuccess: () => {
      setContent("")
      onDone()
    },
  })

  function onSubmit(event: FormEvent) {
    event.preventDefault()
    mutation.mutate()
  }

  return (
    <form className="space-y-3 pt-2" onSubmit={onSubmit}>
      <div className="flex items-center gap-2">
        <span className="text-sm">评分</span>
        <input
          type="range"
          min={0}
          max={10}
          value={rating}
          onChange={(e) => setRating(Number(e.target.value))}
        />
        <span className="text-sm">{rating}</span>
      </div>
      <Textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder="写下你的评价"
      />
      <Button type="submit" size="sm" disabled={mutation.isPending}>
        提交评价
      </Button>
    </form>
  )
}
