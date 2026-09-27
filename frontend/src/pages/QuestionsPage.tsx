import { useState } from "react"
import { useQuery } from "@tanstack/react-query"
import { Search } from "lucide-react"
import { api, type PageResponse } from "@/lib/api"
import type { QuestionCard } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { EmptyState, LoadingState } from "@/components/ui/spinner"
import { QuestionListItem } from "@/components/common/QuestionListItem"

const PAGE_SIZE = 10

export function QuestionsPage() {
  const [keyword, setKeyword] = useState("")
  const [submitted, setSubmitted] = useState("")
  const [page, setPage] = useState(0)

  const query = useQuery({
    queryKey: ["questions", submitted, page],
    queryFn: () =>
      submitted
        ? api.get<PageResponse<QuestionCard>>(
            `/public/search/questions?keyword=${encodeURIComponent(submitted)}&page=${page}&size=${PAGE_SIZE}`,
          )
        : api.get<PageResponse<QuestionCard>>(`/public/questions?page=${page}&size=${PAGE_SIZE}`),
  })

  const totalPages = query.data ? Math.ceil(query.data.total / PAGE_SIZE) : 0

  return (
    <div className="space-y-5">
      <div className="flex gap-2">
        <Input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              setPage(0)
              setSubmitted(keyword)
            }
          }}
          placeholder="搜索问题标题或内容"
        />
        <Button
          onClick={() => {
            setPage(0)
            setSubmitted(keyword)
          }}
        >
          <Search />
          搜索
        </Button>
      </div>

      {query.isLoading ? (
        <LoadingState />
      ) : query.data?.list.length ? (
        <div className="space-y-3">
          {query.data.list.map((q) => (
            <QuestionListItem key={q.id} question={q} />
          ))}
        </div>
      ) : (
        <EmptyState label="没有找到相关问题" />
      )}

      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-3">
          <Button variant="outline" size="sm" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
            上一页
          </Button>
          <span className="text-sm text-muted-foreground">
            {page + 1} / {totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            disabled={page + 1 >= totalPages}
            onClick={() => setPage((p) => p + 1)}
          >
            下一页
          </Button>
        </div>
      )}
    </div>
  )
}
