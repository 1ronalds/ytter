package api.ytter.backend.exception.exception_types;


public class RegistrationException extends RuntimeException {
    public RegistrationException(String message){
        super(message);
    }
}