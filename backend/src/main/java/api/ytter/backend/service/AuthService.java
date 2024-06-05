package api.ytter.backend.service;

import api.ytter.backend.database_model.UserEntity;
import api.ytter.backend.database_repository.UserRepository;
import api.ytter.backend.model.LoginData;
import api.ytter.backend.model.RegistrationData;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey jwtSecret;

    @Value("signing-password")
    String signingPassword;

    public void registerUser(RegistrationData registrationData){
        if(
                userRepository.findByEmail(registrationData.getEmail()).isEmpty() &&
                userRepository.findByUsername(registrationData.getUsername()).isEmpty()
        ){
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(registrationData.getEmail());
            userEntity.setUsername(registrationData.getUsername());
            userEntity.setHashedPassword(passwordEncoder.encode(registrationData.getPassword()));
            userEntity.setIsAdmin(false);
            userEntity.setIsVerified(false);
            userRepository.save(userEntity);
        } else {
            throw new RuntimeException();
        }
    }

    public String checkLoginAndGenerateToken(LoginData loginData){
        String JWT = "";
        if(loginData.getUsername() != null || loginData.getPassword() != null){
            UserEntity userEntity;
            if(loginData.getUsername() != null){
                userEntity = userRepository.findByUsername(loginData.getUsername()).orElseThrow(RuntimeException::new);
            } else {
                userEntity = userRepository.findByEmail(loginData.getEmail()).orElseThrow(RuntimeException::new);
            }
            if(passwordEncoder.matches(loginData.getPassword(), userEntity.getHashedPassword())){
                JWT = generateJWT(userEntity.getUsername(), userEntity.getIsAdmin());
            } else {
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
        return JWT;
    }

    private String generateJWT(String username, Boolean isAdmin){
        Date currentDate = new Date();
        System.out.println("Date: " + Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()));
        return String.valueOf(Jwts.builder()
                .subject(username)
                .claim("admin", isAdmin ? "yes" : "no")
                .expiration(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).plusDays(1).toInstant()))
                .signWith(jwtSecret)
                .compact());
    }

    public void verifyEmail(String verificationKey){

    }
}
