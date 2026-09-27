import { Link, NavLink, Outlet } from "react-router-dom"
import { GraduationCap, LayoutDashboard, ListChecks, Map, Newspaper, Users, UserCog } from "lucide-react"
import { cn } from "@/lib/utils"

const links = [
  { to: "/admin/dashboard", label: "仪表盘", icon: LayoutDashboard },
  { to: "/admin/questions", label: "问题审核", icon: ListChecks },
  { to: "/admin/professors", label: "教授认证", icon: UserCog },
  { to: "/admin/users", label: "用户管理", icon: Users },
  { to: "/admin/areas", label: "领域管理", icon: Map },
  { to: "/admin/news", label: "资讯管理", icon: Newspaper },
]

export function AdminLayout() {
  return (
    <div className="flex min-h-screen bg-muted/30">
      <aside className="flex w-56 shrink-0 flex-col border-r border-border bg-background">
        <div className="flex h-14 items-center gap-2 border-b border-border px-4 font-semibold">
          <GraduationCap className="size-5" />
          管理后台
        </div>
        <nav className="flex-1 space-y-1 p-3">
          {links.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                cn(
                  "flex items-center gap-2 rounded-md px-3 py-2 text-sm transition-colors",
                  isActive ? "bg-primary text-primary-foreground" : "hover:bg-accent",
                )
              }
            >
              <Icon className="size-4" />
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="p-3">
          <Link to="/" className="text-sm text-muted-foreground hover:text-primary">
            ← 返回前台
          </Link>
        </div>
      </aside>
      <main className="flex-1 p-6">
        <Outlet />
      </main>
    </div>
  )
}
