package api.ytter.backend.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginData {
    private String username;
    private String email;
    private String password;
}
