import { Link } from "react-router-dom"
import { useQuery } from "@tanstack/react-query"
import { ArrowRight, Search } from "lucide-react"
import { api, type PageResponse } from "@/lib/api"
import type { NewsView, ProfessorSummary, QuestionCard } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { LoadingState } from "@/components/ui/spinner"
import { QuestionListItem } from "@/components/common/QuestionListItem"

export function HomePage() {
  const news = useQuery({
    queryKey: ["home", "news"],
    queryFn: () => api.get<NewsView[]>("/news/index"),
  })
  const questions = useQuery({
    queryKey: ["home", "questions"],
    queryFn: () => api.get<PageResponse<QuestionCard>>("/public/questions?size=5"),
  })
  const professors = useQuery({
    queryKey: ["home", "professors"],
    queryFn: () => api.get<PageResponse<ProfessorSummary>>("/professors?size=6"),
  })

  return (
    <div className="space-y-8">
      <section className="rounded-2xl bg-gradient-to-br from-primary to-primary/70 px-8 py-14 text-primary-foreground">
        <h1 className="text-3xl font-bold md:text-4xl">教授面对面</h1>
        <p className="mt-3 max-w-xl text-primary-foreground/90">
          连接学生与教授，解答考研择校、专业方向、学习规划困惑，查看真实经验与学习资料。
        </p>
        <div className="mt-6 flex gap-3">
          <Button variant="secondary" asChild>
            <Link to="/questions">
              <Search />
              浏览问答
            </Link>
          </Button>
          <Button
            variant="outline"
            className="border-primary-foreground/40 bg-transparent text-primary-foreground hover:bg-primary-foreground/10"
            asChild
          >
            <Link to="/professors">找教授</Link>
          </Button>
        </div>
      </section>

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="space-y-4 lg:col-span-2">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold">最新问答</h2>
            <Button variant="link" size="sm" asChild>
              <Link to="/questions">
                全部 <ArrowRight />
              </Link>
            </Button>
          </div>
          {questions.isLoading ? (
            <LoadingState />
          ) : (
            <div className="space-y-3">
              {questions.data?.list.map((q) => (
                <QuestionListItem key={q.id} question={q} />
              ))}
            </div>
          )}
        </div>

        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="text-base">热门资讯</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {news.data?.map((item) => (
                <Link
                  key={item.id}
                  to={`/news/${item.id}`}
                  className="block text-sm hover:text-primary"
                >
                  {item.title}
                </Link>
              ))}
              {news.data?.length === 0 && (
                <p className="text-sm text-muted-foreground">暂无资讯</p>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-base">推荐教授</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {professors.data?.list.map((p) => (
                <Link
                  key={p.userId}
                  to={`/professors/${p.userId}`}
                  className="flex items-center justify-between text-sm hover:text-primary"
                >
                  <span>{p.realName}</span>
                  <span className="text-muted-foreground">{p.jobRankName}</span>
                </Link>
              ))}
              {professors.data?.list.length === 0 && (
                <p className="text-sm text-muted-foreground">暂无教授</p>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
