package api.ytter.backend.controller;

import api.ytter.backend.model.LoginData;
import api.ytter.backend.model.RegistrationData;
import api.ytter.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Klase satur funkcijas, kas tiek izsauktas, kad tiek veikts REST API pieprasījums uz kādu no saitēm. Ievāc parametrus un lietotāja datus
// izmantojot JWT. (@RequestAttribute). Pēctam izsauc servisa funkciju, kas atgriež datus. Dati tiek automātiski konvertēti uz JSON formātu

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
    public ResponseEntity<Void> register(@Valid @RequestBody RegistrationData registrationData){
        authService.registerUser(registrationData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/verify/{verificationKey}")
    public ResponseEntity<Void> verify(@PathVariable String verificationKey){
        authService.verifyEmail(verificationKey);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/whatismyname")
    public ResponseEntity<String> getNameOfUsername(@RequestAttribute String username){
        return new ResponseEntity<String>(authService.getNameOfUsername(username), HttpStatus.OK);
    }

    @GetMapping("/doesuserexist/{username}")
    public ResponseEntity<Boolean> doesUserExist(@PathVariable String username){
        return new ResponseEntity<>(authService.getDoesUserExist(username), HttpStatus.OK);
    }

}
