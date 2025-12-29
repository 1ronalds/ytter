package api.ytter.backend.exception;

import api.ytter.backend.exception.exception_types.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // funkcija apraksta Internal Server Error, kas tiek atgriezts ja notiek nedefinēta kļūda
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrors(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // funkcija apraksta Bad Request, kas tiek atgriezts, ja notiek kāda definēta kļūda ievaddatos
    @ExceptionHandler({AuthorizationException.class, InvalidDataException.class,
                        LoginException.class, RegistrationException.class, VerificationException.class})
    public ResponseEntity<ErrorResponse> handleUserInputExceptions(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad request"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
