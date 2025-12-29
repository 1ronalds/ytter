package api.ytter.backend.database_model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

// Klase apraksta objektu, kas tiek izmantots komunikācijā ar datubāzi. Apraksta datubāzes tabulu.

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class PostEntity {
    @Id
    private Long id;
    @Column(name = "text")
    private String text;
    @Column(name = "image_id")
    private Long imageId;
    @Column(name = "timestamp_")
    private Date timestamp;
    @Column(name = "reply_count")
    private Long replyCount;
    @Column(name= "like_count")
    private Long likeCount;
    @Column(name="reyeet_count")
    private Long reyeetCount;
    @ManyToOne
    @JoinColumn(name = "author")
    private UserEntity user;
    @Column(name = "reported")
    private Boolean reported = false;

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
    public void increaseReyeetCount(){
        this.reyeetCount += 1;
    }
    public void decreaseReyeetCount(){
        this.reyeetCount -= 1;
    }
}
