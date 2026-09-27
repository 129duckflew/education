import { Link } from "react-router-dom"
import { BookOpen, GraduationCap, LogIn, MessageSquare, UserRound } from "lucide-react"
import { useAuth } from "@/lib/auth"
import { Button } from "@/components/ui/button"

const navItems = [
  { to: "/questions", label: "问答" },
  { to: "/professors", label: "教授" },
  { to: "/guides", label: "学习指南" },
  { to: "/resources", label: "学习资料" },
  { to: "/news", label: "资讯" },
]

export function Header() {
  const { user, logout } = useAuth()

  return (
    <header className="sticky top-0 z-40 border-b border-border bg-background/95 backdrop-blur">
      <div className="mx-auto flex h-14 max-w-6xl items-center gap-6 px-4">
        <Link to="/" className="flex items-center gap-2 font-semibold">
          <GraduationCap className="size-5" />
          教授面对面
        </Link>
        <nav className="hidden items-center gap-1 md:flex">
          {navItems.map((item) => (
            <Link
              key={item.to}
              to={item.to}
              className="rounded-md px-3 py-1.5 text-sm text-muted-foreground transition-colors hover:bg-accent hover:text-foreground"
            >
              {item.label}
            </Link>
          ))}
        </nav>
        <div className="ml-auto flex items-center gap-2">
          {user ? (
            <>
              <Button variant="ghost" size="sm" asChild>
                <Link to="/questions/new">
                  <BookOpen />
                  提问
                </Link>
              </Button>
              <Button variant="ghost" size="icon" asChild title="消息">
                <Link to="/messages">
                  <MessageSquare />
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
