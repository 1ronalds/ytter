package api.ytter.backend.database_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "likes")
@Getter
@Setter
@NoArgsConstructor
public class LikeEntity {
    public LikeEntity(UserEntity user, PostEntity post) {
        this.user = user;
        this.post = post;
    }

    public LikeEntity(UserEntity user, CommentEntity comment) {
        this.user = user;
        this.comment = comment;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;
}
