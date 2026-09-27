import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Plus, Trash2 } from "lucide-react"
import { api } from "@/lib/api"
import type { ConsultAreaNode } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { LoadingState } from "@/components/ui/spinner"

export function AdminAreasPage() {
  const queryClient = useQueryClient()
  const [name, setName] = useState("")
  const [parentId, setParentId] = useState<number | null>(null)

  const query = useQuery({
    queryKey: ["areas", "tree"],
    queryFn: () => api.get<ConsultAreaNode[]>("/public/areas/tree"),
  })

  const invalidate = () => {
    queryClient.invalidateQueries({ queryKey: ["areas"] })
  }

  const create = useMutation({
    mutationFn: () => api.post("/admin/areas", { name, parentId }),
    onSuccess: () => {
      setName("")
      setParentId(null)
      invalidate()
    },
  })
  const remove = useMutation({
    mutationFn: (id: number) => api.del(`/admin/areas/${id}`),
    onSuccess: invalidate,
  })

  return (
    <div className="space-y-5">
      <h1 className="text-xl font-semibold">领域管理</h1>

      <Card>
        <CardContent className="flex flex-wrap items-end gap-3 p-4">
          <div>
            <p className="mb-1 text-xs text-muted-foreground">父级（留空为顶级）</p>
            <select
              className="h-9 rounded-md border border-input bg-background px-2 text-sm"
              value={parentId ?? ""}
              onChange={(e) => setParentId(e.target.value ? Number(e.target.value) : null)}
            >
              <option value="">顶级领域</option>
              {flatten(query.data ?? []).map((a) => (
                <option key={a.id} value={a.id}>
                  {" ".repeat(a.depth * 2)}
                  {a.name}
                </option>
              ))}
            </select>
          </div>
          <Input
            className="max-w-xs"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="领域名称"
          />
          <Button onClick={() => create.mutate()} disabled={!name || create.isPending}>
            <Plus />
            新增
          </Button>
        </CardContent>
      </Card>

      {query.isLoading ? (
        <LoadingState />
      ) : (
        <Card>
          <CardContent className="p-4">
            <AreaTree nodes={query.data ?? []} depth={0} onDelete={(id) => remove.mutate(id)} />
          </CardContent>
        </Card>
      )}
    </div>
  )
}

interface FlatArea extends ConsultAreaNode {
  depth: number
}

function flatten(nodes: ConsultAreaNode[], depth = 0): FlatArea[] {
  return nodes.flatMap((node) => [
    { ...node, depth },
    ...flatten(node.children, depth + 1),
  ])
}

function AreaTree({
  nodes,
  depth,
  onDelete,
}: {
  nodes: ConsultAreaNode[]
  depth: number
  onDelete: (id: number) => void
}) {
  return (
    <ul className="space-y-1">
      {nodes.map((node) => (
        <li key={node.id}>
          <div
            className="flex items-center justify-between rounded-md px-2 py-1 text-sm hover:bg-accent"
            style={{ paddingLeft: depth * 16 + 8 }}
          >
            <span>{node.name}</span>
            <Button variant="ghost" size="icon" onClick={() => onDelete(node.id)}>
              <Trash2 className="size-4 text-destructive" />
            </Button>
          </div>
          {node.children.length > 0 && (
            <AreaTree nodes={node.children} depth={depth + 1} onDelete={onDelete} />
          )}
        </li>
      ))}
    </ul>
  )
}
