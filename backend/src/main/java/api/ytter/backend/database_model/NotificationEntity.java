package api.ytter.backend.database_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

// Klase apraksta objektu, kas tiek izmantots komunikācijā ar datubāzi. Apraksta datubāzes tabulu.

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @Column(name = "description")
    private String description;
    @Column(name = "link")
    private String link;
    @Column(name = "is_read")
    private Boolean read;
    @Column(name = "timestamp_")
    private Date timestamp;
}
