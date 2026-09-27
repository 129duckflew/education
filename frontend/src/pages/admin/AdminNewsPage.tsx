import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Trash2 } from "lucide-react"
import { api, type PageResponse } from "@/lib/api"
import type { NewsView } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { LoadingState } from "@/components/ui/spinner"

export function AdminNewsPage() {
  const queryClient = useQueryClient()
  const [form, setForm] = useState({ title: "", content: "", priority: 0, indexShow: false })

  const query = useQuery({
    queryKey: ["admin", "news"],
    queryFn: () => api.get<PageResponse<NewsView>>("/news?size=50"),
  })

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ["admin", "news"] })

  const create = useMutation({
    mutationFn: () => api.post("/admin/news", form),
    onSuccess: () => {
      setForm({ title: "", content: "", priority: 0, indexShow: false })
      invalidate()
    },
  })
  const remove = useMutation({
    mutationFn: (id: number) => api.del(`/admin/news/${id}`),
    onSuccess: invalidate,
  })

  return (
    <div className="space-y-5">
      <h1 className="text-xl font-semibold">资讯管理</h1>

      <Card>
        <CardContent className="space-y-3 p-4">
          <Input
            value={form.title}
            onChange={(e) => setForm({ ...form, title: e.target.value })}
            placeholder="标题"
          />
          <Textarea
            value={form.content}
            onChange={(e) => setForm({ ...form, content: e.target.value })}
            placeholder="内容"
          />
          <div className="flex items-center gap-4">
            <label className="flex items-center gap-2 text-sm">
              优先级
              <Input
                type="number"
                className="w-20"
                value={form.priority}
                onChange={(e) => setForm({ ...form, priority: Number(e.target.value) })}
              />
            </label>
            <label className="flex items-center gap-2 text-sm">
              <input
                type="checkbox"
                checked={form.indexShow}
                onChange={(e) => setForm({ ...form, indexShow: e.target.checked })}
              />
              首页展示
            </label>
            <Button onClick={() => create.mutate()} disabled={!form.title || create.isPending}>
              发布
            </Button>
          </div>
        </CardContent>
      </Card>

      {query.isLoading ? (
        <LoadingState />
      ) : (
        <div className="space-y-3">
          {query.data?.list.map((n) => (
            <Card key={n.id}>
              <CardContent className="flex items-center justify-between p-4">
                <div>
                  <p className="font-medium">{n.title}</p>
                  <p className="text-xs text-muted-foreground">
                    优先级 {n.priority} · {n.indexShow ? "首页展示" : "不展示"}
                  </p>
                </div>
                <Button variant="ghost" size="icon" onClick={() => remove.mutate(n.id)}>
                  <Trash2 className="size-4 text-destructive" />
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
