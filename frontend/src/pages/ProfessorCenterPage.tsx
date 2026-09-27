import { useState, type FormEvent } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Plus, Trash2 } from "lucide-react"
import { api } from "@/lib/api"
import type {
  ConsultArea,
  Degree,
  FileInfo,
  JobRank,
  ProfessorSelfView,
} from "@/lib/types"
import { useAuth } from "@/lib/auth"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { LoadingState } from "@/components/ui/spinner"
import { FileUpload } from "@/components/common/FileUpload"
import { cn } from "@/lib/utils"

interface EducationDraft {
  schoolName: string
  majorName: string
  degreeId: number | null
  startDate: string
  endDate: string
  fullTime: boolean
  researchDirection: string
}

export function ProfessorCenterPage() {
  const { refreshUser } = useAuth()
  const queryClient = useQueryClient()
  const query = useQuery({
    queryKey: ["professor", "me"],
    queryFn: () => api.get<ProfessorSelfView | null>("/professors/me"),
  })
  const jobRanks = useQuery({
    queryKey: ["meta", "job-ranks"],
    queryFn: () => api.get<JobRank[]>("/public/meta/job-ranks"),
  })
  const degrees = useQuery({
    queryKey: ["meta", "degrees"],
    queryFn: () => api.get<Degree[]>("/public/meta/degrees"),
  })
  const areas = useQuery({
    queryKey: ["areas", "flat"],
    queryFn: () => api.get<ConsultArea[]>("/public/areas/flat"),
  })

  if (query.isLoading) return <LoadingState />

  const profile = query.data
  const invalidate = () => {
    queryClient.invalidateQueries({ queryKey: ["professor", "me"] })
    refreshUser()
  }

  return (
    <div className="mx-auto max-w-3xl space-y-5">
      {!profile ? (
        <ApplyPanel jobRanks={jobRanks.data ?? []} onDone={invalidate} />
      ) : (
        <>
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <CardTitle className="text-base">教授中心</CardTitle>
                <Badge variant={profile.approved ? "secondary" : "outline"}>
                  {profile.approved ? "已通过认证" : "认证审核中"}
                </Badge>
              </div>
            </CardHeader>
            <CardContent>
              <ProfileForm
                profile={profile}
                jobRanks={jobRanks.data ?? []}
                degreeOptions={degrees.data ?? []}
                areas={areas.data ?? []}
                onSaved={invalidate}
              />
            </CardContent>
          </Card>

          {profile.approved && <ResourcePublishPanel />}
        </>
      )}
    </div>
  )
}

function ApplyPanel({ jobRanks, onDone }: { jobRanks: JobRank[]; onDone: () => void }) {
  const [form, setForm] = useState({ jobRankId: "", introduction: "", consultPrice: 0 })
  const [cvFileId, setCvFileId] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)

  const mutation = useMutation({
    mutationFn: () =>
      api.post("/professors/apply", {
        jobRankId: form.jobRankId ? Number(form.jobRankId) : null,
        introduction: form.introduction,
        consultPrice: form.consultPrice,
        cvFileId,
      }),
    onSuccess: onDone,
    onError: (err) => setError(err instanceof Error ? err.message : "提交失败"),
  })

  function onSubmit(event: FormEvent) {
    event.preventDefault()
    setError(null)
    mutation.mutate()
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">申请成为教授</CardTitle>
      </CardHeader>
      <CardContent>
        <form className="space-y-4" onSubmit={onSubmit}>
          <div className="space-y-2">
            <Label>职称</Label>
            <select
              className="h-9 w-full rounded-md border border-input bg-background px-3 text-sm"
              value={form.jobRankId}
              onChange={(e) => setForm({ ...form, jobRankId: e.target.value })}
              required
            >
              <option value="">请选择职称</option>
              {jobRanks.map((rank) => (
                <option key={rank.id} value={rank.id}>
                  {rank.name}
                </option>
              ))}
            </select>
          </div>
          <div className="space-y-2">
            <Label>个人简介</Label>
            <Textarea
              value={form.introduction}
              onChange={(e) => setForm({ ...form, introduction: e.target.value })}
              placeholder="研究方向、招生偏好、可咨询的问题等"
            />
          </div>
          <div className="space-y-2">
            <Label>单次咨询价格（元）</Label>
            <Input
              type="number"
              value={form.consultPrice}
              onChange={(e) => setForm({ ...form, consultPrice: Number(e.target.value) })}
            />
          </div>
          <div className="space-y-2">
            <Label>简历</Label>
            <FileUpload accept=".pdf,.doc,.docx" onChange={(f: FileInfo) => setCvFileId(f.id)} />
          </div>
          {error && <p className="text-sm text-destructive">{error}</p>}
          <Button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? "提交中..." : "提交申请"}
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}

function ProfileForm({
  profile,
  jobRanks,
  degreeOptions,
  areas,
  onSaved,
}: {
  profile: ProfessorSelfView
  jobRanks: JobRank[]
  degreeOptions: Degree[]
  areas: ConsultArea[]
  onSaved: () => void
}) {
  const [form, setForm] = useState({
    jobRankId: profile.jobRankId ? String(profile.jobRankId) : "",
    introduction: profile.introduction ?? "",
    consultPrice: profile.consultPrice,
  })
  const [cvFileId, setCvFileId] = useState<number | null>(profile.cvFileId)
  const [selectedAreas, setSelectedAreas] = useState<number[]>(profile.areaIds)
  const [educations, setEducations] = useState<EducationDraft[]>(
    profile.educations.map((e) => ({
      schoolName: e.schoolName,
      majorName: e.majorName ?? "",
      degreeId: degreeOptions.find((d) => d.name === e.degreeName)?.id ?? null,
      startDate: e.startDate ?? "",
      endDate: e.endDate ?? "",
      fullTime: e.fullTime,
      researchDirection: e.researchDirection ?? "",
    })),
  )
  const [message, setMessage] = useState<string | null>(null)

  const save = useMutation({
    mutationFn: async () => {
      await api.put("/professors/me", {
        jobRankId: form.jobRankId ? Number(form.jobRankId) : null,
        introduction: form.introduction,
        consultPrice: form.consultPrice,
        cvFileId,
      })
      await api.post("/professors/me/areas", { areaIds: selectedAreas })
      await api.put(
        "/professors/me/educations",
        educations.map((e) => ({
          schoolName: e.schoolName,
          majorName: e.majorName || null,
          degreeId: e.degreeId,
          startDate: e.startDate || null,
          endDate: e.endDate || null,
          fullTime: e.fullTime,
          researchDirection: e.researchDirection || null,
        })),
      )
    },
    onSuccess: () => {
      setMessage("已保存")
      onSaved()
    },
    onError: (err) => setMessage(err instanceof Error ? err.message : "保存失败"),
  })

  function updateEducation(index: number, patch: Partial<EducationDraft>) {
    setEducations((prev) => prev.map((e, i) => (i === index ? { ...e, ...patch } : e)))
  }

  return (
    <div className="space-y-6">
      <div className="space-y-4">
        <div className="space-y-2">
          <Label>职称</Label>
          <select
            className="h-9 w-full rounded-md border border-input bg-background px-3 text-sm"
            value={form.jobRankId}
            onChange={(e) => setForm({ ...form, jobRankId: e.target.value })}
          >
            <option value="">请选择职称</option>
            {jobRanks.map((rank) => (
              <option key={rank.id} value={rank.id}>
                {rank.name}
              </option>
            ))}
          </select>
        </div>
        <div className="space-y-2">
          <Label>个人简介</Label>
          <Textarea
            value={form.introduction}
            onChange={(e) => setForm({ ...form, introduction: e.target.value })}
          />
        </div>
        <div className="space-y-2">
          <Label>咨询价格（元）</Label>
          <Input
            type="number"
            value={form.consultPrice}
            onChange={(e) => setForm({ ...form, consultPrice: Number(e.target.value) })}
          />
        </div>
        <div className="space-y-2">
          <Label>简历</Label>
          <FileUpload
            accept=".pdf,.doc,.docx"
            label={cvFileId ? "替换简历" : "上传简历"}
            onChange={(f) => setCvFileId(f.id)}
          />
        </div>
        <div className="space-y-2">
          <Label>可答领域</Label>
          <div className="flex flex-wrap gap-2">
            {areas.map((area) => (
              <button
                key={area.id}
                type="button"
                onClick={() =>
                  setSelectedAreas((prev) =>
                    prev.includes(area.id)
                      ? prev.filter((x) => x !== area.id)
                      : [...prev, area.id],
                  )
                }
                className={cn(
                  "rounded-md border px-3 py-1 text-sm",
                  selectedAreas.includes(area.id)
                    ? "border-primary bg-primary text-primary-foreground"
                    : "hover:bg-accent",
                )}
              >
                {area.name}
              </button>
            ))}
          </div>
        </div>

        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <Label>教育经历</Label>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() =>
                setEducations((prev) => [
                  ...prev,
                  {
                    schoolName: "",
                    majorName: "",
                    degreeId: degreeOptions[0]?.id ?? null,
                    startDate: "",
                    endDate: "",
                    fullTime: true,
                    researchDirection: "",
                  },
                ])
              }
            >
              <Plus />
              添加
            </Button>
          </div>
          {educations.map((edu, index) => (
            <div key={index} className="grid gap-2 rounded-md border p-3 md:grid-cols-2">
              <Input
                placeholder="学校"
                value={edu.schoolName}
                onChange={(e) => updateEducation(index, { schoolName: e.target.value })}
              />
              <Input
                placeholder="专业"
                value={edu.majorName}
                onChange={(e) => updateEducation(index, { majorName: e.target.value })}
              />
              <select
                className="h-9 rounded-md border border-input bg-background px-3 text-sm"
                value={edu.degreeId ?? ""}
                onChange={(e) =>
                  updateEducation(index, { degreeId: e.target.value ? Number(e.target.value) : null })
                }
              >
                <option value="">学位</option>
                {degreeOptions.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.name}
                  </option>
                ))}
              </select>
              <Input
                type="date"
                value={edu.startDate}
                onChange={(e) => updateEducation(index, { startDate: e.target.value })}
              />
              <Input
                type="date"
                value={edu.endDate}
                onChange={(e) => updateEducation(index, { endDate: e.target.value })}
              />
              <Input
                placeholder="研究方向"
                value={edu.researchDirection}
                onChange={(e) => updateEducation(index, { researchDirection: e.target.value })}
              />
              <label className="flex items-center gap-2 text-sm">
                <input
                  type="checkbox"
                  checked={edu.fullTime}
                  onChange={(e) => updateEducation(index, { fullTime: e.target.checked })}
                />
                全日制
              </label>
              <Button
                type="button"
                variant="ghost"
                size="sm"
                className="justify-self-start text-destructive"
                onClick={() => setEducations((prev) => prev.filter((_, i) => i !== index))}
              >
                <Trash2 />
                删除
              </Button>
            </div>
          ))}
        </div>
      </div>

      {message && <p className="text-sm text-muted-foreground">{message}</p>}
      <Button onClick={() => save.mutate()} disabled={save.isPending}>
        {save.isPending ? "保存中..." : "保存资料"}
      </Button>
    </div>
  )
}

function ResourcePublishPanel() {
  const queryClient = useQueryClient()
  const [form, setForm] = useState({ name: "", remark: "" })
  const [fileId, setFileId] = useState<number | null>(null)
  const [message, setMessage] = useState<string | null>(null)

  const publish = useMutation({
    mutationFn: () => api.post("/resources", { name: form.name, fileId, remark: form.remark }),
    onSuccess: () => {
      setForm({ name: "", remark: "" })
      setFileId(null)
      setMessage("已发布")
      queryClient.invalidateQueries({ queryKey: ["resources"] })
    },
    onError: (err) => setMessage(err instanceof Error ? err.message : "发布失败"),
  })

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">发布学习资料</CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        <Input
          placeholder="资料名称"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
        />
        <Input
          placeholder="备注（可选）"
          value={form.remark}
          onChange={(e) => setForm({ ...form, remark: e.target.value })}
        />
        <FileUpload label="上传文件" onChange={(f: FileInfo) => setFileId(f.id)} />
        {message && <p className="text-sm text-muted-foreground">{message}</p>}
        <Button
          onClick={() => publish.mutate()}
          disabled={!form.name || !fileId || publish.isPending}
        >
          发布
        </Button>
      </CardContent>
    </Card>
  )
}
