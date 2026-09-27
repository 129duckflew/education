import { useEffect, useState, type ReactNode } from "react"
import { Link, useSearchParams } from "react-router-dom"
import { useQuery } from "@tanstack/react-query"
import { Newspaper, Search, UserRound } from "lucide-react"
import { api } from "@/lib/api"
import type { SearchResult } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { EmptyState, LoadingState } from "@/components/ui/spinner"

export function SearchPage() {
  const [params, setParams] = useSearchParams()
  const initial = params.get("q") ?? ""
  const [keyword, setKeyword] = useState(initial)
  const [submitted, setSubmitted] = useState(initial)

  useEffect(() => {
    const urlKeyword = params.get("q") ?? ""
    if (urlKeyword !== submitted) {
      setSubmitted(urlKeyword)
      setKeyword(urlKeyword)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [params])

  const query = useQuery({
    queryKey: ["search", submitted],
    queryFn: () => api.get<SearchResult>(`/public/search?keyword=${encodeURIComponent(submitted)}&limit=5`),
    enabled: submitted.trim().length > 0,
  })

  function submit(value: string) {
    setSubmitted(value)
    setParams(value ? { q: value } : {})
  }

  const data = query.data

  return (
    <div className="space-y-5">
      <form
        className="flex gap-2"
        onSubmit={(e) => {
          e.preventDefault()
          submit(keyword)
        }}
      >
        <Input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          placeholder="全站搜索：问题、教授、资讯、资料、指南"
        />
        <Button type="submit">
          <Search />
          搜索
        </Button>
      </form>

      {!submitted.trim() ? (
        <EmptyState label="输入关键词开始搜索" />
      ) : query.isLoading ? (
        <LoadingState />
      ) : (
        <div className="space-y-5">
          <Section title={`问题（${data?.questions.length ?? 0}）`} empty="没有问题">
            {data?.questions.map((q) => (
              <Link key={q.id} to={`/questions/${q.id}`} className="block py-2 hover:text-primary">
                <p className="text-sm font-medium">{q.title}</p>
                <p className="line-clamp-1 text-xs text-muted-foreground">{q.description}</p>
              </Link>
            ))}
          </Section>

          <Section title={`教授（${data?.professors.length ?? 0}）`} empty="没有教授">
            {data?.professors.map((p) => (
              <Link
                key={p.userId}
                to={`/professors/${p.userId}`}
                className="flex items-center gap-2 py-2 hover:text-primary"
              >
                <UserRound className="size-4" />
                <span className="text-sm font-medium">{p.realName}</span>
                <span className="text-xs text-muted-foreground">{p.jobRankName}</span>
              </Link>
            ))}
          </Section>

          <Section title={`资讯（${data?.news.length ?? 0}）`} empty="没有资讯">
            {data?.news.map((n) => (
              <Link
                key={n.id}
                to={`/news/${n.id}`}
                className="flex items-center gap-2 py-2 text-sm hover:text-primary"
              >
                <Newspaper className="size-4" />
                {n.title}
              </Link>
            ))}
          </Section>

          <Section title={`学习资料（${data?.resources.length ?? 0}）`} empty="没有资料">
            {data?.resources.map((r) => (
              <div key={r.id} className="py-2 text-sm">
                {r.name}
                <span className="ml-2 text-xs text-muted-foreground">{r.professorName}</span>
              </div>
            ))}
          </Section>

          <Section title={`学习指南（${data?.guides.length ?? 0}）`} empty="没有指南">
            {data?.guides.map((g) => (
              <Link key={g.id} to="/guides" className="block py-2 text-sm hover:text-primary">
                {g.name}
              </Link>
            ))}
          </Section>
        </div>
      )}
    </div>
  )
}

function Section({
  title,
  empty,
  children,
}: {
  title: string
  empty: string
  children?: ReactNode
}) {
  const hasContent = Array.isArray(children) ? children.length > 0 : !!children
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        {hasContent ? children : <p className="text-sm text-muted-foreground">{empty}</p>}
      </CardContent>
    </Card>
  )
}
