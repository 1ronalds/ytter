package api.ytter.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
    Long rootPostId;
    Long replyToCommentId;
    Long reyeetCount;
    Long likeCount;
    Long replyCount;
    String comment;
    private Boolean liked;
    private Boolean reyeeted;
}
