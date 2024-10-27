package api.ytter.backend.service;

import api.ytter.backend.database_model.UserEntity;
import api.ytter.backend.database_model.VerificationEntity;
import api.ytter.backend.database_repository.UserRepository;
import api.ytter.backend.database_repository.VerificationRepository;
import api.ytter.backend.model.LoginData;
import api.ytter.backend.model.RegistrationData;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey jwtSecret;
    private final MailService mailService;

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

            VerificationEntity verificationEntity = new VerificationEntity();
            verificationEntity.setUser(userRepository.findByUsername(registrationData.getUsername()).get());
            verificationEntity.setVerificationKey(UUID.randomUUID().toString().replace("-", "").substring(0, 30));
            verificationRepository.save(verificationEntity);

            mailService.sendVerificationCode(registrationData.getEmail(), verificationEntity.getVerificationKey());
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
                if(userEntity.getIsVerified() == false){
                    throw new RuntimeException(); // unverified
                }
            } else {
                userEntity = userRepository.findByEmail(loginData.getEmail()).orElseThrow(RuntimeException::new);
            }
            if(passwordEncoder.matches(loginData.getPassword(), userEntity.getHashedPassword())){
                if(userEntity.getIsVerified()) {
                    JWT = generateJWT(userEntity.getUsername(), userEntity.getIsAdmin());
                } else {
                    throw new RuntimeException();
                }
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
        VerificationEntity verificationEntity = verificationRepository.findByVerificationKey(verificationKey)
                .orElseThrow(RuntimeException::new);
        UserEntity userEntity = userRepository.getReferenceById(verificationEntity.getUser().getId());
        userEntity.setIsVerified(true);
        userRepository.save(userEntity);
        verificationRepository.delete(verificationEntity);
    }
}
