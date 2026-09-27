import { Link } from "react-router-dom"
import { Heart, MessageSquare } from "lucide-react"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent } from "@/components/ui/card"
import type { QuestionCard } from "@/lib/types"

const statusLabel: Record<QuestionCard["status"], string> = {
  AUDITING: "审核中",
  NORMAL: "已发布",
  FORBIDDEN: "已禁止",
  REJECTED: "未通过",
  PRIVATE: "不公开",
}

export function QuestionListItem({ question }: { question: QuestionCard }) {
  return (
    <Card className="transition-shadow hover:shadow-md">
      <CardContent className="p-5">
        <Link to={`/questions/${question.id}`} className="block space-y-2">
          <div className="flex items-start justify-between gap-4">
            <h3 className="font-medium leading-snug">{question.title}</h3>
            <Badge variant={question.status === "NORMAL" ? "secondary" : "outline"}>
              {statusLabel[question.status]}
            </Badge>
          </div>
          {question.description && (
            <p className="line-clamp-2 text-sm text-muted-foreground">{question.description}</p>
          )}
          <div className="flex flex-wrap items-center gap-2 text-xs text-muted-foreground">
            {question.areaNames.map((name) => (
              <Badge key={name} variant="outline">
                {name}
              </Badge>
            ))}
            <span className="ml-auto flex items-center gap-3">
              <span className="flex items-center gap-1">
                <Heart className="size-3.5" />
                {question.likeCount}
              </span>
              <span className="flex items-center gap-1">
                <MessageSquare className="size-3.5" />
                {question.answerCount}
              </span>
              <span>{question.authorName}</span>
            </span>
          </div>
        </Link>
      </CardContent>
    </Card>
  )
}
