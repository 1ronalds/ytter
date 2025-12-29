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
@NoArgsConstructor
@AllArgsConstructor
public class PostData {

    public PostData(String text){
        this.text = text;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    private Long postId;
    private ProfilePublicData profilePublicData;
    private String imageId;
    private Long replyCount;
    private Long likeCount;
    private Long reyeetCount;
    private String text;
    private Date timestamp;
    private Boolean liked;
    private Boolean reyeeted;
}
