package api.ytter.backend.database_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class CommentEntity {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "root_post")
    private PostEntity rootPost;

    @ManyToOne
    @JoinColumn(name = "reply_to_comment")
    private CommentEntity replyToComment;

    @Column(name = "comment")
    private String comment;

    @Column(name = "reply_count")
    private Long replyCount;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "timestamp_")
    private Date timestamp;

    public void increaseReplyCount(){
        this.replyCount += 1;
    }
    public void decreaseReplyCount(){
        this.replyCount -= 1;
    }
    public void increaseLikeCount(){
        this.likeCount += 1;
    }
    public void decreaseLikeCount(){
        this.likeCount -= 1;
    }
}
