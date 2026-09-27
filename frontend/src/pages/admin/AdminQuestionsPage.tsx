import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Link } from "react-router-dom"
import { api, type PageResponse } from "@/lib/api"
import type { QuestionCard } from "@/lib/types"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { LoadingState } from "@/components/ui/spinner"

const statuses = ["", "AUDITING", "NORMAL", "REJECTED", "FORBIDDEN"] as const

export function AdminQuestionsPage() {
  const [status, setStatus] = useState<string>("AUDITING")
  const queryClient = useQueryClient()

  const query = useQuery({
    queryKey: ["admin", "questions", status],
    queryFn: () =>
      api.get<PageResponse<QuestionCard>>(
        `/admin/questions?size=30${status ? `&status=${status}` : ""}`,
      ),
  })

  const audit = useMutation({
    mutationFn: ({ id, target }: { id: number; target: string }) =>
      api.post("/admin/questions/audit", { questionId: id, status: target }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin", "questions"] }),
  })

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">问题审核</h1>
        <div className="flex gap-2">
          {statuses.map((s) => (
            <Button
              key={s || "all"}
              size="sm"
              variant={status === s ? "default" : "outline"}
              onClick={() => setStatus(s)}
            >
              {s || "全部"}
            </Button>
          ))}
        </div>
      </div>

      {query.isLoading ? (
        <LoadingState />
      ) : (
        <div className="space-y-3">
          {query.data?.list.map((q) => (
            <Card key={q.id}>
              <CardContent className="flex items-center justify-between gap-4 p-4">
                <div className="min-w-0">
                  <Link to={`/questions/${q.id}`} className="font-medium hover:text-primary">
                    {q.title}
                  </Link>
                  <p className="truncate text-sm text-muted-foreground">{q.description}</p>
                  <p className="text-xs text-muted-foreground">
                    {q.authorName} · {new Date(q.createdAt).toLocaleString("zh-CN")}
                  </p>
                </div>
                <div className="flex shrink-0 items-center gap-2">
                  <Badge variant="outline">{q.status}</Badge>
                  {q.status !== "NORMAL" && (
                    <Button size="sm" onClick={() => audit.mutate({ id: q.id, target: "NORMAL" })}>
                      通过
                    </Button>
                  )}
                  {q.status !== "REJECTED" && (
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => audit.mutate({ id: q.id, target: "REJECTED" })}
                    >
                      驳回
                    </Button>
                  )}
                  {q.status !== "FORBIDDEN" && (
                    <Button
                      size="sm"
                      variant="destructive"
                      onClick={() => audit.mutate({ id: q.id, target: "FORBIDDEN" })}
                    >
                      禁止
                    </Button>
                  )}
                </div>
              </CardContent>
            </Card>
          ))}
          {query.data?.list.length === 0 && (
            <p className="py-10 text-center text-sm text-muted-foreground">暂无问题</p>
          )}
        </div>
      )}
    </div>
  )
}
