package repository;

import domain.User;
import validation.Validator;

/**
 * Defines a database repository placeholder for user entities.
 *
 * <p>Database persistence is not implemented in the
 * current laboratory version. Unsupported operations
 * throw UnsupportedOperationException.</p>
 *
 * @see Repository
 */
public class UserDatabaseRepository implements Repository<Long, User>{

    private final Validator<User> validator;

    /**
     * Creates a database repository for users.
     *
     * @param validator validator for user entities
     */
    public UserDatabaseRepository(Validator<User> validator){
        this.validator = validator;
    }


    /**
     * Retrieves a user from the database by their unique identifier.
     *
     * @param id the identifier of the user to retrieve
     * @return the matching user, or null if no user with
     *         the specified identifier exists
     * @throws UnsupportedOperationException if database operations
     *         are not implemented yet
     */
    @Override
    public User findOne(Long id){
        throw new UnsupportedOperationException("Database operations are not implemented yet.");
    }

    /**
     * Retrieves all users stored in the database.
     *
     * @return an iterable collection of users
     * @throws UnsupportedOperationException if database operations
     *         are not implemented yet
     */
    @Override
    public Iterable<User> findAll(){
        throw new UnsupportedOperationException("Database operations are not implemented yet.");
    }

    /**
     * Saves a new user in the database.
     *
     * @param entity the user to save
     * @return null if saved successfully, or the existing user
     *         if its identifier is already present
     * @throws UnsupportedOperationException if database operations
     *         are not implemented yet
     */
    @Override
    public User save(User entity){
        throw new UnsupportedOperationException("Database operations are not implemented yet.");
    }

    /**
     * Deletes a user from the database using their identifier.
     *
     * @param id the identifier of the user to delete
     * @return the deleted user, or null if the user does not exist
     * @throws UnsupportedOperationException if database operations
     *         are not implemented yet
     */
    @Override
    public User delete(Long id){
        throw new UnsupportedOperationException("Database operations are not implemented yet.");
    }

    /**
     * Updates an existing user in the database.
     *
     * @param entity the user containing the updated information
     * @return null if updated successfully, or the provided user
     *         if its identifier does not exist
     * @throws UnsupportedOperationException if database operations
     *         are not implemented yet
     */
    @Override
    public User update(User entity){
        throw new UnsupportedOperationException("Database operations are not implemented yet.");
    }
}
