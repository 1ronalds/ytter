package api.ytter.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class CommentData {

    public CommentData(Long rootPostId, Long replyToCommentId, String comment) {
        this.rootPostId = rootPostId;
        this.replyToCommentId = replyToCommentId;
        this.comment = comment;
    }

    Long commentId;
    Long userId;
    Long rootPostId;
    Long replyToCommentId;
    Long likeCount;
    Long replyCount;
    String comment;
    Date timestamp;
    private Boolean liked;
}
