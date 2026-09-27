import { useRef, useState } from "react"
import { Paperclip, Upload, X } from "lucide-react"
import { api } from "@/lib/api"
import type { FileInfo } from "@/lib/types"
import { Button } from "@/components/ui/button"

export function FileUpload({
  onChange,
  accept,
  label = "选择文件",
  hint,
}: {
  onChange: (file: FileInfo) => void
  accept?: string
  label?: string
  hint?: string
}) {
  const inputRef = useRef<HTMLInputElement>(null)
  const [uploading, setUploading] = useState(false)
  const [uploaded, setUploaded] = useState<FileInfo | null>(null)
  const [error, setError] = useState<string | null>(null)

  async function handleChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0]
    if (!file) return
    setUploading(true)
    setError(null)
    try {
      const formData = new FormData()
      formData.append("file", file)
      const info = await api.upload<FileInfo>("/files", formData)
      setUploaded(info)
      onChange(info)
    } catch (err) {
      setError(err instanceof Error ? err.message : "上传失败")
    } finally {
      setUploading(false)
      if (inputRef.current) inputRef.current.value = ""
    }
  }

  return (
    <div className="space-y-1">
      <input ref={inputRef} type="file" accept={accept} hidden onChange={handleChange} />
      {uploaded ? (
        <div className="flex items-center gap-2 text-sm">
          <Paperclip className="size-4" />
          <span>{uploaded.originalName}</span>
          <Button
            type="button"
            variant="ghost"
            size="icon"
            onClick={() => setUploaded(null)}
            title="移除"
          >
            <X className="size-4" />
          </Button>
        </div>
      ) : (
        <Button
          type="button"
          variant="outline"
          size="sm"
          disabled={uploading}
          onClick={() => inputRef.current?.click()}
        >
          <Upload />
          {uploading ? "上传中..." : label}
        </Button>
      )}
      {hint && !uploaded && <p className="text-xs text-muted-foreground">{hint}</p>}
      {error && <p className="text-xs text-destructive">{error}</p>}
    </div>
  )
}
