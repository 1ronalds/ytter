package api.ytter.backend;

import api.ytter.backend.model.LoginData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.internal.database.hsqldb.HSQLDBTable;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FollowTests {
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

    @BeforeEach
    void setUp(){
        String passwordHash = passwordEncoder.encode("password123");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM follow");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('ronalds', 'ronalds', '%s', 'ronalds@test.com', true, false)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('user2', 'user', '%s', 'user@test.com', true, false)""", passwordHash));
        Long userId1 = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'ronalds'", Long.class);
        Long userId2 = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'user2'", Long.class);
        jdbcTemplate.execute(String.format("INSERT INTO follow (follower_id, following_id) VALUES (%s, %s)", userId1, userId2));
    }

    @Test
    void testFollow() throws Exception {
        String JWTtoken = loginAndGetJWT("user2", "password123");

        mockMvc.perform(post("/profile/ronalds/follow")
                .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());
        Long userId2 = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'user2'", Long.class);
        Integer count = jdbcTemplate.queryForObject(String.format("SELECT COUNT(*) FROM follow WHERE follower_id = %s", userId2), Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testUnfollow() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(delete("/profile/user2/follow")
                .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk());
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM follow WHERE follower_id = 1", Integer.class);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void testGetFollowing() throws Exception {
        mockMvc.perform(get("/profile/ronalds/following"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$[0].username").value("user2"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetFollowers() throws Exception {
        mockMvc.perform(get("/profile/user2/followers"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$[0].username").value("ronalds"))
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
