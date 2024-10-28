package api.ytter.backend.exception.exception_types;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message){
        super(message);
    }
}
