package api.ytter.backend.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private Integer status;
    private String error;
}
