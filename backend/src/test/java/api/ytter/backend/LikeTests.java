package api.ytter.backend;

import api.ytter.backend.model.LoginData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LikeTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    private String getTodayDateTime(){
        LocalDateTime today = LocalDateTime.now();
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getTodayDateTimeMinus20Min(){
        LocalDateTime today = LocalDateTime.now().minusMinutes(20);
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
    void setUp() {
        String passwordHash = passwordEncoder.encode("password123");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('ronalds', 'ronalds', '%s', 'ronalds@test.com', true, false)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('user2', 'user', '%s', 'user@test.com', true, false)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO posts (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (2234, 1, 'already liked post', 0, null, '%s', 2, 0, false)""", getTodayDateTimeMinus20Min()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO posts (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (1234, 1, 'not already liked post', 0, null, '%s', 1, 0, false)""", getTodayDateTime()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO comments (id, author, root_post, reply_to_comment, comment, reply_count, like_count, timestamp_, reported)
                VALUES (5321, 1, 1234, null, 'already liked comment', 0, 0, '%s', false)""", getTodayDateTimeMinus20Min()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO comments (id, author, root_post, reply_to_comment, comment, reply_count, like_count, timestamp_, reported)
                VALUES (6321, 1, 1234, null, 'not already liked comment', 0, 0, '%s', false)""", getTodayDateTime()));
        jdbcTemplate.execute("INSERT INTO likes (post_id, comment_id, user_id) VALUES (null, 5321, 1)");
        jdbcTemplate.execute("INSERT INTO likes (post_id, comment_id, user_id) VALUES (2234, null, 1)");
    }

    @Test
    void testLikePost() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(post("/post/1234/like")
                .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());


        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE post_id = 1234", Integer.class);
        assertThat(count).isEqualTo(1);

    }

    @Test
    void testLikeComment() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(post("/comment/6321/like")
                        .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());


        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE comment_id = 6321", Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testUnlikePost() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(delete("/post/2234/like")
                        .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());


        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE post_id = 2234", Integer.class);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void testUnlikeComment() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(delete("/comment/5321/like")
                        .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());


        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM likes WHERE comment_id = 5321", Integer.class);
        assertThat(count).isEqualTo(0);
    }

}
