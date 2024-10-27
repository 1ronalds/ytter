package api.ytter.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReyeetPostData {

    private Long postId;
    private ProfilePublicData profileDataPublic;
    private Long imageId;
    private Long replyCount;
    private Long likeCount;
    private Long reyeetCount;
    private String text;
    private Date timestamp;
    private Boolean liked;
    private Boolean reyeeted;
    private String reyeetedByName;
}
