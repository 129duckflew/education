package cn.duckflew.education.messaging;

import cn.duckflew.education.AbstractIntegrationTest;
import cn.duckflew.education.qa.Answer;
import cn.duckflew.education.qa.AnswerRepository;
import cn.duckflew.education.qa.AnswerStatus;
import cn.duckflew.education.qa.Question;
import cn.duckflew.education.qa.QuestionRepository;
import cn.duckflew.education.qa.QuestionStatus;
import cn.duckflew.education.security.JwtService;
import cn.duckflew.education.user.User;
import cn.duckflew.education.user.UserRepository;
import cn.duckflew.education.user.UserRole;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 通知深链锚点：resourceId 统一为问题 id，anchor 指向回答/评论，
 * 使前端可从通知直达问题并定位到具体回答或评论。
 */
@AutoConfigureMockMvc
class NotificationDeepLinkIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    JwtService jwtService;

    private User author;
    private User professor;
    private User commenter;
    private String authorToken;
    private String professorToken;
    private String commenterToken;
    private Long questionId;

    @BeforeEach
    void setUp() {
        author = createUser("author", UserRole.USER);
        professor = createUser("professor", UserRole.PROFESSOR);
        commenter = createUser("commenter", UserRole.USER);
        authorToken = jwtService.issueAccessToken(author);
        professorToken = jwtService.issueAccessToken(professor);
        commenterToken = jwtService.issueAccessToken(commenter);

        Question question = new Question();
        question.setAuthorId(author.getId());
        question.setTitle("考研择校");
        question.setDescription("求助");
        question.setStatus(QuestionStatus.NORMAL);
        questionId = questionRepository.save(question).getId();
    }

    @Test
    void answerNotificationAnchorsToAnswer() throws Exception {
        long answerId = publishAnswer();

        Map<String, Object> n = firstNotification(authorToken, "ANSWER_RECEIVED");
        assertThat(number(n, "resourceId")).isEqualTo(questionId);
        assertThat(n.get("anchorType")).isEqualTo("ANSWER");
        assertThat(number(n, "anchorId")).isEqualTo(answerId);
    }

    @Test
    void questionCommentNotificationAnchorsToComment() throws Exception {
        long commentId = comment(commenterToken, "QUESTION", questionId, null, "同问");

        Map<String, Object> n = firstNotification(authorToken, "COMMENT_ON_QUESTION");
        assertThat(number(n, "resourceId")).isEqualTo(questionId);
        assertThat(n.get("anchorType")).isEqualTo("QUESTION_COMMENT");
        assertThat(number(n, "anchorId")).isEqualTo(commentId);
        assertThat(n.get("anchorRefId")).isNull();
    }

    @Test
    void answerCommentNotificationAnchorsToAnswerComment() throws Exception {
        long answerId = createAnswer();
        long commentId = comment(commenterToken, "ANSWER", answerId, null, "请问有书单吗");

        Map<String, Object> n = firstNotification(professorToken, "COMMENT_ON_ANSWER");
        assertThat(number(n, "resourceId")).isEqualTo(questionId);
        assertThat(n.get("anchorType")).isEqualTo("ANSWER_COMMENT");
        assertThat(number(n, "anchorId")).isEqualTo(commentId);
        assertThat(number(n, "anchorRefId")).isEqualTo(answerId);
    }

    @Test
    void replyNotificationAnchorsToReplyComment() throws Exception {
        long parentId = comment(commenterToken, "QUESTION", questionId, null, "同问");
        long replyId = comment(professorToken, "QUESTION", questionId, parentId, "已在上方解答");

        Map<String, Object> n = firstNotification(commenterToken, "REPLY_TO_COMMENT");
        assertThat(number(n, "resourceId")).isEqualTo(questionId);
        assertThat(n.get("anchorType")).isEqualTo("QUESTION_COMMENT");
        assertThat(number(n, "anchorId")).isEqualTo(replyId);
    }

    // ---------- helpers ----------

    private long publishAnswer() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/answers")
                        .header("Authorization", "Bearer " + professorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionId\":%d,\"content\":\"建议看学科评估\"}".formatted(questionId)))
                .andExpect(status().isOk())
                .andReturn();
        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.data")).longValue();
    }

    private long createAnswer() {
        Answer answer = new Answer();
        answer.setQuestionId(questionId);
        answer.setProfessorId(professor.getId());
        answer.setContent("建议看学科评估");
        answer.setStatus(AnswerStatus.NORMAL);
        return answerRepository.save(answer).getId();
    }

    private long comment(String token, String targetType, Long targetId, Long parentId, String content)
            throws Exception {
        String body = parentId == null
                ? "{\"targetType\":\"%s\",\"targetId\":%d,\"content\":\"%s\"}".formatted(targetType, targetId, content)
                : "{\"targetType\":\"%s\",\"targetId\":%d,\"content\":\"%s\",\"parentId\":%d}"
                .formatted(targetType, targetId, content, parentId);
        MvcResult result = mockMvc.perform(post("/api/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.data")).longValue();
    }

    private Map<String, Object> firstNotification(String token, String type) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/notifications")
                        .param("size", "100")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        List<Map<String, Object>> list = JsonPath.read(
                result.getResponse().getContentAsString(), "$.data.list");
        return list.stream()
                .filter(item -> type.equals(item.get("type")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未找到通知: " + type));
    }

    private long number(Map<String, Object> map, String key) {
        return ((Number) map.get(key)).longValue();
    }

    private User createUser(String name, UserRole role) {
        User user = new User();
        user.setUsername(name + "-" + System.nanoTime());
        user.setNickname(name);
        user.setRealName(name);
        user.setPasswordHash("$2a$10$abcdefghijklmnopqrstuv");
        user.setRole(role);
        user.setEnabled(true);
        return userRepository.save(user);
    }
}
