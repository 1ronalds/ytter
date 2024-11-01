package api.ytter.backend.database_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "verifications")
public class VerificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "verification_key")
    private String verificationKey;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}