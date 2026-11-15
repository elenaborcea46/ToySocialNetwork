package exceptions;


/**
 * Signals that an entity does not satisfy the
 * application's validation rules.
 */
public class ValidationException extends RuntimeException{

    /**
     * Creates a validation exception with a descriptive message.
     *
     * @param message the error message
     */
    public ValidationException(String message){
        super(message);//apelam constructorul clasei parinte, care memoreaza mesajul erorii
    }
}
