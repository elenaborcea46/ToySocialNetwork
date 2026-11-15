package validation;

import domain.Friendship;
import exceptions.ValidationException;


/**
 * Validates friendship entities.
 *
 * <p>Ensures that user identifiers are valid and that
 * a user cannot establish a friendship with themselves.</p>
 *
 * <p>The existence of the users is checked separately
 * by the service layer.</p>
 *
 * @see Friendship
 * @see Validator
 */
public class FriendshipValidator implements Validator<Friendship> {


    /**
     * Creates a friendship validator.
     */
    public FriendshipValidator() {
    }

    /**
     * Validates a friendship entity.
     *
     * A friendship is valid if:
     * - the entity is not null;
     * - both user identifiers are positive;
     * - the two user identifiers are different;
     * - the friendship date is not null.
     *
     * This method does not check whether users exist
     * in the repository or whether the friendship
     * already exists.
     *
     * @param friendship the friendship to validate
     * @throws ValidationException if the friendship is invalid
     */
    @Override
    public void validate(Friendship friendship){

        if(friendship == null){
            throw new ValidationException("Prietenia nu poate fi null!");
        }

        if(friendship.getId() == null){
            throw new ValidationException("ID-ul prieteniei nu poate fi null!");
        }

        if(friendship.getIdUser1() == null || friendship.getIdUser1() <= 0){
            throw new ValidationException("ID-ul primului utilizator este invalid!");
        }

        if(friendship.getIdUser2() == null || friendship.getIdUser2() <= 0){
            throw new ValidationException("ID-ul celui de-al doilea utilizator este invalid!");
        }

        if(friendship.getIdUser1().equals(friendship.getIdUser2())){
            throw new ValidationException("Un utilizator nu poate fi prieten cu el insusi!");
        }

        if(friendship.getFriendsFrom() == null){
            throw new ValidationException("Data prieteniei nu poate fi null!");
        }
    }
}
