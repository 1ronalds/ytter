package api.ytter.backend.controller;

import api.ytter.backend.model.LoginData;
import api.ytter.backend.model.RegistrationData;
import api.ytter.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthEndpoints {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginData loginData){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authService.checkLoginAndGenerateToken(loginData));
        // If invalid login, checkLoginAndGenerateToken throws exception and handler returns error to client
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegistrationData registrationData){
        authService.validateAndRegisterUser(registrationData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
