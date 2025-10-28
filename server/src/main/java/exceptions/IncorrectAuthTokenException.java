package exceptions;

public class IncorrectAuthTokenException extends RuntimeException {

    public IncorrectAuthTokenException() {
        super();
    }

    public IncorrectAuthTokenException(String message) {
        super(message);
    }}
