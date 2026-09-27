package cn.duckflew.education.comment;

import cn.duckflew.education.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CommentIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void commentReplyAndPermissionFlow() throws Exception {
        String studentToken = login("student@example.com", "password123");
        String profToken = login("prof@example.com", "password123");

        long questionId = firstQuestionId();

        // 学生评论问题
        MvcResult created = mockMvc.perform(post("/api/comments")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"targetType":"QUESTION","targetId":%d,"content":"同问，我也想知道"}
                                """.formatted(questionId)))
                .andExpect(status().isOk())
                .andReturn();
        long commentId = ((Number) JsonPath.read(
                created.getResponse().getContentAsString(), "$.data")).longValue();

        // 列表（公开）
        mockMvc.perform(get("/api/comments?targetType=QUESTION&targetId=" + questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].content").value("同问，我也想知道"))
                .andExpect(jsonPath("$.data[0].userName").value("小明"));

        // 教授回复该评论
        MvcResult reply = mockMvc.perform(post("/api/comments")
                        .header("Authorization", "Bearer " + profToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"targetType":"QUESTION","targetId":%d,"content":"已在上方详细解答","parentId":%d}
                                """.formatted(questionId, commentId)))
                .andExpect(status().isOk())
                .andReturn();
        long replyId = ((Number) JsonPath.read(reply.getResponse().getContentAsString(), "$.data")).longValue();

        mockMvc.perform(get("/api/comments?targetType=QUESTION&targetId=" + questionId))
                .andExpect(jsonPath("$.data[0].replies.length()").value(1))
                .andExpect(jsonPath("$.data[0].replies[0].content").value("已在上方详细解答"));

        // 评论数进入问题卡片
        mockMvc.perform(get("/api/questions/" + questionId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.question.commentCount").value(2));

        // 学生无权删除教授回复
        mockMvc.perform(delete("/api/comments/" + replyId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());

        // 学生可删除自己的评论
        mockMvc.perform(delete("/api/comments/" + commentId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    @Test
    void commentOnMissingTargetIsRejected() throws Exception {
        String token = login("student@example.com", "password123");
        mockMvc.perform(post("/api/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"targetType":"QUESTION","targetId":999999,"content":"x"}
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    private String login(String account, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"account":"%s","password":"%s"}
                                """.formatted(account, password)))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.accessToken");
    }

    private long firstQuestionId() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/public/questions?size=1"))
                .andExpect(status().isOk())
                .andReturn();
        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.list[0].id");
        assertThat(id).isNotNull();
        return id.longValue();
    }
}
