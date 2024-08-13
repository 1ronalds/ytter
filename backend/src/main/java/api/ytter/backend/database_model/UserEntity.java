package api.ytter.backend.database_model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    @Column
    private String hashedPassword;
    @Column(name = "is_admin")
    private Boolean isAdmin;
    @Column(name = "is_verified")
    private Boolean isVerified;
}
