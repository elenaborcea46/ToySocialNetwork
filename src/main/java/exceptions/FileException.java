package exceptions;


/**
 * Signals an error while reading, parsing,
 * or writing persistent data files.
 */
public class FileException extends RuntimeException{

    /**
     * Creates a file exception with a message and an original cause.
     *
     * @param message the error message
     * @param cause the exception that caused the file operation to fail
     */
    public FileException(String message, Throwable cause){
        super(message, cause);
        //cause este exceptia originala care a provocat problema
    }
}
