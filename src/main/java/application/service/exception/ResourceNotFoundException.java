package application.service.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String countryIsWrong) {
        super(countryIsWrong);
    }

    public ResourceNotFoundException(String message, Throwable e) {
        super(message, e);
    }
}
