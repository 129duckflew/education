package cn.duckflew.education.conversation;

import cn.duckflew.education.AbstractIntegrationTest;
import cn.duckflew.education.professor.ProfessorProfile;
import cn.duckflew.education.professor.ProfessorProfileRepository;
import cn.duckflew.education.security.JwtService;
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
class ConversationIntegrationTest extends AbstractIntegrationTest {

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
    private User student;
    private User professor;
    private User stranger;
    private String studentToken;
    private String professorToken;
    private String strangerToken;

    @BeforeEach
    void setUp() {
        areaId = consultAreaRepository.findAll().get(0).getId();
        student = createUser("student", UserRole.USER);
        professor = createUser("professor", UserRole.PROFESSOR);
        stranger = createUser("stranger", UserRole.USER);

        ProfessorProfile profile = new ProfessorProfile();
        profile.setUserId(professor.getId());
        profile.setApproved(true);
        profile.setConsultPrice(new BigDecimal("50.00"));
        professorProfileRepository.save(profile);

        studentToken = jwtService.issueAccessToken(student);
        professorToken = jwtService.issueAccessToken(professor);
        strangerToken = jwtService.issueAccessToken(stranger);
    }

    @Test
    void fullConversationFlow() throws Exception {
        // 教授回答学生问题 -> 建立互动关系
        answerStudentQuestion("建议提前一年准备");

        // 学生打开与教授的会话
        long conversationId = openConversation(studentToken, professor.getId());

        // 发送消息
        MvcResult sent = mockMvc.perform(post("/api/conversations/" + conversationId + "/messages")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"老师您好，请问参考书怎么选？","type":"TEXT","clientMsgId":"c-1"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("老师您好，请问参考书怎么选？"))
                .andExpect(jsonPath("$.data.type").value("TEXT"))
                .andExpect(jsonPath("$.data.status").value("NORMAL"))
                .andReturn();
        long messageId = ((Number) JsonPath.read(
                sent.getResponse().getContentAsString(), "$.data.id")).longValue();

        // 幂等：相同 clientMsgId 重发不产生新消息
        mockMvc.perform(post("/api/conversations/" + conversationId + "/messages")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"重复发送","type":"TEXT","clientMsgId":"c-1"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(messageId));

        // 教授视角：会话列表未读 1，对端是学生
        mockMvc.perform(get("/api/conversations")
                        .header("Authorization", "Bearer " + professorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(conversationId))
                .andExpect(jsonPath("$.data[0].peerId").value(student.getId()))
                .andExpect(jsonPath("$.data[0].unread").value(1))
                .andExpect(jsonPath("$.data[0].lastMessage").value("老师您好，请问参考书怎么选？"));

        // 教授未读总数 1
        mockMvc.perform(get("/api/conversations/unread-count")
                        .header("Authorization", "Bearer " + professorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));

        // 教授标记已读
        mockMvc.perform(post("/api/conversations/" + conversationId + "/read")
                        .header("Authorization", "Bearer " + professorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"upToMessageId\":%d}".formatted(messageId)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/conversations/unread-count")
                        .header("Authorization", "Bearer " + professorToken))
                .andExpect(jsonPath("$.data").value(0));

        // 教授回复
        mockMvc.perform(post("/api/conversations/" + conversationId + "/messages")
                        .header("Authorization", "Bearer " + professorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"建议用王道系列","type":"TEXT","clientMsgId":"p-1"}
                                """))
                .andExpect(status().isOk());

        // 学生查看消息（升序，2 条）
        mockMvc.perform(get("/api/conversations/" + conversationId + "/messages")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list.length()").value(2))
                .andExpect(jsonPath("$.data.list[0].content").value("老师您好，请问参考书怎么选？"))
                .andExpect(jsonPath("$.data.list[1].content").value("建议用王道系列"))
                .andExpect(jsonPath("$.data.hasMore").value(false));
    }

    @Test
    void strangerWithoutInteractionIsRejected() throws Exception {
        mockMvc.perform(post("/api/conversations")
                        .header("Authorization", "Bearer " + strangerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"peerId\":%d}".formatted(professor.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    void nonMemberCannotReadConversation() throws Exception {
        answerStudentQuestion("回答");
        long conversationId = openConversation(studentToken, professor.getId());

        mockMvc.perform(get("/api/conversations/" + conversationId + "/messages")
                        .header("Authorization", "Bearer " + strangerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void cannotOpenConversationWithSelf() throws Exception {
        mockMvc.perform(post("/api/conversations")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"peerId\":%d}".formatted(student.getId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mediaMessageRequiresExistingFile() throws Exception {
        answerStudentQuestion("回答");
        long conversationId = openConversation(studentToken, professor.getId());

        // IMAGE 缺 fileId
        mockMvc.perform(post("/api/conversations/" + conversationId + "/messages")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"IMAGE\"}"))
                .andExpect(status().isBadRequest());

        // 文件不存在
        mockMvc.perform(post("/api/conversations/" + conversationId + "/messages")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"IMAGE\",\"fileId\":999999}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void senderCanRecallOwnMessage() throws Exception {
        answerStudentQuestion("回答");
        long conversationId = openConversation(studentToken, professor.getId());
        MvcResult sent = mockMvc.perform(post("/api/conversations/" + conversationId + "/messages")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"发错了\",\"clientMsgId\":\"r-1\"}"))
                .andExpect(status().isOk())
                .andReturn();
        long messageId = ((Number) JsonPath.read(
                sent.getResponse().getContentAsString(), "$.data.id")).longValue();

        mockMvc.perform(post("/api/conversations/" + conversationId
                        + "/messages/" + messageId + "/recall")
                        .header("Authorization", "Bearer " + professorToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/conversations/" + conversationId
                        + "/messages/" + messageId + "/recall")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/conversations/" + conversationId + "/messages")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(jsonPath("$.data.list[0].status").value("RECALLED"));
    }

    private void answerStudentQuestion(String content) throws Exception {
        MvcResult submitted = mockMvc.perform(post("/api/questions")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"考研如何择校","description":"请指导","areaIds":[%d],"professorIds":[%d]}
                                """.formatted(areaId, professor.getId())))
                .andExpect(status().isOk())
                .andReturn();
        long questionId = ((Number) JsonPath.read(
                submitted.getResponse().getContentAsString(), "$.data.question.id")).longValue();

        mockMvc.perform(post("/api/answers")
                        .header("Authorization", "Bearer " + professorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionId\":%d,\"content\":\"%s\"}".formatted(questionId, content)))
                .andExpect(status().isOk());
    }

    private long openConversation(String token, Long peerId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/conversations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"peerId\":%d}".formatted(peerId)))
                .andExpect(status().isOk())
                .andReturn();
        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.data")).longValue();
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
