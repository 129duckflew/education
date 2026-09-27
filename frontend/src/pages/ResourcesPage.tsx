import { useState } from "react"
import { useQuery } from "@tanstack/react-query"
import { Download, Search } from "lucide-react"
import { api, type PageResponse } from "@/lib/api"
import type { ResourceView } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { EmptyState, LoadingState } from "@/components/ui/spinner"

export function ResourcesPage() {
  const [keyword, setKeyword] = useState("")
  const [submitted, setSubmitted] = useState("")

  const query = useQuery({
    queryKey: ["resources", submitted],
    queryFn: () =>
      api.get<PageResponse<ResourceView>>(
        `/resources?size=20${submitted ? `&keyword=${encodeURIComponent(submitted)}` : ""}`,
      ),
  })

  return (
    <div className="space-y-5">
      <h1 className="text-xl font-semibold">学习资料</h1>
      <div className="flex gap-2">
        <Input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && setSubmitted(keyword)}
          placeholder="搜索资料名称"
        />
        <Button onClick={() => setSubmitted(keyword)}>
          <Search />
          搜索
        </Button>
      </div>

      {query.isLoading ? (
        <LoadingState />
      ) : query.data?.list.length ? (
        <div className="space-y-3">
          {query.data.list.map((r) => (
            <Card key={r.id}>
              <CardContent className="flex items-center justify-between p-5">
                <div>
                  <p className="font-medium">{r.name}</p>
                  <p className="text-sm text-muted-foreground">
                    {r.professorName} · {r.remark}
                  </p>
                </div>
                <Button variant="outline" size="sm" asChild>
                  <a href={`/api/files/${r.fileId}`} target="_blank" rel="noreferrer">
                    <Download />
                    下载
                  </a>
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <EmptyState label="暂无资料" />
      )}
    </div>
  )
}
