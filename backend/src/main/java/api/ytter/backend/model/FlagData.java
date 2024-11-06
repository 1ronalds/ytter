package api.ytter.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FlagData {
    private String text;
    private Long postId;
    private Long commentId;
}
