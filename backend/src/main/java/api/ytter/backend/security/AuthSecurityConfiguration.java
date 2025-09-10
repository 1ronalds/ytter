package api.ytter.backend.security;

import api.ytter.backend.model.CommentData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebSecurity
public class AuthSecurityConfiguration{
    private String jwtSecret;

    @Bean
    public SecretKey jwtSecretKey(){
        if(jwtSecret == null){
            SecureRandom secureRandom = new SecureRandom();
            byte[] key = new byte[32];
            secureRandom.nextBytes(key);
            jwtSecret = Base64.getEncoder().encodeToString(key);
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
                    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                    configuration.setExposedHeaders(List.of("Authorization"));
                    configuration.setAllowCredentials(true);
                    return configuration;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new AuthFilter(jwtSecretKey()), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET, "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/profile/*/amifollowing").authenticated()
                        .requestMatchers(HttpMethod.GET, "/whatismyname").authenticated()
                        .requestMatchers(HttpMethod.GET, "/doesuserexist/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/verify/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/test/logged-in-access").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/admin/post/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/admin/comment/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/comment/delete/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/comment/to-post/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/comment/to-comment/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/comment/to-post/create").authenticated()
                        .requestMatchers(HttpMethod.POST, "/comment/to-comment/create").authenticated()
                        .requestMatchers(HttpMethod.POST, "/profile/*/follow").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/profile/*/follow").authenticated()
                        .requestMatchers(HttpMethod.GET, "/profile/*/following").permitAll()
                        .requestMatchers(HttpMethod.GET, "/profile/*/followers").permitAll()
                        .requestMatchers(HttpMethod.POST, "/post/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/comment/*/like").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/post/*/like").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/comment/*/like").authenticated()
                        .requestMatchers(HttpMethod.GET, "/notifications/unread").authenticated()
                        .requestMatchers(HttpMethod.GET, "/notifications/all").authenticated()
                        .requestMatchers(HttpMethod.GET, "/notifications/unread-count").authenticated()
                        .requestMatchers(HttpMethod.GET, "/posts/profile/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts/by-me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/posts/following-feed").authenticated()
                        .requestMatchers(HttpMethod.GET, "/posts/top/this-week").permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts/top/this-month").permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts/new").permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts/by-id/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/posts/upload").authenticated()
                        .requestMatchers(HttpMethod.GET, "/posts/images/*").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/posts/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/posts/*/ry").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/posts/*/ry").authenticated()
                        .requestMatchers(HttpMethod.GET, "/reyeet-feed").authenticated()
                        .requestMatchers(HttpMethod.GET, "/reyeets/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/report/post/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/report/comment/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/report").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/report/post/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/report/comment/*").authenticated()
                        .anyRequest().denyAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}