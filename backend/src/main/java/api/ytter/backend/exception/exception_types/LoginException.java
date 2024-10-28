package api.ytter.backend.exception.exception_types;

public class LoginException extends RuntimeException {
    public LoginException(String message){
        super(message);
    }
}
