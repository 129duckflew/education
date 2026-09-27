package cn.duckflew.education.qa;

import cn.duckflew.education.AbstractIntegrationTest;
import cn.duckflew.education.professor.ProfessorProfile;
import cn.duckflew.education.professor.ProfessorProfileRepository;
import cn.duckflew.education.security.JwtService;
import cn.duckflew.education.taxonomy.ConsultArea;
import cn.duckflew.education.taxonomy.ConsultAreaRepository;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class QaFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProfessorProfileRepository professorProfileRepository;
    @Autowired
    ConsultAreaRepository consultAreaRepository;
    @Autowired
    JwtService jwtService;

    private Long areaId;
    private User author;
    private User professor;
    private User stranger;
    private String authorToken;
    private String professorToken;
    private String strangerToken;

    @BeforeEach
    void setUp() {
        ConsultArea area = consultAreaRepository.findAll().get(0);
        areaId = area.getId();

        author = createUser("author", UserRole.USER);
        professor = createUser("professor", UserRole.PROFESSOR);
        stranger = createUser("stranger", UserRole.USER);

        ProfessorProfile profile = new ProfessorProfile();
        profile.setUserId(professor.getId());
        profile.setApproved(true);
        profile.setConsultPrice(new BigDecimal("50.00"));
        professorProfileRepository.save(profile);

        authorToken = jwtService.issueAccessToken(author);
        professorToken = jwtService.issueAccessToken(professor);
        strangerToken = jwtService.issueAccessToken(stranger);
    }

    @Test
    void fullQuestionAnswerLikeFlow() throws Exception {
        // 提问
        MvcResult submitted = mockMvc.perform(post("/api/questions")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"如何准备考研","description":"请给些建议","areaIds":[%d],"professorIds":[%d]}
                                """.formatted(areaId, professor.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.question.status").value("AUDITING"))
                .andReturn();
        Long questionId = ((Number) JsonPath.read(
                submitted.getResponse().getContentAsString(), "$.data.question.id")).longValue();

        // 教授作答
        MvcResult answered = mockMvc.perform(post("/api/answers")
                        .header("Authorization", "Bearer " + professorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"questionId":%d,"content":"建议提前规划"}
                                """.formatted(questionId)))
                .andExpect(status().isOk())
                .andReturn();
        Long answerId = ((Number) JsonPath.read(
                answered.getResponse().getContentAsString(), "$.data")).longValue();

        // 作者查看：回答数 1
        mockMvc.perform(get("/api/questions/" + questionId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answers.length()").value(1))
                .andExpect(jsonPath("$.data.answers[0].content").value("建议提前规划"));

        // 作者给回答点赞 + 收藏
        mockMvc.perform(post("/api/answers/" + answerId + "/like")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/answers/" + answerId + "/collect")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk());

        // 点赞问题
        mockMvc.perform(post("/api/questions/" + questionId + "/like")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk());

        // 再次查看：计数与我的状态
        mockMvc.perform(get("/api/questions/" + questionId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.question.likeCount").value(1))
                .andExpect(jsonPath("$.data.question.liked").value(true))
                .andExpect(jsonPath("$.data.answers[0].likeCount").value(1))
                .andExpect(jsonPath("$.data.answers[0].collected").value(true));

        // 重复点赞幂等
        mockMvc.perform(post("/api/questions/" + questionId + "/like")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/questions/" + questionId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(jsonPath("$.data.question.likeCount").value(1));
    }

    @Test
    void otherUserCannotSeePendingQuestion() throws Exception {
        MvcResult submitted = mockMvc.perform(post("/api/questions")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"私有问题","description":"待审核","areaIds":[%d],"professorIds":[%d]}
                                """.formatted(areaId, professor.getId())))
                .andExpect(status().isOk())
                .andReturn();
        Long questionId = ((Number) JsonPath.read(
                submitted.getResponse().getContentAsString(), "$.data.question.id")).longValue();

        mockMvc.perform(get("/api/questions/" + questionId)
                        .header("Authorization", "Bearer " + strangerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void answerIsUpdatedNotDuplicated() throws Exception {
        MvcResult submitted = mockMvc.perform(post("/api/questions")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"回答问题","description":"x","areaIds":[%d],"professorIds":[%d]}
                                """.formatted(areaId, professor.getId())))
                .andExpect(status().isOk())
                .andReturn();
        Long questionId = ((Number) JsonPath.read(
                submitted.getResponse().getContentAsString(), "$.data.question.id")).longValue();

        for (String content : new String[]{"初稿", "修订稿"}) {
            mockMvc.perform(post("/api/answers")
                            .header("Authorization", "Bearer " + professorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"questionId":%d,"content":"%s"}
                                    """.formatted(questionId, content)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/questions/" + questionId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(jsonPath("$.data.answers.length()").value(1))
                .andExpect(jsonPath("$.data.answers[0].content").value("修订稿"));
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
