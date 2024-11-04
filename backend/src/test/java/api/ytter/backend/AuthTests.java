package api.ytter.backend;

import api.ytter.backend.model.LoginData;
import api.ytter.backend.model.RegistrationData;
import api.ytter.backend.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthTests {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    MailService mailService;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM verifications");
        jdbcTemplate.execute("DELETE FROM users");
    }


    @Test
    void testRegistration() throws Exception {
        RegistrationData registrationData = new RegistrationData("ronalds", "ronalds", "ronalds@test.com", "parole123");
        ObjectMapper objectMapper = new ObjectMapper();
        String registrationDataAsJsonString = objectMapper.writeValueAsString(registrationData);

        Mockito.doNothing().when(mailService).sendVerificationCode(any(), any());

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationDataAsJsonString))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        Mockito.verify(mailService, Mockito.times(1)).sendVerificationCode(any(), any());

        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, "ronalds");
        assertThat(count).isEqualTo(1);

        String sql2 = "SELECT COUNT(*) FROM verifications WHERE user_id = (SELECT id FROM users WHERE username = ?)";
        Integer count2 = jdbcTemplate.queryForObject(sql2, Integer.class, "ronalds");
        assertThat(count2).isEqualTo(1);
    }

    @Test
    void testVerification() throws Exception {
        String verificationKey = "ABC123XYZ";
        String sql = String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin) VALUES ('testUser', 'userTest', 'password123', 'testUser@test.com', false, false);
                INSERT INTO verifications (verification_key, user_id) VALUES ('%s', 1);
                """, verificationKey);
        jdbcTemplate.execute(sql);

        mockMvc.perform(get("/verify/" + verificationKey))
                .andDo(print())
                .andExpect(status().isOk());

        String sql2 = "SELECT COUNT(*) FROM verifications WHERE verification_key = ?";
        Integer count = jdbcTemplate.queryForObject(sql2, Integer.class, verificationKey);
        assertThat(count).isEqualTo(0);

        String sql3 = "SELECT is_verified FROM users";
        Boolean isVerified = jdbcTemplate.queryForObject(sql3, Boolean.class);
        assertThat(isVerified).isEqualTo(true);
    }

    @Test
    void loginTest() throws Exception {
        String passwordHash = passwordEncoder.encode("password123");

        String sql = String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('testUser', 'userTest', '%s', 'testUser@test.com', true, false);
                """, passwordHash);
        jdbcTemplate.execute(sql);

        LoginData loginData = new LoginData("testUser", null, "password123");
        ObjectMapper objectMapper = new ObjectMapper();
        String loginDataAsJsonString = objectMapper.writeValueAsString(loginData);

        MvcResult result1 = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDataAsJsonString))
                .andDo(print())
                .andExpect(header().exists("Authorization"))
                .andReturn();

        String headerValue = result1.getResponse().getHeader("Authorization");

        mockMvc.perform(get("/test/logged-in-access")
                        .header("Authorization", headerValue))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }

    @Test
    void loginTestWithEmail() throws Exception {
        String passwordHash = passwordEncoder.encode("password123");

        String sql = String.format("""
                INSERT INTO users (username, name, hashed_password, email, is_verified, is_admin)
                VALUES ('testUser', 'userTest', '%s', 'testUser@test.com', true, false);
                """, passwordHash);
        jdbcTemplate.execute(sql);

        LoginData loginData = new LoginData(null, "testUser@test.com", "password123");
        ObjectMapper objectMapper = new ObjectMapper();
        String loginDataAsJsonString = objectMapper.writeValueAsString(loginData);

        MvcResult result1 = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDataAsJsonString))
                .andDo(print())
                .andExpect(header().exists("Authorization"))
                .andReturn();

        String headerValue = result1.getResponse().getHeader("Authorization");

        mockMvc.perform(get("/test/logged-in-access")
                        .header("Authorization", headerValue))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }
}
