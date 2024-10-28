package api.ytter.backend.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
public class NotificationData {
    private String description;
    private String link;
    private Boolean read;
    private Date timestamp;
}
