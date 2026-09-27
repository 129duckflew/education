import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { api, type PageResponse } from "@/lib/api"
import type { UserProfile } from "@/lib/types"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { LoadingState } from "@/components/ui/spinner"

export function AdminUsersPage() {
  const [keyword, setKeyword] = useState("")
  const [submitted, setSubmitted] = useState("")
  const queryClient = useQueryClient()

  const query = useQuery({
    queryKey: ["admin", "users", submitted],
    queryFn: () =>
      api.get<PageResponse<UserProfile>>(
        `/admin/users?size=30${submitted ? `&keyword=${encodeURIComponent(submitted)}` : ""}`,
      ),
  })

  const toggle = useMutation({
    mutationFn: ({ id, enabled }: { id: number; enabled: boolean }) =>
      api.patch(`/admin/users/${id}/enabled?enabled=${!enabled}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin", "users"] }),
  })

  return (
    <div className="space-y-5">
      <h1 className="text-xl font-semibold">用户管理</h1>
      <div className="flex gap-2">
        <Input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && setSubmitted(keyword)}
          placeholder="搜索用户名 / 昵称 / 真名"
        />
        <Button onClick={() => setSubmitted(keyword)}>搜索</Button>
      </div>

      {query.isLoading ? (
        <LoadingState />
      ) : (
        <div className="space-y-3">
          {query.data?.list.map((u) => (
            <Card key={u.id}>
              <CardContent className="flex items-center justify-between p-4">
                <div>
                  <p className="font-medium">
                    {u.nickname ?? u.username} <Badge variant="outline">{u.role}</Badge>
                  </p>
                  <p className="text-xs text-muted-foreground">
                    {u.email ?? u.phone ?? u.username}
                  </p>
                </div>
                <Button
                  size="sm"
                  variant={u.enabled ? "destructive" : "default"}
                  onClick={() => toggle.mutate({ id: u.id, enabled: u.enabled })}
                >
                  {u.enabled ? "禁用" : "启用"}
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
