package api.ytter.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

// klase apraksta kļūdas ziņojuma JSON datu struktūru

@AllArgsConstructor
@Data
public class ErrorResponse {
    private String message;
    private Integer status;
    private String error;
}
