package exceptions;

/**
 * Signals an error related to repository operations,
 * data consistency, or persistence.
 *
 * <p>May preserve the original cause of an error
 * to support debugging and error reporting.</p>
 */
public class RepositoryException extends RuntimeException{

    /**
     * Creates a repository exception with a descriptive message.
     *
     * @param message the error message
     */
    public RepositoryException(String message){
        super(message);
    }

    /**
     * Creates a repository exception with a message
     * and the original cause.
     *
     * @param message the error message
     * @param cause the exception that caused the failure
     */
    public RepositoryException(String message, Throwable cause){
        super(message, cause);
    }
}
