package repository;

import domain.Friendship;
import domain.Tuple;
import validation.Validator;

/**
 * Defines a database repository placeholder for friendship entities.
 *
 * <p>Database persistence is reserved for a future
 * implementation. Unsupported operations throw
 * UnsupportedOperationException.</p>
 *
 * @see Repository
 */
public class FriendshipDatabaseRepository implements Repository<Tuple<Long, Long>, Friendship>{

    private final Validator<Friendship> validator;

    /**
     * Creates a database repository for friendships.
     *
     * @param validator validator for frienship entities
     */
    public FriendshipDatabaseRepository(Validator<Friendship> validator) {
        this.validator = validator;
    }


    /**
     * Retrieves a friendship from the database by its identifier.
     *
     * <p>The friendship is identified by a tuple containing
     * the identifiers of the two users involved in the relationship.</p>
     *
     * @param id the tuple identifying the friendship to retrieve
     * @return the matching friendship, or null if no friendship
     *         with the specified identifier exists
     * @throws UnsupportedOperationException if database operations
     *         are not implemented yet
     */
    @Override
    public Friendship findOne(Tuple<Long, Long> id){
        throw new UnsupportedOperationException("Database operations are not implemented yet.");
    }

    /**
     * Retrieves all friendships stored in the database.
     *
     * @return an iterable collection of friendships
     * @throws UnsupportedOperationException if database operations
     *         are not implemented yet
     */
    @Override
    public Iterable<Friendship> findAll(){
        throw new UnsupportedOperationException("Database operations are not implemented yet.");
    }

    /**
     * Saves a new friendship in the database.
     *
     * @param entity the friendship to save
     * @return null if saved successfully, or the existing friendship
     *         if its identifier is already present
     * @throws UnsupportedOperationException if database operations
     *         are not implemented yet
     */
    @Override
    public Friendship save(Friendship entity){
        throw new UnsupportedOperationException("Database operations are not implemented yet.");
    }

    /**
     * Deletes a friendship from the database using its identifier.
     *
     * @param id the tuple identifying the friendship to delete
     * @return the deleted friendship, or null if it does not exist
     * @throws UnsupportedOperationException if database operations
     *         are not implemented yet
     */
    @Override
    public Friendship delete(Tuple<Long, Long> id){
        throw new UnsupportedOperationException("Database operations are not implemented yet.");
    }

    /**
     * Updates an existing friendship in the database.
     *
     * @param entity the friendship containing the updated information
     * @return null if updated successfully, or the provided friendship
     *         if its identifier does not exist
     * @throws UnsupportedOperationException if database operations
     *         are not implemented yet
     */
    @Override
    public Friendship update(Friendship entity){
        throw new UnsupportedOperationException("Database operations are not implemented yet.");
    }
}
