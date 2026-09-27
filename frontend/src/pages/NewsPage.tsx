import { Link } from "react-router-dom"
import { useQuery } from "@tanstack/react-query"
import { api, type PageResponse } from "@/lib/api"
import type { NewsView } from "@/lib/types"
import { Card, CardContent } from "@/components/ui/card"
import { EmptyState, LoadingState } from "@/components/ui/spinner"

export function NewsPage() {
  const query = useQuery({
    queryKey: ["news", "list"],
    queryFn: () => api.get<PageResponse<NewsView>>("/news?size=20"),
  })

  if (query.isLoading) return <LoadingState />
  if (!query.data?.list.length) return <EmptyState label="暂无资讯" />

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-semibold">系统资讯</h1>
      {query.data.list.map((news) => (
        <Card key={news.id} className="transition-shadow hover:shadow-md">
          <CardContent className="p-5">
            <Link to={`/news/${news.id}`} className="space-y-1">
              <h2 className="font-medium hover:text-primary">{news.title}</h2>
              <p className="line-clamp-2 text-sm text-muted-foreground">{news.content}</p>
              <p className="text-xs text-muted-foreground">
                {new Date(news.createdAt).toLocaleDateString("zh-CN")}
              </p>
            </Link>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
