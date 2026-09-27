import { Link, useParams } from "react-router-dom"
import { useQuery } from "@tanstack/react-query"
import { api } from "@/lib/api"
import type { NewsView } from "@/lib/types"
import { Card, CardContent } from "@/components/ui/card"
import { LoadingState } from "@/components/ui/spinner"

export function NewsDetailPage() {
  const { id } = useParams()
  const newsId = Number(id)

  const query = useQuery({
    queryKey: ["news", newsId],
    queryFn: () => api.get<NewsView>(`/news/${newsId}`),
  })
  const related = useQuery({
    queryKey: ["news", newsId, "related"],
    queryFn: () => api.get<NewsView[]>(`/news/${newsId}/related?limit=5`),
  })

  if (query.isLoading) return <LoadingState />
  if (query.isError || !query.data) {
    return <p className="py-16 text-center text-muted-foreground">资讯不存在</p>
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <article className="space-y-4">
        <h1 className="text-2xl font-bold">{query.data.title}</h1>
        <p className="text-xs text-muted-foreground">
          {new Date(query.data.createdAt).toLocaleString("zh-CN")}
        </p>
        <div className="whitespace-pre-wrap text-sm leading-relaxed">{query.data.content}</div>
        {query.data.sources.length > 0 && (
          <div className="space-y-1 border-t border-border pt-4 text-sm">
            <p className="font-medium">来源</p>
            {query.data.sources.map((s) => (
              <a
                key={s.id}
                href={s.url}
                target="_blank"
                rel="noreferrer"
                className="block text-primary hover:underline"
              >
                {s.title}
              </a>
            ))}
          </div>
        )}
      </article>

      {related.data && related.data.length > 0 && (
        <Card>
          <CardContent className="space-y-2 p-5">
            <p className="font-medium">相关资讯</p>
            {related.data.map((item) => (
              <Link
                key={item.id}
                to={`/news/${item.id}`}
                className="block text-sm text-muted-foreground hover:text-primary"
              >
                {item.title}
              </Link>
            ))}
          </CardContent>
        </Card>
      )}
    </div>
  )
}
