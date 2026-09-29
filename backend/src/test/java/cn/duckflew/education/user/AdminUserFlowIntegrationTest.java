package cn.duckflew.education.user;

import cn.duckflew.education.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 覆盖管理后台「用户管理」接口。
 * 回归：null keyword 曾走 {@code :keyword is null or lower(concat(...))}，
 * 被 PostgreSQL 解析为 lower(bytea) 而 500。
 */
@AutoConfigureMockMvc
@WithMockUser(authorities = {"ROLE_ADMIN", "user:manage"})
class AdminUserFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    @Test
    void listWithoutKeywordReturnsPagedUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users?size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list.length()").value(greaterThan(0)));
    }

    @Test
    void searchByKeywordWorks() throws Exception {
        String nickname = "搜索目标" + System.nanoTime();
        createUser(nickname, UserRole.USER);

        mockMvc.perform(get("/api/admin/users")
                        .param("size", "10")
                        .param("keyword", nickname))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].nickname").value(nickname));
    }

    @Test
    void filterByRoleWorks() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .param("size", "10")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].role").value("USER"));
    }

    @Test
    void searchByKeywordAndRoleWorks() throws Exception {
        String nickname = "角色目标" + System.nanoTime();
        createUser(nickname, UserRole.PROFESSOR);

        mockMvc.perform(get("/api/admin/users")
                        .param("size", "10")
                        .param("keyword", nickname)
                        .param("role", "PROFESSOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list.length()").value(1));

        mockMvc.perform(get("/api/admin/users")
                        .param("size", "10")
                        .param("keyword", nickname)
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list.length()").value(0));
    }

    @Test
    void toggleEnabledWorks() throws Exception {
        User target = createUser("启停目标" + System.nanoTime(), UserRole.USER);

        mockMvc.perform(patch("/api/admin/users/" + target.getId() + "/enabled?enabled=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(false));
    }

    private User createUser(String nickname, UserRole role) {
        User user = new User();
        user.setUsername("it-" + System.nanoTime());
        user.setNickname(nickname);
        user.setRealName(nickname);
        user.setPasswordHash("$2a$10$abcdefghijklmnopqrstuv");
        user.setRole(role);
        user.setEnabled(true);
        return userRepository.save(user);
    }
}
