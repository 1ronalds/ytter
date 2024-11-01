package api.ytter.backend;

import api.ytter.backend.model.LoginData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PostTests {
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

    private String getTodayTimeMinusTwentyMin(){
        LocalDateTime today = LocalDateTime.now().minusMinutes(20);
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getTodayTimeMinusFortyMin(){
        LocalDateTime today = LocalDateTime.now().minusMinutes(40);
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getSixDaysAgoDateTime(){
        LocalDateTime today = LocalDateTime.now().minusDays(6);
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getEightDaysAgoDateTime(){
        LocalDateTime today = LocalDateTime.now().minusDays(8);
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getTwentyDaysAgoDateTime(){
        LocalDateTime today = LocalDateTime.now().minusDays(20);
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getFortyDaysAgoDateTime(){
        LocalDateTime today = LocalDateTime.now().minusDays(40);
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @BeforeEach
    void setUp() {
        String passwordHash = passwordEncoder.encode("password123");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('ronalds', 'ronalds', '%s', 'ronalds@test.com', true, false)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('user2', 'user', '%s', 'user@test.com', true, false)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO POSTS (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (1234, 1, 'Hello, my 5th post', 0, null, '%s', 1, 0, false)""", getTodayDateTime()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO POSTS (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (2234, 1, 'Hello, my 4th post', 0, null, '%s', 2, 0, false)""", getSixDaysAgoDateTime()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO POSTS (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (3234, 1, 'Hello, my 3rd post', 0, null, '%s', 3, 0, false)""", getEightDaysAgoDateTime()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO POSTS (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (4234, 1, 'Hello, my 2nd post', 0, null, '%s', 4, 0, false)""", getTwentyDaysAgoDateTime()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO POSTS (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (5234, 1, 'Hello, my 1st post', 0, null, '%s', 5, 0, false)""", getFortyDaysAgoDateTime()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO POSTS (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (6234, 2, 'Hello, post by different user', 0, null, '%s', 0, 0, false)""", getTodayTimeMinusTwentyMin()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO POSTS (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (7234, 2, 'Hello, post by different user with image', 0, null, '%s', 0, 0, false)""", getTodayTimeMinusFortyMin()));
        jdbcTemplate.execute("""
                INSERT INTO follow (follower_id, following_id)
                VALUES (1, 2)""");
    }

    @Test
    void testGetPostsByUsername() throws Exception {
        mockMvc.perform(get("/posts/profile/ronalds")
                .param("limit", "100")
                .param("offset", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Hello, my 5th post"))
                .andExpect(jsonPath("$[1].text").value("Hello, my 4th post"))
                .andExpect(jsonPath("$[2].text").value("Hello, my 3rd post"))
                .andExpect(jsonPath("$[3].text").value("Hello, my 2nd post"))
                .andExpect(jsonPath("$[4].text").value("Hello, my 1st post"))
                .andExpect(jsonPath("$", hasSize(5)));
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

    @Test
    void testGetPostsByMe() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(get("/posts/by-me")
                        .header("Authorization", JWTtoken)
                        .param("limit", "100")
                        .param("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Hello, my 5th post"))
                .andExpect(jsonPath("$[1].text").value("Hello, my 4th post"))
                .andExpect(jsonPath("$[2].text").value("Hello, my 3rd post"))
                .andExpect(jsonPath("$[3].text").value("Hello, my 2nd post"))
                .andExpect(jsonPath("$[4].text").value("Hello, my 1st post"))
                .andExpect(jsonPath("$", hasSize(5)));

    }

    @Test
    void testGetFollowingFeed() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");
        mockMvc.perform(get("/posts/following-feed")
                        .header("Authorization", JWTtoken)
                        .param("limit", "100")
                        .param("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Hello, post by different user"))
                .andExpect(jsonPath("$[1].text").value("Hello, post by different user with image"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetTopPostsThisWeek() throws Exception {
        mockMvc.perform(get("/posts/top/this-week")
                        .param("limit", "100")
                        .param("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Hello, my 4th post"))
                .andExpect(jsonPath("$[1].text").value("Hello, my 5th post"))
                .andExpect(jsonPath("$[2].text").value("Hello, post by different user"))
                .andExpect(jsonPath("$[3].text").value("Hello, post by different user with image"))
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    void testGetTopPostsThisMonth() throws Exception {
        mockMvc.perform(get("/posts/top/this-month")
                        .param("limit", "100")
                        .param("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Hello, my 2nd post"))
                .andExpect(jsonPath("$[1].text").value("Hello, my 3rd post"))
                .andExpect(jsonPath("$[2].text").value("Hello, my 4th post"))
                .andExpect(jsonPath("$[3].text").value("Hello, my 5th post"))
                .andExpect(jsonPath("$[4].text").value("Hello, post by different user"))
                .andExpect(jsonPath("$[5].text").value("Hello, post by different user with image"))
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    void testGetNewPosts() throws Exception {
        mockMvc.perform(get("/posts/new")
                        .param("limit", "100")
                        .param("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Hello, my 5th post"))
                .andExpect(jsonPath("$[1].text").value("Hello, post by different user"))
                .andExpect(jsonPath("$[2].text").value("Hello, post by different user with image"))
                .andExpect(jsonPath("$[3].text").value("Hello, my 4th post"))
                .andExpect(jsonPath("$[4].text").value("Hello, my 3rd post"))
                .andExpect(jsonPath("$[5].text").value("Hello, my 2nd post"))
                .andExpect(jsonPath("$[6].text").value("Hello, my 1st post"))
                .andExpect(jsonPath("$", hasSize(7)));
    }

    @Test
    void testGetPostById() throws Exception {
        mockMvc.perform(get("/posts/by-id/1234"))
                .andDo(print())
                .andExpect(jsonPath("$.text").value("Hello, my 5th post"));
    }



}