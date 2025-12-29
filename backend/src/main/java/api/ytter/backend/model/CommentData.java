package api.ytter.backend.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

// klase apraksta JSON datu struktūru, kas tiek atgriezta klientam un objektu ar ko darbojas kods

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentData {

    public CommentData(Long rootPostId, Long replyToCommentId, String comment) {
        this.rootPostId = rootPostId;
        this.replyToCommentId = replyToCommentId;
        this.comment = comment;
    }

    public CommentData(String rootPostId, String replyToCommentId, String comment) {
        this.rootPostId = Long.parseLong(rootPostId);
        this.replyToCommentId = Long.parseLong(replyToCommentId);
        this.comment = comment;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    Long commentId;
    ProfilePublicData profilePublicData;
    @JsonSerialize(using = ToStringSerializer.class)
    Long rootPostId;
    @JsonSerialize(using = ToStringSerializer.class)
    Long replyToCommentId;
    Long likeCount;
    Long replyCount;
    String comment;
    Date timestamp;
    Boolean liked;
}
