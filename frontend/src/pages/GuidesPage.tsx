import { useQuery } from "@tanstack/react-query"
import { ChevronRight } from "lucide-react"
import { api } from "@/lib/api"
import type { GuideNode } from "@/lib/types"
import { useAuth } from "@/lib/auth"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent } from "@/components/ui/card"
import { LoadingState } from "@/components/ui/spinner"

export function GuidesPage() {
  const { user } = useAuth()
  const tree = useQuery({
    queryKey: ["guides", "tree"],
    queryFn: () => api.get<GuideNode[]>("/study-guides/tree"),
  })
  const recommended = useQuery({
    queryKey: ["guides", "recommend"],
    queryFn: () => api.get<GuideNode[]>("/study-guides/recommend"),
    enabled: !!user,
  })

  return (
    <div className="space-y-6">
      <h1 className="text-xl font-semibold">学习指南</h1>

      {user && recommended.data && recommended.data.length > 0 && (
        <Card>
          <CardContent className="space-y-2 p-5">
            <p className="font-medium">根据你的关注领域推荐</p>
            <GuideTree nodes={recommended.data} />
          </CardContent>
        </Card>
      )}

      {tree.isLoading ? (
        <LoadingState />
      ) : (
        <Card>
          <CardContent className="p-5">
            <GuideTree nodes={tree.data ?? []} />
          </CardContent>
        </Card>
      )}
    </div>
  )
}

function GuideTree({ nodes }: { nodes: GuideNode[] }) {
  if (!nodes.length) {
    return <p className="text-sm text-muted-foreground">暂无指南</p>
  }
  return (
    <ul className="space-y-1 pl-1">
      {nodes.map((node) => (
        <li key={node.id}>
          <details open className="group">
            <summary className="flex cursor-pointer list-none items-center gap-1 py-1 text-sm">
              <ChevronRight className="size-3.5 transition-transform group-open:rotate-90" />
              {node.name}
              {node.important && <Badge variant="secondary">重点</Badge>}
            </summary>
            {node.children.length > 0 && (
              <div className="pl-4">
                <GuideTree nodes={node.children} />
              </div>
            )}
          </details>
        </li>
      ))}
    </ul>
  )
}
