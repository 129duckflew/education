import { cn } from "@/lib/utils"

export function Spinner({ className }: { className?: string }) {
  return (
    <div
      className={cn(
        "size-6 animate-spin rounded-full border-2 border-muted border-t-primary",
        className,
      )}
    />
  )
}

export function LoadingState({ label = "加载中..." }: { label?: string }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-muted-foreground">
      <Spinner />
      <span className="text-sm">{label}</span>
    </div>
  )
}

export function EmptyState({ label = "暂无数据" }: { label?: string }) {
  return <div className="py-16 text-center text-sm text-muted-foreground">{label}</div>
}
