import { Outlet } from "react-router-dom"
import { Header } from "@/components/layout/Header"

export function Layout() {
  return (
    <div className="flex min-h-screen flex-col bg-muted/30">
      <Header />
      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-6">
        <Outlet />
      </main>
      <footer className="border-t border-border bg-background py-6 text-center text-xs text-muted-foreground">
        教授面对面 · 毕业设计重构版 · Spring Boot 4 + React
      </footer>
    </div>
  )
}
