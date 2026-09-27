import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Plus, Save, Trash2 } from "lucide-react"
import { api } from "@/lib/api"
import type { ConsultArea, GuideNode } from "@/lib/types"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { LoadingState } from "@/components/ui/spinner"
import { cn } from "@/lib/utils"

export function AdminGuidesPage() {
  const queryClient = useQueryClient()
  const [name, setName] = useState("")
  const [parentId, setParentId] = useState<number | null>(null)
  const [important, setImportant] = useState(false)
  const [selected, setSelected] = useState<GuideNode | null>(null)

  const tree = useQuery({
    queryKey: ["guides", "tree"],
    queryFn: () => api.get<GuideNode[]>("/public/study-guides/tree"),
  })
  const areas = useQuery({
    queryKey: ["areas", "flat"],
    queryFn: () => api.get<ConsultArea[]>("/public/areas/flat"),
  })
  const selectedAreas = useQuery({
    queryKey: ["guides", "areas", selected?.id],
    queryFn: () => api.get<number[]>(`/admin/study-guides/${selected!.id}/areas`),
    enabled: !!selected,
  })

  const invalidate = () => {
    queryClient.invalidateQueries({ queryKey: ["guides"] })
  }

  const create = useMutation({
    mutationFn: () => api.post("/admin/study-guides", { name, parentId, important }),
    onSuccess: () => {
      setName("")
      setParentId(null)
      setImportant(false)
      invalidate()
    },
  })
  const remove = useMutation({
    mutationFn: (id: number) => api.del(`/admin/study-guides/${id}`),
    onSuccess: () => {
      setSelected(null)
      invalidate()
    },
  })

  return (
    <div className="space-y-5">
      <h1 className="text-xl font-semibold">学习指南管理</h1>

      <Card>
        <CardContent className="flex flex-wrap items-end gap-3 p-4">
          <div>
            <p className="mb-1 text-xs text-muted-foreground">父节点</p>
            <select
              className="h-9 rounded-md border border-input bg-background px-2 text-sm"
              value={parentId ?? ""}
              onChange={(e) => setParentId(e.target.value ? Number(e.target.value) : null)}
            >
              <option value="">根节点</option>
              {flatten(tree.data ?? []).map((n) => (
                <option key={n.id} value={n.id}>
                  {" ".repeat(n.depth * 2)}
                  {n.name}
                </option>
              ))}
            </select>
          </div>
          <Input
            className="max-w-xs"
            placeholder="节点名称"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          <label className="flex items-center gap-2 text-sm">
            <input
              type="checkbox"
              checked={important}
              onChange={(e) => setImportant(e.target.checked)}
            />
            重点
          </label>
          <Button onClick={() => create.mutate()} disabled={!name || create.isPending}>
            <Plus />
            新增
          </Button>
        </CardContent>
      </Card>

      <div className="grid gap-4 lg:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="text-base">指南树</CardTitle>
          </CardHeader>
          <CardContent>
            {tree.isLoading ? (
              <LoadingState />
            ) : (
              <GuideTree
                nodes={tree.data ?? []}
                depth={0}
                selectedId={selected?.id}
                onSelect={setSelected}
                onDelete={(id) => remove.mutate(id)}
              />
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">
              {selected ? `关联领域：${selected.name}` : "选择一个节点"}
            </CardTitle>
          </CardHeader>
          <CardContent>
            {selected && (
              <AreaPicker
                key={selected.id}
                guideId={selected.id}
                areas={areas.data ?? []}
                current={selectedAreas.data ?? []}
              />
            )}
            {!selected && <p className="text-sm text-muted-foreground">点击左侧节点设置关联领域</p>}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

interface FlatGuide extends GuideNode {
  depth: number
}

function flatten(nodes: GuideNode[], depth = 0): FlatGuide[] {
  return nodes.flatMap((n) => [{ ...n, depth }, ...flatten(n.children, depth + 1)])
}

function GuideTree({
  nodes,
  depth,
  selectedId,
  onSelect,
  onDelete,
}: {
  nodes: GuideNode[]
  depth: number
  selectedId?: number
  onSelect: (node: GuideNode) => void
  onDelete: (id: number) => void
}) {
  return (
    <ul className="space-y-1">
      {nodes.map((node) => (
        <li key={node.id}>
          <div
            className={cn(
              "flex items-center justify-between rounded-md px-2 py-1 text-sm hover:bg-accent",
              selectedId === node.id && "bg-primary/10",
            )}
            style={{ paddingLeft: depth * 16 + 8 }}
          >
            <button type="button" className="flex items-center gap-2" onClick={() => onSelect(node)}>
              {node.name}
              {node.important && <Badge variant="secondary">重点</Badge>}
            </button>
            <Button variant="ghost" size="icon" onClick={() => onDelete(node.id)}>
              <Trash2 className="size-4 text-destructive" />
            </Button>
          </div>
          {node.children.length > 0 && (
            <GuideTree
              nodes={node.children}
              depth={depth + 1}
              selectedId={selectedId}
              onSelect={onSelect}
              onDelete={onDelete}
            />
          )}
        </li>
      ))}
    </ul>
  )
}

function AreaPicker({
  guideId,
  areas,
  current,
}: {
  guideId: number
  areas: ConsultArea[]
  current: number[]
}) {
  const queryClient = useQueryClient()
  const [selected, setSelected] = useState<number[]>(current)
  const save = useMutation({
    mutationFn: () => api.put(`/admin/study-guides/${guideId}/areas`, { areaIds: selected }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["guides", "areas", guideId] }),
  })

  return (
    <div className="space-y-3">
      <div className="flex flex-wrap gap-2">
        {areas.map((area) => (
          <button
            key={area.id}
            type="button"
            onClick={() =>
              setSelected((prev) =>
                prev.includes(area.id) ? prev.filter((x) => x !== area.id) : [...prev, area.id],
              )
            }
            className={cn(
              "rounded-md border px-2 py-1 text-xs",
              selected.includes(area.id)
                ? "border-primary bg-primary text-primary-foreground"
                : "hover:bg-accent",
            )}
          >
            {area.name}
          </button>
        ))}
      </div>
      <Button size="sm" onClick={() => save.mutate()} disabled={save.isPending}>
        <Save />
        保存关联
      </Button>
    </div>
  )
}
