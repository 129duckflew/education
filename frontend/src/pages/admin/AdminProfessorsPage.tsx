import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { api, type PageResponse } from "@/lib/api"
import type { ProfessorSummary } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { LoadingState } from "@/components/ui/spinner"
import { cn } from "@/lib/utils"

export function AdminProfessorsPage() {
  const [approved, setApproved] = useState(false)
  const queryClient = useQueryClient()

  const query = useQuery({
    queryKey: ["admin", "professors", approved],
    queryFn: () =>
      api.get<PageResponse<ProfessorSummary>>(`/admin/professors?approved=${approved}&size=30`),
  })

  const act = useMutation({
    mutationFn: ({ id, action }: { id: number; action: "approve" | "reject" }) =>
      api.post(`/admin/professors/${id}/${action}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin", "professors"] }),
  })

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">教授认证</h1>
        <div className="flex gap-2">
          <Button size="sm" variant={!approved ? "default" : "outline"} onClick={() => setApproved(false)}>
            待审核
          </Button>
          <Button size="sm" variant={approved ? "default" : "outline"} onClick={() => setApproved(true)}>
            已通过
          </Button>
        </div>
      </div>

      {query.isLoading ? (
        <LoadingState />
      ) : (
        <div className="space-y-3">
          {query.data?.list.map((p) => (
            <Card key={p.userId}>
              <CardContent className="flex items-center justify-between p-4">
                <div>
                  <p className="font-medium">{p.realName}</p>
                  <p className={cn("text-sm text-muted-foreground")}>
                    {p.jobRankName} · ￥{p.consultPrice}
                  </p>
                  <p className="line-clamp-1 text-xs text-muted-foreground">{p.introduction}</p>
                </div>
                {!approved && (
                  <div className="flex gap-2">
                    <Button size="sm" onClick={() => act.mutate({ id: p.userId, action: "approve" })}>
                      通过
                    </Button>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => act.mutate({ id: p.userId, action: "reject" })}
                    >
                      拒绝
                    </Button>
                  </div>
                )}
              </CardContent>
            </Card>
          ))}
          {query.data?.list.length === 0 && (
            <p className="py-10 text-center text-sm text-muted-foreground">暂无数据</p>
          )}
        </div>
      )}
    </div>
  )
}
