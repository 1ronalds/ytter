package api.ytter.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

// klase apraksta JSON datu struktūru, kas tiek atgriezta klientam un objektu ar ko darbojas kods

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReyeetPostData {

    private String postId;
    private ProfilePublicData profilePublicData;
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
