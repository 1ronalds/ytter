package api.ytter.backend.database_model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Klase apraksta objektu, kas tiek izmantots komunikācijā ar datubāzi. Apraksta datubāzes tabulu.

@Setter
@Getter
@Entity
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "hashed_password", nullable = false, length = 255)
    private String hashedPassword;

    @Column(unique = true, name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin;
}
