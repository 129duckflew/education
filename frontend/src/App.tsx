import { Navigate, Route, Routes } from "react-router-dom"
import { Layout } from "@/components/layout/Layout"
import { AdminLayout } from "@/components/layout/AdminLayout"
import { RequireAuth } from "@/components/layout/RequireAuth"
import { HomePage } from "@/pages/HomePage"
import { LoginPage } from "@/pages/LoginPage"
import { RegisterPage } from "@/pages/RegisterPage"
import { QuestionsPage } from "@/pages/QuestionsPage"
import { QuestionDetailPage } from "@/pages/QuestionDetailPage"
import { AskQuestionPage } from "@/pages/AskQuestionPage"
import { ProfessorsPage } from "@/pages/ProfessorsPage"
import { ProfessorDetailPage } from "@/pages/ProfessorDetailPage"
import { NewsPage } from "@/pages/NewsPage"
import { NewsDetailPage } from "@/pages/NewsDetailPage"
import { GuidesPage } from "@/pages/GuidesPage"
import { ResourcesPage } from "@/pages/ResourcesPage"
import { MessagesPage } from "@/pages/MessagesPage"
import { ProfilePage } from "@/pages/ProfilePage"
import { NotFoundPage } from "@/pages/NotFoundPage"
import { AdminDashboardPage } from "@/pages/admin/AdminDashboardPage"
import { AdminQuestionsPage } from "@/pages/admin/AdminQuestionsPage"
import { AdminUsersPage } from "@/pages/admin/AdminUsersPage"
import { AdminProfessorsPage } from "@/pages/admin/AdminProfessorsPage"
import { AdminAreasPage } from "@/pages/admin/AdminAreasPage"
import { AdminNewsPage } from "@/pages/admin/AdminNewsPage"
import { AdminTaxonomyPage } from "@/pages/admin/AdminTaxonomyPage"
import { AdminGuidesPage } from "@/pages/admin/AdminGuidesPage"
import { ProfessorCenterPage } from "@/pages/ProfessorCenterPage"

export function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
        <Route path="questions" element={<QuestionsPage />} />
        <Route path="questions/:id" element={<QuestionDetailPage />} />
        <Route
          path="questions/new"
          element={
            <RequireAuth>
              <AskQuestionPage />
            </RequireAuth>
          }
        />
        <Route path="professors" element={<ProfessorsPage />} />
        <Route path="professors/:id" element={<ProfessorDetailPage />} />
        <Route
          path="professor/center"
          element={
            <RequireAuth>
              <ProfessorCenterPage />
            </RequireAuth>
          }
        />
        <Route path="news" element={<NewsPage />} />
        <Route path="news/:id" element={<NewsDetailPage />} />
        <Route path="guides" element={<GuidesPage />} />
        <Route path="resources" element={<ResourcesPage />} />
        <Route
          path="messages"
          element={
            <RequireAuth>
              <MessagesPage />
            </RequireAuth>
          }
        />
        <Route
          path="profile"
          element={
            <RequireAuth>
              <ProfilePage />
            </RequireAuth>
          }
        />
        <Route path="*" element={<NotFoundPage />} />
      </Route>

      <Route
        path="/admin"
        element={
          <RequireAuth role="ADMIN">
            <AdminLayout />
          </RequireAuth>
        }
      >
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<AdminDashboardPage />} />
        <Route path="questions" element={<AdminQuestionsPage />} />
        <Route path="users" element={<AdminUsersPage />} />
        <Route path="professors" element={<AdminProfessorsPage />} />
        <Route path="areas" element={<AdminAreasPage />} />
        <Route path="taxonomy" element={<AdminTaxonomyPage />} />
        <Route path="guides" element={<AdminGuidesPage />} />
        <Route path="news" element={<AdminNewsPage />} />
      </Route>
    </Routes>
  )
}
