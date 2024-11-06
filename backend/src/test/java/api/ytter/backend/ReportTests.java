package api.ytter.backend;

import api.ytter.backend.model.FlagData;
import api.ytter.backend.model.LoginData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReportTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    private String loginAndGetJWT(String username, String password) throws Exception {
        LoginData loginData = new LoginData(username, null, password);
        ObjectMapper objectMapper = new ObjectMapper();
        String loginDataAsJsonString = objectMapper.writeValueAsString(loginData);
        return mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDataAsJsonString))
                .andReturn().getResponse().getHeader("Authorization");
    }


    private String getTodayDateTime(){
        LocalDateTime today = LocalDateTime.now();
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getTodayDateTimeMinus20Min(){
        LocalDateTime today = LocalDateTime.now().minusMinutes(20);
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @BeforeEach
    void setUp() {
        String passwordHash = passwordEncoder.encode("password123");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('ronalds', 'ronalds', '%s', 'ronalds@test.com', true, false)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('adminuser', 'user', '%s', 'user@test.com', true, true)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO posts (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (2234, 1, 'not already reported post', 0, null, '%s', 0, 0, false)""", getTodayDateTimeMinus20Min()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO posts (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (3234, 1, 'already reported post', 0, null, '%s', 0, 0, true)""", getTodayDateTime()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO comments (id, author, root_post, reply_to_comment, comment, reply_count, like_count, timestamp_, reported)
                VALUES (4321, 1, 2234, null, 'not already reported comment', 1, 0, '%s', false)""", getTodayDateTimeMinus20Min()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO comments (id, author, root_post, reply_to_comment, comment, reply_count, like_count, timestamp_, reported)
                VALUES (5321, 1, 2234, null, 'already reported comment', 0, 0, '%s', true)""", getTodayDateTime()));
    }

    @Test
    void testReportPost() throws Exception{
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(post("/report/post/2234")
                .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());

        Boolean value = jdbcTemplate.queryForObject("SELECT reported FROM posts WHERE id = 2234", Boolean.class);
        assertThat(value).isEqualTo(true);
    }

    @Test
    void testReportComment() throws Exception{
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(post("/report/comment/4321")
                        .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());

        Boolean value = jdbcTemplate.queryForObject("SELECT reported FROM comments WHERE id = 4321", Boolean.class);
        assertThat(value).isEqualTo(true);
    }

    @Test
    void testGetReported() throws Exception {
        String JWTtoken = loginAndGetJWT("adminuser", "password123");
        mockMvc.perform(get("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("already reported post"))
                .andExpect(jsonPath("$[1].text").value("already reported comment"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testDeleteReportFromPost() throws Exception {
        String JWTtoken = loginAndGetJWT("adminuser", "password123");

        mockMvc.perform(delete("/report/post/3234")
                        .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());

        Boolean value = jdbcTemplate.queryForObject("SELECT reported FROM posts WHERE id = 3234", Boolean.class);
        assertThat(value).isEqualTo(false);
    }

    @Test
    void testDeleteReportFromComment() throws Exception {
        String JWTtoken = loginAndGetJWT("adminuser", "password123");

        mockMvc.perform(delete("/report/comment/5321")
                        .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());

        Boolean value = jdbcTemplate.queryForObject("SELECT reported FROM comments WHERE id = 5321", Boolean.class);
        assertThat(value).isEqualTo(false);
    }
}
