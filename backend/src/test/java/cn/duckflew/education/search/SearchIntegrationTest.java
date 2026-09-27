package cn.duckflew.education.search;

import cn.duckflew.education.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class SearchIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void aggregatedSearchReturnsGroupedResults() throws Exception {
        mockMvc.perform(get("/api/public/search").param("keyword", "考研"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.keyword").value("考研"))
                // 演示数据中存在考研相关问题与学习指南
                .andExpect(jsonPath("$.data.questions.length()").value(org.hamcrest.Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.data.guides.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    void searchProfessorsByName() throws Exception {
        mockMvc.perform(get("/api/public/search").param("keyword", "张伟"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.professors[0].realName").value("张伟"));
    }

    @Test
    void blankKeywordReturnsEmpty() throws Exception {
        mockMvc.perform(get("/api/public/search").param("keyword", "  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questions.length()").value(0))
                .andExpect(jsonPath("$.data.professors.length()").value(0))
                .andExpect(jsonPath("$.data.guides.length()").value(0));
    }
}
