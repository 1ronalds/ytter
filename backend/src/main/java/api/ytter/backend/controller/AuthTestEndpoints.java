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
public class AuthTestEndpoints {
    private final AuthService authService;

    @GetMapping("/test/anyone-access")
    public ResponseEntity<String> accessToAnyone(){
        return new ResponseEntity<>("success - access to anyone", HttpStatus.OK);
    }

    @GetMapping("/test/logged-in-access")
    public ResponseEntity<String> accessToLoggedIn(@RequestAttribute String username){
        return new ResponseEntity<>("success - access to logged in, " + username, HttpStatus.OK);
    }

    @GetMapping("/test/admin-access")
    public ResponseEntity<String> accessToAdmin(@RequestAttribute String isAdmin){
        if(isAdmin.equals("no")){
            throw new RuntimeException();
        }

        return new ResponseEntity<>("success - admin access logged in", HttpStatus.OK);
    }
}
