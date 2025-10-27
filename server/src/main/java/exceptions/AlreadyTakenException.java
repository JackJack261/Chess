package exceptions;

/**
 * Exception thrown when a resource (like a username) is already taken.
 */
public class AlreadyTakenException extends RuntimeException {

    /**
     * Constructs a new AlreadyTakenException with null as its detail message.
     */
    public AlreadyTakenException() {
        // Calls the constructor of the RuntimeException parent class
        super();
    }

    /**
     * Constructs a new AlreadyTakenException with the specified detail message.
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public AlreadyTakenException(String message) {
        // Calls the constructor of the RuntimeException parent class with a message
        super(message);
    }
}