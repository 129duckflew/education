export type UserRole = "USER" | "PROFESSOR" | "ADMIN"

export interface UserProfile {
  id: number
  username: string | null
  email: string | null
  phone: string | null
  nickname: string | null
  realName: string | null
  role: UserRole
  avatarFileId: number | null
  gender: string | null
  birthday: string | null
  enabled: boolean
  createdAt: string
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: UserProfile
}

export interface ConsultArea {
  id: number
  name: string
  parentId: number | null
  sortOrder: number
}

export interface ConsultAreaNode extends ConsultArea {
  children: ConsultAreaNode[]
}

export interface QuestionCard {
  id: number
  title: string
  description: string | null
  status: "AUDITING" | "NORMAL" | "FORBIDDEN" | "REJECTED" | "PRIVATE"
  authorId: number
  authorName: string
  areaIds: number[]
  areaNames: string[]
  imageFileIds: number[]
  likeCount: number
  answerCount: number
  commentCount: number
  liked: boolean
  createdAt: string
}

export interface AnswerView {
  id: number
  professorId: number
  professorName: string
  content: string
  likeCount: number
  collectCount: number
  commentCount: number
  liked: boolean
  collected: boolean
  createdAt: string
}

export interface QuestionDetail {
  question: QuestionCard
  answers: AnswerView[]
}

export interface ProfessorSummary {
  userId: number
  realName: string
  avatarFileId: number | null
  jobRankName: string | null
  introduction: string | null
  consultPrice: number
  areaNames: string[]
  rating: number
  reviewCount: number
}

export interface ProfessorDetail {
  summary: ProfessorSummary
  educations: {
    id: number
    schoolName: string
    majorName: string | null
    degreeName: string | null
    startDate: string | null
    endDate: string | null
    fullTime: boolean
    researchDirection: string | null
  }[]
  reviews: {
    id: number
    userId: number
    userName: string
    rating: number
    content: string | null
    createdAt: string
  }[]
}

export interface NewsView {
  id: number
  title: string
  content: string | null
  coverFileId: number | null
  priority: number
  indexShow: boolean
  createdAt: string
  sources: { id: number; title: string; url: string }[]
}

export interface GuideNode {
  id: number
  name: string
  parentId: number | null
  important: boolean
  sortOrder: number
  children: GuideNode[]
}

export interface ResourceView {
  id: number
  name: string
  fileId: number
  remark: string | null
  professorId: number
  professorName: string
  createdAt: string
}

export interface NotificationView {
  id: number
  fromUserId: number | null
  type: string
  resourceId: number | null
  relatedUserId: number | null
  read: boolean
  createdAt: string
}

export interface MessageView {
  id: number
  fromUserId: number
  toUserId: number
  content: string
  read: boolean
  createdAt: string
}

export interface ConversationView {
  peerId: number
  peerName: string
  lastMessage: string | null
  lastAt: string | null
  unread: number
}

export interface PayOrder {
  id: number
  orderNo: string
  userId: number
  amount: number
  status: "TO_PAY" | "PAID" | "CANCELLED" | "REFUNDED"
  subject: string | null
  createdAt: string
  paidAt: string | null
}

export interface University {
  id: number
  name: string
  province: string | null
  city: string | null
  level: string | null
  department: string | null
}

export interface FileInfo {
  id: number
  originalName: string
  contentType: string | null
  size: number
  url: string
}

export interface JobRank {
  id: number
  name: string
}

export interface Degree {
  id: number
  name: string
}

export interface UniversityMajor {
  id: number
  code: string | null
  name: string
  parentId: number | null
}

export interface ResearchDirection {
  id: number
  name: string
  majorId: number | null
}

export interface EducationView {
  id: number
  schoolName: string
  majorName: string | null
  degreeName: string | null
  startDate: string | null
  endDate: string | null
  fullTime: boolean
  researchDirection: string | null
}

export interface ProfessorSelfView {
  userId: number
  approved: boolean
  jobRankId: number | null
  introduction: string | null
  consultPrice: number
  cvFileId: number | null
  areaIds: number[]
  educations: EducationView[]
}

export type CommentTargetType = "QUESTION" | "ANSWER"

export interface CommentView {
  id: number
  targetType: CommentTargetType
  targetId: number
  userId: number
  userName: string
  userAvatar: string | null
  content: string
  parentId: number | null
  createdAt: string
  replies: CommentView[]
}

export interface SearchResult {
  keyword: string
  questions: QuestionCard[]
  professors: ProfessorSummary[]
  news: NewsView[]
  resources: ResourceView[]
  guides: GuideNode[]
}
