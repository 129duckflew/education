import { useEffect, useState, type FormEvent } from "react"
import { Link } from "react-router-dom"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { api, type PageResponse } from "@/lib/api"
import type { AnswerView, ConsultArea, PayOrder, QuestionCard, UserProfile } from "@/lib/types"
import { useAuth } from "@/lib/auth"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { LoadingState } from "@/components/ui/spinner"
import { cn } from "@/lib/utils"

type Tab = "profile" | "interests" | "questions" | "orders" | "liked"

export function ProfilePage() {
  const { user, refreshUser } = useAuth()
  const [tab, setTab] = useState<Tab>("profile")

  if (!user) return <LoadingState label="请先登录" />

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap gap-2">
        {(
          [
            ["profile", "基本资料"],
            ["interests", "关注领域"],
            ["questions", "我的提问"],
            ["liked", "我的点赞"],
            ["orders", "我的订单"],
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

      {tab === "profile" && <ProfileForm user={user} onSaved={refreshUser} />}
      {tab === "interests" && <Interests />}
      {tab === "questions" && <MyQuestions />}
      {tab === "liked" && <MyLikes />}
      {tab === "orders" && <MyOrders />}
    </div>
  )
}

function ProfileForm({ user, onSaved }: { user: UserProfile; onSaved: () => void }) {
  const [form, setForm] = useState({
    nickname: user.nickname ?? "",
    realName: user.realName ?? "",
    gender: user.gender ?? "",
  })
  const mutation = useMutation({
    mutationFn: () => api.put<UserProfile>("/users/me", form),
    onSuccess: () => onSaved(),
  })

  function onSubmit(event: FormEvent) {
    event.preventDefault()
    mutation.mutate()
  }

  return (
    <Card className="max-w-xl">
      <CardHeader>
        <CardTitle className="text-base">基本资料</CardTitle>
      </CardHeader>
      <CardContent>
        <form className="space-y-4" onSubmit={onSubmit}>
          <div className="space-y-2">
            <Label>用户名</Label>
            <Input value={user.username ?? "-"} disabled />
          </div>
          <div className="space-y-2">
            <Label>昵称</Label>
            <Input
              value={form.nickname}
              onChange={(e) => setForm({ ...form, nickname: e.target.value })}
            />
          </div>
          <div className="space-y-2">
            <Label>真实姓名</Label>
            <Input
              value={form.realName}
              onChange={(e) => setForm({ ...form, realName: e.target.value })}
            />
          </div>
          <div className="space-y-2">
            <Label>性别</Label>
            <Input
              value={form.gender}
              onChange={(e) => setForm({ ...form, gender: e.target.value })}
            />
          </div>
          <Button type="submit" disabled={mutation.isPending}>
            保存
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}

function Interests() {
  const queryClient = useQueryClient()
  const [selected, setSelected] = useState<number[]>([])
  const areas = useQuery({
    queryKey: ["areas", "flat"],
    queryFn: () => api.get<ConsultArea[]>("/public/areas/flat"),
  })
  const current = useQuery({
    queryKey: ["interests"],
    queryFn: () => api.get<number[]>("/users/me/interest-areas"),
  })

  useEffect(() => {
    if (current.data) setSelected(current.data)
  }, [current.data])

  const save = useMutation({
    mutationFn: () => api.put("/users/me/interest-areas", { areaIds: selected }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["interests"] }),
  })

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">关注领域（用于推荐问答与指南）</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex flex-wrap gap-2">
          {areas.data?.map((area) => (
            <button
              key={area.id}
              type="button"
              onClick={() =>
                setSelected((prev) =>
                  prev.includes(area.id) ? prev.filter((x) => x !== area.id) : [...prev, area.id],
                )
              }
              className={cn(
                "rounded-md border px-3 py-1 text-sm",
                selected.includes(area.id)
                  ? "border-primary bg-primary text-primary-foreground"
                  : "hover:bg-accent",
              )}
            >
              {area.name}
            </button>
          ))}
        </div>
        <Button onClick={() => save.mutate()} disabled={save.isPending}>
          保存
        </Button>
      </CardContent>
    </Card>
  )
}

function MyLikes() {
  const questions = useQuery({
    queryKey: ["liked", "questions"],
    queryFn: () => api.get<QuestionCard[]>("/questions/liked"),
  })
  const answers = useQuery({
    queryKey: ["liked", "answers"],
    queryFn: () => api.get<AnswerView[]>("/answers/liked"),
  })

  return (
    <div className="space-y-6">
      <div className="space-y-3">
        <h3 className="text-sm font-semibold">点赞的问题（{questions.data?.length ?? 0}）</h3>
        {questions.data?.length ? (
          questions.data.map((q) => (
            <Card key={q.id}>
              <CardContent className="p-4">
                <Link to={`/questions/${q.id}`} className="text-sm font-medium hover:text-primary">
                  {q.title}
                </Link>
              </CardContent>
            </Card>
          ))
        ) : (
          <p className="text-sm text-muted-foreground">暂无</p>
        )}
      </div>

      <div className="space-y-3">
        <h3 className="text-sm font-semibold">点赞的回答（{answers.data?.length ?? 0}）</h3>
        {answers.data?.length ? (
          answers.data.map((a) => (
            <Card key={a.id}>
              <CardContent className="p-4">
                <p className="text-sm font-medium">{a.professorName}</p>
                <p className="line-clamp-2 text-sm text-muted-foreground">{a.content}</p>
              </CardContent>
            </Card>
          ))
        ) : (
          <p className="text-sm text-muted-foreground">暂无</p>
        )}
      </div>
    </div>
  )
}

function MyQuestions() {  const query = useQuery({
    queryKey: ["my-questions"],
    queryFn: () => api.get<PageResponse<QuestionCard>>("/questions/mine?size=20"),
  })
  if (query.isLoading) return <LoadingState />
  if (!query.data?.list.length) return <p className="text-sm text-muted-foreground">暂无提问</p>
  return (
    <div className="space-y-3">
      {query.data.list.map((q) => (
        <Card key={q.id}>
          <CardContent className="flex items-center justify-between p-4">
            <span>{q.title}</span>
            <Badge variant="outline">{q.status}</Badge>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}

function MyOrders() {
  const queryClient = useQueryClient()
  const query = useQuery({
    queryKey: ["orders"],
    queryFn: () => api.get<PageResponse<PayOrder>>("/orders?size=20"),
  })
  const pay = useMutation({
    mutationFn: async (order: PayOrder) => {
      await api.post(`/payments/orders/${order.id}`)
      await api.post(`/payments/mock/confirm?orderNo=${order.orderNo}`)
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["orders"] }),
  })

  if (query.isLoading) return <LoadingState />
  if (!query.data?.list.length) return <p className="text-sm text-muted-foreground">暂无订单</p>
  return (
    <div className="space-y-3">
      {query.data.list.map((order) => (
        <Card key={order.id}>
          <CardContent className="flex items-center justify-between p-4">
            <div>
              <p className="text-sm font-medium">{order.subject}</p>
              <p className="text-xs text-muted-foreground">
                {order.orderNo} · ￥{order.amount}
              </p>
            </div>
            <div className="flex items-center gap-3">
              <Badge variant={order.status === "PAID" ? "secondary" : "outline"}>
                {order.status === "PAID" ? "已支付" : order.status === "TO_PAY" ? "待支付" : order.status}
              </Badge>
              {order.status === "TO_PAY" && (
                <Button size="sm" onClick={() => pay.mutate(order)} disabled={pay.isPending}>
                  模拟支付
                </Button>
              )}
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
