import { useState, type FormEvent } from "react"
import { useNavigate } from "react-router-dom"
import { useMutation, useQuery } from "@tanstack/react-query"
import { api, type PageResponse } from "@/lib/api"
import type { ConsultArea, PayOrder, ProfessorSummary } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { LoadingState } from "@/components/ui/spinner"
import { cn } from "@/lib/utils"

export function AskQuestionPage() {
  const navigate = useNavigate()
  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")
  const [paid, setPaid] = useState(false)
  const [areaIds, setAreaIds] = useState<number[]>([])
  const [professorIds, setProfessorIds] = useState<number[]>([])
  const [error, setError] = useState<string | null>(null)

  const areas = useQuery({
    queryKey: ["areas", "flat"],
    queryFn: () => api.get<ConsultArea[]>("/public/areas/flat"),
  })
  const professors = useQuery({
    queryKey: ["professors", "all"],
    queryFn: () => api.get<PageResponse<ProfessorSummary>>("/professors?size=100"),
  })

  const mutation = useMutation({
    mutationFn: async (): Promise<PayOrder | undefined> => {
      if (paid) {
        if (professorIds.length !== 1) {
          throw new Error("付费提问只能选择一位教授")
        }
        return api.post<PayOrder>("/questions/paid", {
          title,
          description,
          areaIds,
          professorId: professorIds[0],
        })
      }
      await api.post("/questions", { title, description, areaIds, professorIds })
      return undefined
    },
    onSuccess: (result) => {
      if (paid && result) {
        navigate(`/profile?tab=orders&pay=${result.id}`)
      } else {
        navigate("/questions")
      }
    },
    onError: (err) => setError(err instanceof Error ? err.message : "提交失败"),
  })

  function toggle(list: number[], id: number, setter: (v: number[]) => void, single = false) {
    if (single) {
      setter(list.includes(id) ? [] : [id])
      return
    }
    setter(list.includes(id) ? list.filter((x) => x !== id) : [...list, id])
  }

  function onSubmit(event: FormEvent) {
    event.preventDefault()
    setError(null)
    if (areaIds.length === 0) {
      setError("请至少选择一个领域")
      return
    }
    if (professorIds.length === 0) {
      setError("请至少选择一位教授")
      return
    }
    mutation.mutate()
  }

  return (
    <div className="mx-auto max-w-3xl">
      <Card>
        <CardHeader>
          <CardTitle>发起提问</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="space-y-5" onSubmit={onSubmit}>
            <div className="space-y-2">
              <Label htmlFor="title">标题</Label>
              <Input id="title" value={title} onChange={(e) => setTitle(e.target.value)} required />
            </div>
            <div className="space-y-2">
              <Label htmlFor="description">问题描述</Label>
              <Textarea
                id="description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                className="min-h-32"
                required
              />
            </div>

            <div className="space-y-2">
              <Label>咨询领域</Label>
              <div className="flex flex-wrap gap-2">
                {areas.data?.map((area) => (
                  <button
                    key={area.id}
                    type="button"
                    onClick={() => toggle(areaIds, area.id, setAreaIds)}
                    className={cn(
                      "rounded-md border px-3 py-1 text-sm transition-colors",
                      areaIds.includes(area.id)
                        ? "border-primary bg-primary text-primary-foreground"
                        : "hover:bg-accent",
                    )}
                  >
                    {area.name}
                  </button>
                ))}
              </div>
            </div>

            <div className="space-y-2">
              <Label>选择教授 {paid && "（付费提问仅限一位）"}</Label>
              <div className="grid max-h-56 grid-cols-2 gap-2 overflow-y-auto rounded-md border p-3 md:grid-cols-3">
                {professors.data?.list.map((p) => (
                  <button
                    key={p.userId}
                    type="button"
                    onClick={() => toggle(professorIds, p.userId, setProfessorIds, paid)}
                    className={cn(
                      "rounded-md border px-2 py-1 text-left text-sm transition-colors",
                      professorIds.includes(p.userId)
                        ? "border-primary bg-primary text-primary-foreground"
                        : "hover:bg-accent",
                    )}
                  >
                    {p.realName}
                    <span className="block text-xs opacity-70">{p.jobRankName}</span>
                  </button>
                ))}
                {professors.data?.list.length === 0 && (
                  <p className="col-span-full text-sm text-muted-foreground">暂无教授</p>
                )}
              </div>
            </div>

            <label className="flex items-center gap-2 text-sm">
              <input type="checkbox" checked={paid} onChange={(e) => setPaid(e.target.checked)} />
              付费咨询
            </label>

            {error && <p className="text-sm text-destructive">{error}</p>}
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending ? "提交中..." : paid ? "生成订单" : "提交问题"}
            </Button>
          </form>
        </CardContent>
      </Card>
      {areas.isLoading && <LoadingState />}
    </div>
  )
}
