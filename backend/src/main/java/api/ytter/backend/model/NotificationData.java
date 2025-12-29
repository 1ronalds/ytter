package api.ytter.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

// klase apraksta JSON datu struktūru, kas tiek atgriezta klientam un objektu ar ko darbojas kods

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationData {
    private String description;
    private String link;
    private Boolean read;
    private Date timestamp;
}