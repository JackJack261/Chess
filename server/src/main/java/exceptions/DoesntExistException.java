package exceptions;

public class DoesntExistException extends RuntimeException {
    DoesntExistException() {
        super();
    }

    public DoesntExistException(String message) {
        super(message);
    }
}
