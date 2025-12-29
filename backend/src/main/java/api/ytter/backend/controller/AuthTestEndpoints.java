package api.ytter.backend.controller;

import api.ytter.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController

// Klase satur funkcijas, kas tiek izsauktas, kad tiek veikts REST API pieprasījums uz kādu no saitēm. Ievāc parametrus un lietotāja datus
// izmantojot JWT. (@RequestAttribute). Pēctam izsauc servisa funkciju, kas atgriež datus. Dati tiek automātiski konvertēti uz JSON formātu

public class AuthTestEndpoints {
    private final AuthService authService;

    @GetMapping("/test/anyone-access")
    public ResponseEntity<String> accessToAnyone(){
        return new ResponseEntity<>("success - access to anyone", HttpStatus.OK);
    }

    @GetMapping("/test/logged-in-access")
    public ResponseEntity<String> accessToLoggedIn(@RequestAttribute String username){
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @GetMapping("/test/admin-access")
    public ResponseEntity<String> accessToAdmin(@RequestAttribute String isAdmin){
        if(isAdmin.equals("no")){
            throw new RuntimeException();
        }

        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
