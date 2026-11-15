package validation;

import domain.User;
import exceptions.ValidationException;


/**
 * Validates user entities according to the application's
 * rules for identifiers and personal information.
 *
 * @see User
 * @see Validator
 */
public class UserValidator implements Validator<User>{

    /**
     * Creates a user validator.
     */
    public UserValidator() {
    }

    /**
     * Validates the specified user.
     *
     * @param user the user to validate
     * @throws exceptions.ValidationException if the user is invalid
     */
    @Override
    public void validate(User user){

        if(user == null){
            throw new ValidationException("Utilizatorul nu poate fi null!");
        }

        if(user.getId() == null || user.getId() <= 0){
            throw new ValidationException("ID-ul trebuie sa fie pozitiv!");
        }

        if(user.getFirstName() == null || user.getFirstName().isBlank()){
            throw new ValidationException("Prenumele nu poate fi gol!");
        }

        if(user.getLastName() == null || user.getLastName().isBlank()){
            throw new ValidationException("Numele nu poate fi gol!");
        }
    }
}
