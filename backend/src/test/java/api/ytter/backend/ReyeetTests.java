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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReyeetTests {

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
    void setUp(){
        String passwordHash = passwordEncoder.encode("password123");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM reyeet");
        jdbcTemplate.execute("DELETE FROM follow");
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('ronalds', 'ronalds', '%s', 'ronalds@test.com', true, false)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('user2', 'user', '%s', 'user@test.com', true, false)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO posts (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (2234, 1, 'Hello, my 1st post', 0, null, '%s', 2, 0, false)""", getTodayDateTimeMinus20Min()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO posts (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (1234, 1, 'Hello, my 2nd post', 0, null, '%s', 1, 0, false)""", getTodayDateTime()));
        jdbcTemplate.execute("INSERT INTO reyeet (user_id, post_id) VALUES (1, 2234)");
        jdbcTemplate.execute("INSERT INTO follow (follower_id, following_id) VALUES (2, 1)");
    }

    @Test
    void testPostReyeet() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(post("/posts/1234/ry")
                .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reyeet WHERE user_id = 1 AND post_id = 1234", Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testDeleteReyeet() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(delete("/posts/2234/ry")
                        .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reyeet WHERE user_id = 1 AND post_id = 2234", Integer.class);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void getFollowingReyeetFeed() throws Exception {
        String JWTtoken = loginAndGetJWT("user2", "password123");
        mockMvc.perform(get("/reyeet-feed")
                        .header("Authorization", JWTtoken)
                        .param("limit", "100")
                        .param("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Hello, my 1st post"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getReyeetsByUsername() throws Exception {
        mockMvc.perform(get("/reyeets/ronalds")
                .param("limit", "100")
                .param("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Hello, my 1st post"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

}
