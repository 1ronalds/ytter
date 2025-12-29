package api.ytter.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// klase apraksta JSON datu struktūru, kas tiek atgriezta klientam un objektu ar ko darbojas kods

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePublicData {
    private String username;
    private String name;
}
