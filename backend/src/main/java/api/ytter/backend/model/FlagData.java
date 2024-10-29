package api.ytter.backend.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FlagData {
    private String text;
    private Long postId;
    private Long commentId;
}
