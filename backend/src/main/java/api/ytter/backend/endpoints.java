package api.ytter.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class endpoints {
    @GetMapping("/api")
    public String hello(){
        return "Hello from spring boot";
    }
}
