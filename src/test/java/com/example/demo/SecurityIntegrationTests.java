package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerReturnsAccessAndRefreshTokens() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "new-reader@demo.com",
                                  "password": "123456",
                                  "fullName": "New Reader"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("READER"));
    }

    @Test
    void loginAndRefreshRotateRefreshToken() throws Exception {
        JsonNode loginPayload = readBody(mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "alice@demo.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn());

        String refreshToken = loginPayload.get("refreshToken").asText();

        JsonNode refreshPayload = readBody(mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "%s"
                                }
                                """.formatted(refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andReturn());

        assertThat(refreshPayload.get("refreshToken").asText()).isNotEqualTo(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "%s"
                                }
                                """.formatted(refreshToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicArticleEndpointsRemainAccessibleAndCanUseOptionalAuth() throws Exception {
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        mockMvc.perform(get("/api/articles/article-ai-agents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reactions.currentUserReaction").doesNotExist());

        String accessToken = loginAndGetAccessToken("alice@demo.com", "123456");
        mockMvc.perform(get("/api/articles/article-ai-agents")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reactions.currentUserReaction").value("LOVE"));
    }

    @Test
    void protectedRoutesRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/profile/me"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/articles/article-ai-agents/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Nice article"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanAccessProfileCommentAndReact() throws Exception {
        String accessToken = loginAndGetAccessToken("bob@demo.com", "123456");

        mockMvc.perform(get("/api/profile/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@demo.com"));

        mockMvc.perform(put("/api/profile/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Bob Tran Updated",
                                  "avatarUrl": "https://example.com/avatar.png",
                                  "bio": "Updated bio"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Bob Tran Updated"));

        mockMvc.perform(post("/api/articles/article-ai-agents/comments")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Security is wired correctly."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-bob"));

        mockMvc.perform(post("/api/articles/article-ai-agents/reactions")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reactionType": "WOW"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentUserReaction").value("WOW"));
    }

    @Test
    void applicationLayerDoesNotImportSpringApis() throws Exception {
        try (var files = Files.walk(Path.of("src/main/java/com/example/demo/application"))) {
            List<Path> javaFiles = files
                    .filter(path -> path.toString().endsWith(".java"))
                    .toList();

            for (Path javaFile : javaFiles) {
                String content = Files.readString(javaFile);
                assertThat(content)
                        .as("application layer file should not import Spring: " + javaFile)
                        .doesNotContain("org.springframework");
                assertThat(content)
                        .as("application layer file should not use @Service: " + javaFile)
                        .doesNotContain("@Service");
            }
        }
    }

    private String loginAndGetAccessToken(String email, String password) throws Exception {
        JsonNode payload = readBody(mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn());
        return payload.get("accessToken").asText();
    }

    private JsonNode readBody(MvcResult mvcResult) throws IOException, com.fasterxml.jackson.core.JsonProcessingException {
        return objectMapper.readTree(mvcResult.getResponse().getContentAsString());
    }
}
