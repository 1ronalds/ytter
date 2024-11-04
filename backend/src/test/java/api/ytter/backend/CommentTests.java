package api.ytter.backend;

import api.ytter.backend.model.CommentData;
import api.ytter.backend.model.LoginData;
import api.ytter.backend.model.PostData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentTests {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    private String getTodayDateTime(){
        LocalDateTime today = LocalDateTime.now();
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String loginAndGetJWT(String username, String password) throws Exception {
        LoginData loginData = new LoginData(username, null, password);
        ObjectMapper objectMapper = new ObjectMapper();
        String loginDataAsJsonString = objectMapper.writeValueAsString(loginData);
        return mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDataAsJsonString))
                .andReturn().getResponse().getHeader("Authorization");
    }



    @BeforeEach
    void setUp(){
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM comments");

        String passwordHash = passwordEncoder.encode("password123");
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('ronalds', 'ronalds', '%s', 'ronalds@test.com', true, false)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO POSTS (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (1234, 1, 'Hello, this is my post', 0, null, '%s', 1, 0, false)""", getTodayDateTime()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO comments (id, author, root_post, reply_to_comment, comment, reply_count, like_count, timestamp_, reported)
                VALUES (4321, 1, 1234, null, 'my comment to post', 1, 0, '%s', false)""", getTodayDateTime()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO comments (id, author, root_post, reply_to_comment, comment, reply_count, like_count, timestamp_, reported)
                VALUES (5321, 1, 1234, 4321, 'my comment to comment', 0, 0, '%s', false)""", getTodayDateTime()));
    }

    @Test
    void getCommentsToPost() throws Exception {
        mockMvc.perform(get("/comment/to-post/1234"))
                .andDo(print())
                .andExpect(jsonPath("$[0].comment").value("my comment to post"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getCommentsToComment() throws Exception {
        mockMvc.perform(get("/comment/to-comment/4321"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("my comment to comment"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testPostCommentToPost() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        ObjectMapper objectMapper = new ObjectMapper();
        CommentData commentData = new CommentData(1234L, null, "test comment");
        String commentDataJsonString = objectMapper.writeValueAsString(commentData);

        mockMvc.perform(post("/comment/to-post/create")
                .header("Authorization", JWTtoken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentDataJsonString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("test comment"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments WHERE root_post = 1234", Integer.class);
        assertThat(count).isEqualTo(3);
    }

    @Test
    void testPostCommentToComment() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        ObjectMapper objectMapper = new ObjectMapper();
        CommentData commentData = new CommentData(1234L, 5321L, "test comment 2");
        String commentDataJsonString = objectMapper.writeValueAsString(commentData);

        mockMvc.perform(post("/comment/to-comment/create")
                        .header("Authorization", JWTtoken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDataJsonString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("test comment 2"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments WHERE root_post = 1234 AND reply_to_comment = 5321", Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testDeleteCommentNoReplies() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(delete("/comment/delete/5321")
                .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comments WHERE root_post = 1234", Integer.class);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void testDeleteCommentWithReplies() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(delete("/comment/delete/4321")
                        .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());

        String comment = jdbcTemplate.queryForObject("SELECT comment FROM comments WHERE id = 4321", String.class);
        assertThat(comment).isEqualTo("[deleted]");
    }

}