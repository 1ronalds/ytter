package api.ytter.backend.database_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="follow")
public class FollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private UserEntity follower;
    @ManyToOne
    @JoinColumn(name = "following_id")
    private UserEntity following;
}
