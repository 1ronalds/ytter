package api.ytter.backend.controller;

import api.ytter.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthTestEndpoints {
    private final AuthService authService;

    @GetMapping("/test/anyone-access")
    public ResponseEntity<String> accessToAnyone(){

        return null;
    }

    @GetMapping("/test/logged-in-access")
    public ResponseEntity<String> accessToLoggedIn(){

        return null;
    }

    @GetMapping("/test/admin-access")
    public ResponseEntity<String> accessToAdmin(){

        return null;
    }
}
