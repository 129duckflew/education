import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Trash2 } from "lucide-react"
import { api, type PageResponse } from "@/lib/api"
import type { ResearchDirection, University, UniversityMajor } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { LoadingState } from "@/components/ui/spinner"

type Tab = "universities" | "majors" | "directions"

export function AdminTaxonomyPage() {
  const [tab, setTab] = useState<Tab>("universities")
  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">学校 / 专业 / 方向</h1>
        <div className="flex gap-2">
          {(
            [
              ["universities", "学校"],
              ["majors", "专业"],
              ["directions", "研究方向"],
            ] as [Tab, string][]
          ).map(([key, label]) => (
            <Button
              key={key}
              size="sm"
              variant={tab === key ? "default" : "outline"}
              onClick={() => setTab(key)}
            >
              {label}
            </Button>
          ))}
        </div>
      </div>
      {tab === "universities" && <Universities />}
      {tab === "majors" && <Majors />}
      {tab === "directions" && <Directions />}
    </div>
  )
}

function Universities() {
  const queryClient = useQueryClient()
  const [form, setForm] = useState({ name: "", province: "", city: "", level: "" })
  const query = useQuery({
    queryKey: ["taxonomy", "universities"],
    queryFn: () => api.get<PageResponse<University>>("/public/taxonomy/universities?size=50"),
  })
  const invalidate = () => queryClient.invalidateQueries({ queryKey: ["taxonomy", "universities"] })
  const create = useMutation({
    mutationFn: () => api.post("/admin/taxonomy/universities", form),
    onSuccess: () => {
      setForm({ name: "", province: "", city: "", level: "" })
      invalidate()
    },
  })
  const remove = useMutation({
    mutationFn: (id: number) => api.del(`/admin/taxonomy/universities/${id}`),
    onSuccess: invalidate,
  })

  return (
    <div className="space-y-4">
      <Card>
        <CardContent className="grid gap-2 p-4 md:grid-cols-5">
          <Input
            placeholder="学校名称"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
          />
          <Input
            placeholder="省份"
            value={form.province}
            onChange={(e) => setForm({ ...form, province: e.target.value })}
          />
          <Input
            placeholder="城市"
            value={form.city}
            onChange={(e) => setForm({ ...form, city: e.target.value })}
          />
          <Input
            placeholder="办学层次"
            value={form.level}
            onChange={(e) => setForm({ ...form, level: e.target.value })}
          />
          <Button onClick={() => create.mutate()} disabled={!form.name || create.isPending}>
            新增
          </Button>
        </CardContent>
      </Card>
      {query.isLoading ? (
        <LoadingState />
      ) : (
        <Card>
          <CardContent className="divide-y p-0">
            {query.data?.list.map((u) => (
              <div key={u.id} className="flex items-center justify-between px-4 py-2 text-sm">
                <span>
                  {u.name}
                  <span className="ml-2 text-muted-foreground">
                    {u.province} {u.city} {u.level}
                  </span>
                </span>
                <Button variant="ghost" size="icon" onClick={() => remove.mutate(u.id)}>
                  <Trash2 className="size-4 text-destructive" />
                </Button>
              </div>
            ))}
            {query.data?.list.length === 0 && (
              <p className="p-4 text-sm text-muted-foreground">暂无数据</p>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  )
}

function Majors() {
  const queryClient = useQueryClient()
  const [form, setForm] = useState({ code: "", name: "" })
  const query = useQuery({
    queryKey: ["taxonomy", "majors"],
    queryFn: () => api.get<UniversityMajor[]>("/public/taxonomy/majors"),
  })
  const invalidate = () => queryClient.invalidateQueries({ queryKey: ["taxonomy", "majors"] })
  const create = useMutation({
    mutationFn: () => api.post("/admin/taxonomy/majors", { ...form, parentId: null }),
    onSuccess: () => {
      setForm({ code: "", name: "" })
      invalidate()
    },
  })
  const remove = useMutation({
    mutationFn: (id: number) => api.del(`/admin/taxonomy/majors/${id}`),
    onSuccess: invalidate,
  })

  return (
    <div className="space-y-4">
      <Card>
        <CardContent className="grid gap-2 p-4 md:grid-cols-3">
          <Input
            placeholder="专业代码"
            value={form.code}
            onChange={(e) => setForm({ ...form, code: e.target.value })}
          />
          <Input
            placeholder="专业名称"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
          />
          <Button onClick={() => create.mutate()} disabled={!form.name || create.isPending}>
            新增
          </Button>
        </CardContent>
      </Card>
      {query.isLoading ? (
        <LoadingState />
      ) : (
        <Card>
          <CardContent className="divide-y p-0">
            {query.data?.map((m) => (
              <div key={m.id} className="flex items-center justify-between px-4 py-2 text-sm">
                <span>
                  {m.name}
                  <span className="ml-2 text-muted-foreground">{m.code}</span>
                </span>
                <Button variant="ghost" size="icon" onClick={() => remove.mutate(m.id)}>
                  <Trash2 className="size-4 text-destructive" />
                </Button>
              </div>
            ))}
            {query.data?.length === 0 && <p className="p-4 text-sm text-muted-foreground">暂无数据</p>}
          </CardContent>
        </Card>
      )}
    </div>
  )
}

function Directions() {
  const queryClient = useQueryClient()
  const [name, setName] = useState("")
  const query = useQuery({
    queryKey: ["taxonomy", "directions"],
    queryFn: () => api.get<ResearchDirection[]>("/public/taxonomy/directions"),
  })
  const invalidate = () => queryClient.invalidateQueries({ queryKey: ["taxonomy", "directions"] })
  const create = useMutation({
    mutationFn: () => api.post("/admin/taxonomy/directions", { name, majorId: null }),
    onSuccess: () => {
      setName("")
      invalidate()
    },
  })
  const remove = useMutation({
    mutationFn: (id: number) => api.del(`/admin/taxonomy/directions/${id}`),
    onSuccess: invalidate,
  })

  return (
    <div className="space-y-4">
      <Card>
        <CardContent className="grid gap-2 p-4 md:grid-cols-3">
          <Input
            placeholder="研究方向名称"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          <Button onClick={() => create.mutate()} disabled={!name || create.isPending}>
            新增
          </Button>
        </CardContent>
      </Card>
      {query.isLoading ? (
        <LoadingState />
      ) : (
        <Card>
          <CardContent className="divide-y p-0">
            {query.data?.map((d) => (
              <div key={d.id} className="flex items-center justify-between px-4 py-2 text-sm">
                <span>{d.name}</span>
                <Button variant="ghost" size="icon" onClick={() => remove.mutate(d.id)}>
                  <Trash2 className="size-4 text-destructive" />
                </Button>
              </div>
            ))}
            {query.data?.length === 0 && <p className="p-4 text-sm text-muted-foreground">暂无数据</p>}
          </CardContent>
        </Card>
      )}
    </div>
  )
}
