package api.ytter.backend.database_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Klase apraksta objektu, kas tiek izmantots komunikācijā ar datubāzi. Apraksta datubāzes tabulu.

@Entity
@Table(name = "reyeet")
@Setter
@Getter
@NoArgsConstructor
public class ReyeetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;
}