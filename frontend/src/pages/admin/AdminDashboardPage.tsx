import { useQuery } from "@tanstack/react-query"
import { api, type PageResponse } from "@/lib/api"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

export function AdminDashboardPage() {
  const questions = useQuery({
    queryKey: ["admin", "questions", "count"],
    queryFn: () => api.get<PageResponse<unknown>>("/admin/questions?size=1"),
  })
  const users = useQuery({
    queryKey: ["admin", "users", "count"],
    queryFn: () => api.get<PageResponse<unknown>>("/admin/users?size=1"),
  })
  const pending = useQuery({
    queryKey: ["admin", "professors", "pending"],
    queryFn: () => api.get<PageResponse<unknown>>("/admin/professors?approved=false&size=1"),
  })

  const stats = [
    { label: "问题总数", value: questions.data?.total },
    { label: "用户总数", value: users.data?.total },
    { label: "待审核教授", value: pending.data?.total },
  ]

  return (
    <div className="space-y-6">
      <h1 className="text-xl font-semibold">仪表盘</h1>
      <div className="grid gap-4 md:grid-cols-3">
        {stats.map((stat) => (
          <Card key={stat.label}>
            <CardHeader>
              <CardTitle className="text-sm text-muted-foreground">{stat.label}</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-3xl font-bold">{stat.value ?? "-"}</p>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  )
}
