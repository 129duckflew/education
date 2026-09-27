import { useState } from "react"
import { Link } from "react-router-dom"
import { useQuery } from "@tanstack/react-query"
import { Search, Star } from "lucide-react"
import { api, type PageResponse } from "@/lib/api"
import type { ProfessorSummary } from "@/lib/types"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { EmptyState, LoadingState } from "@/components/ui/spinner"

export function ProfessorsPage() {
  const [keyword, setKeyword] = useState("")
  const [submitted, setSubmitted] = useState("")

  const query = useQuery({
    queryKey: ["professors", submitted],
    queryFn: () =>
      submitted
        ? api.get<PageResponse<ProfessorSummary>>(
            `/professors/search?keyword=${encodeURIComponent(submitted)}&size=24`,
          )
        : api.get<PageResponse<ProfessorSummary>>("/professors?size=24"),
  })

  return (
    <div className="space-y-5">
      <div className="flex gap-2">
        <Input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && setSubmitted(keyword)}
          placeholder="搜索教授姓名或研究方向"
        />
        <Button onClick={() => setSubmitted(keyword)}>
          <Search />
          搜索
        </Button>
      </div>

      {query.isLoading ? (
        <LoadingState />
      ) : query.data?.list.length ? (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {query.data.list.map((p) => (
            <Card key={p.userId} className="transition-shadow hover:shadow-md">
              <CardContent className="space-y-3 p-5">
                <div className="flex items-center justify-between">
                  <Link to={`/professors/${p.userId}`} className="font-medium hover:text-primary">
                    {p.realName}
                  </Link>
                  <span className="flex items-center gap-1 text-sm text-muted-foreground">
                    <Star className="size-3.5 fill-current text-amber-500" />
                    {p.rating.toFixed(1)} ({p.reviewCount})
                  </span>
                </div>
                <p className="text-sm text-muted-foreground">{p.jobRankName}</p>
                <div className="flex flex-wrap gap-1">
                  {p.areaNames.map((name) => (
                    <Badge key={name} variant="outline">
                      {name}
                    </Badge>
                  ))}
                </div>
                <p className="line-clamp-2 text-sm text-muted-foreground">{p.introduction}</p>
                <p className="text-sm">咨询价：￥{p.consultPrice}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <EmptyState label="没有找到教授" />
      )}
    </div>
  )
}
