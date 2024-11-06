package api.ytter.backend;

import api.ytter.backend.model.CommentData;
import api.ytter.backend.model.LoginData;
import api.ytter.backend.model.NotificationData;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.matcher.StringMatcher;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NotificationTests {
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
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM notifications");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("DELETE FROM follow");
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('ronalds', 'ronalds', '%s', 'ronalds@test.com', true, false)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('user2', 'user', '%s', 'user2@test.com', true, false)""", passwordHash));
        jdbcTemplate.execute(String.format("""
                INSERT INTO posts (id, author, text, reply_count, image_id, timestamp_, like_count, reyeet_count, reported)
                VALUES (1234, 1, 'Hello, a post', 0, null, '%s', 1, 0, false)""", getTodayDateTime()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO notifications (user_id, description, link, is_read , timestamp_ )
                VALUES (1, 'Your comment/post has a reply', 'https://ytter.lv/comment/to-post/2', true, '%s')""", getTodayDateTimeMinus20Min()));
        jdbcTemplate.execute(String.format("""
                INSERT INTO notifications (user_id, description, link, is_read , timestamp_ )
                VALUES (1, 'Your comment/post has a reply', 'https://ytter.lv/comment/to-post/1', false, '%s')""", getTodayDateTime()));
    }

    @Test
    void testGetUnreadNotifications() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(get("/notifications/unread")
                .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(jsonPath("$[0].description").value("Your comment/post has a reply"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetAllNotifications() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(get("/notifications/all")
                        .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(jsonPath("$[0].description").value("Your comment/post has a reply"))
                .andExpect(jsonPath("$[1].description").value("Your comment/post has a reply"))
                .andExpect(jsonPath("$", hasSize(2)));
    }
    @Test
    void testGetUnreadNotificationCount() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(get("/notifications/unread-count")
                        .header("Authorization", JWTtoken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void testIfReplyNotificationsWork() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        ObjectMapper objectMapper = new ObjectMapper();
        CommentData commentData = new CommentData(1234L, null, "test comment");
        String commentDataJsonString = objectMapper.writeValueAsString(commentData);

        mockMvc.perform(post("/comment/to-post/create")
                        .header("Authorization", JWTtoken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDataJsonString));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM notifications WHERE link = 'https://ytter.lv/comment/to-post/1234' AND user_id = 1", Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testIfFollowNotificationsWork() throws Exception {
        String JWTtoken = loginAndGetJWT("ronalds", "password123");

        mockMvc.perform(post("/profile/user2/follow")
                        .header("Authorization", JWTtoken));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM notifications WHERE link = 'https://ytter.lv/posts/profile/ronalds' AND user_id = 2", Integer.class);
        assertThat(count).isEqualTo(1);
    }
}
