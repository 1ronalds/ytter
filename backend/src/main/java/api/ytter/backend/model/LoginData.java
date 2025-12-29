package api.ytter.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// klase apraksta JSON datu struktūru, kas tiek atgriezta klientam un objektu ar ko darbojas kods

@AllArgsConstructor
@Setter
@Getter
public class LoginData {
    private String username;
    private String email;
    private String password;

}
