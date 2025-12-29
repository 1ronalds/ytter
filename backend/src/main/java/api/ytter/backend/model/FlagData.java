package api.ytter.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// klase apraksta JSON datu struktūru, kas tiek atgriezta klientam un objektu ar ko darbojas kods

@Getter
@Setter
@AllArgsConstructor
public class FlagData {
    private String text;
    private String postId;
    private String commentId;
}
