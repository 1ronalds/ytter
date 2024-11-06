package api.ytter.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private String message;
    private Integer status;
    private String error;
}
