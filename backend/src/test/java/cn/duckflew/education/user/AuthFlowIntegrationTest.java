package cn.duckflew.education.user;

import cn.duckflew.education.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    VerificationCodeRepository verificationCodeRepository;

    @Test
    void registerThenLoginThenAccessProfile() throws Exception {
        String email = "alice@example.com";
        sendCode(email);

        String registerBody = """
                {"username":"alice","email":"%s","password":"secret123","code":"%s"}
                """.formatted(email, latestCode(email));

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.username").value("alice"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andReturn();

        String accessToken = JsonPath.read(registerResult.getResponse().getContentAsString(), "$.data.accessToken");

        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.role").value("USER"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"account":"alice","password":"secret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    void registerWithWrongCodeIsRejected() throws Exception {
        String email = "bob@example.com";
        sendCode(email);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"bob","email":"%s","password":"secret123","code":"000000"}
                                """.formatted(email)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void duplicateEmailIsRejected() throws Exception {
        String email = "carol@example.com";
        sendCode(email);
        String code = latestCode(email);
        String body = """
                {"username":"carol","email":"%s","password":"secret123","code":"%s"}
                """.formatted(email, code);

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        sendCode(email);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"carol2","email":"%s","password":"secret123","code":"%s"}
                                """.formatted(email, latestCode(email))))
                .andExpect(status().isConflict());
    }

    @Test
    void loginWithWrongPasswordIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"account":"nobody","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void replacingExistingTokenIsNotRequiredButOldPasswordStillFails() throws Exception {
        String email = "dave@example.com";
        sendCode(email);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"dave","email":"%s","password":"secret123","code":"%s"}
                                """.formatted(email, latestCode(email))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"account":"dave@example.com","password":"secret123"}
                                """))
                .andExpect(status().isOk());
    }

    private void sendCode(String email) throws Exception {
        mockMvc.perform(post("/api/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"target":"%s","channel":"EMAIL","purpose":"REGISTER"}
                                """.formatted(email)))
                .andExpect(status().isOk());
    }

    private String latestCode(String target) {
        return verificationCodeRepository
                .findFirstByTargetAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(target, "REGISTER")
                .orElseThrow()
                .getCode();
    }
}
