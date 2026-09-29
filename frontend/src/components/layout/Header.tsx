import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { useQuery } from "@tanstack/react-query"
import { BookOpen, GraduationCap, LogIn, MessageSquare, Search, UserRound } from "lucide-react"
import { api } from "@/lib/api"
import { useAuth } from "@/lib/auth"
import { useRealtimeStream } from "@/lib/useRealtimeStream"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"

const navItems = [
  { to: "/questions", label: "问答" },
  { to: "/professors", label: "教授" },
  { to: "/guides", label: "学习指南" },
  { to: "/resources", label: "学习资料" },
  { to: "/news", label: "资讯" },
]

export function Header() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [keyword, setKeyword] = useState("")
  useRealtimeStream()

  const unread = useQuery({
    queryKey: ["notifications", "unread"],
    queryFn: () => api.get<number>("/notifications/unread-count"),
    enabled: !!user,
    refetchInterval: 30_000,
  })
  const unreadMessages = useQuery({
    queryKey: ["conversations", "unread"],
    queryFn: () => api.get<number>("/conversations/unread-count"),
    enabled: !!user,
    refetchInterval: 30_000,
  })
  const badge = (unread.data ?? 0) + (unreadMessages.data ?? 0)

  return (
    <header className="sticky top-0 z-40 border-b border-border bg-background/95 backdrop-blur">
      <div className="mx-auto flex h-14 max-w-6xl items-center gap-4 px-4">
        <Link to="/" className="flex shrink-0 items-center gap-2 font-semibold">
          <GraduationCap className="size-5" />
          教授面对面
        </Link>
        <nav className="hidden items-center gap-1 lg:flex">
          {navItems.map((item) => (
            <Link
              key={item.to}
              to={item.to}
              className="rounded-md px-2.5 py-1.5 text-sm text-muted-foreground transition-colors hover:bg-accent hover:text-foreground"
            >
              {item.label}
            </Link>
          ))}
        </nav>

        <form
          className="relative ml-auto hidden max-w-xs flex-1 md:block"
          onSubmit={(e) => {
            e.preventDefault()
            navigate(`/search?q=${encodeURIComponent(keyword)}`)
          }}
        >
          <Search className="pointer-events-none absolute left-2.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder="搜索"
            className="pl-8"
          />
        </form>

        <div className="flex items-center gap-2">
          {user ? (
            <>
              <Button variant="ghost" size="sm" asChild>
                <Link to="/questions/new">
                  <BookOpen />
                  提问
                </Link>
              </Button>
              {user.role !== "ADMIN" && (
                <Button variant="ghost" size="sm" asChild>
                  <Link to="/professor/center">
                    {user.role === "PROFESSOR" ? "教授中心" : "成为教授"}
                  </Link>
                </Button>
              )}
              <Button variant="ghost" size="icon" asChild title="消息">
                <Link to="/messages" className="relative">
                  <MessageSquare />
                  {badge > 0 && (
                    <span className="absolute -right-1 -top-1 flex size-4 items-center justify-center rounded-full bg-destructive text-[10px] text-destructive-foreground">
                      {badge > 99 ? "99+" : badge}
                    </span>
                  )}
                </Link>
              </Button>
              <Button variant="ghost" size="sm" asChild>
                <Link to="/profile">
                  <UserRound />
                  {user.nickname ?? user.username ?? "我"}
                </Link>
              </Button>
              {user.role === "ADMIN" && (
                <Button variant="secondary" size="sm" asChild>
                  <Link to="/admin">后台</Link>
                </Button>
              )}
              <Button variant="outline" size="sm" onClick={logout}>
                退出
              </Button>
            </>
          ) : (
            <Button size="sm" asChild>
              <Link to="/login">
                <LogIn />
                登录
              </Link>
            </Button>
          )}
        </div>
      </div>
    </header>
  )
}
