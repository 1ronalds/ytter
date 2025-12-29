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
public class ProfileFullPublicData {
    private String username;
    private String name;
    private Integer followers;
    private Integer following;
    private Integer posts;
    private Integer reyeets;
}
