package repository;

import domain.Friendship;
import domain.Tuple;
import validation.Validator;

import java.sql.*;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import exceptions.RepositoryException;

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
    private final String url;
    private final String user;
    private final String password;

    /**
     * Creates a database repository for friendships.
     *
     * @param validator validator for frienship entities
     */
    public FriendshipDatabaseRepository(Validator<Friendship> validator, String url, String user, String password) {

        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }


    /**
     * Retrieves a friendship from the database by its identifier.
     *
     * <p>The friendship is identified by a tuple containing
     * the identifiers of the two users involved in the relationship.</p>
     *
     * @param id the tuple identifying the friendship to retrieve
     * @return an Optional containing the friendship,
     *         or an empty Optional if it does not exist
     * @throws IllegalArgumentException if the identifier or its components are null
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id){
           if(id == null || id.getFirst() == null || id.getSecond() == null){
               throw new IllegalArgumentException("Friendship ID cannot be null.");
           }

           Long id1 = Math.min(id.getFirst(), id.getSecond());
           Long id2 = Math.max(id.getFirst(), id.getSecond());

           String sql = "SELECT id1, id2, friends_from FROM friendships WHERE id1 = ? AND id2 = ?";

           try(Connection connection = DriverManager.getConnection(url, user, password);
               PreparedStatement statement = connection.prepareStatement(sql)){
               statement.setLong(1, id1);
               statement.setLong(2, id2);

               try(ResultSet resultSet = statement.executeQuery()){

                   if(resultSet.next()){
                       Long userId1 = resultSet.getLong("id1");
                       Long userId2 = resultSet.getLong("id2");

                       LocalDateTime friendsFrom = resultSet.getTimestamp("friends_from").toLocalDateTime();

                       return Optional.of(new Friendship(userId1, userId2, friendsFrom));
                   }

                   return Optional.empty();

               }
           } catch(SQLException e){
               throw new RepositoryException("Error finding friendship with ID " + id, e);
           }
    }

    /**
     * Retrieves all friendships stored in the database.
     *
     *  @return an iterable collection of friendships,
     *          or an empty collection if no friendships exist
     *  @throws RepositoryException if a database error occurs
     */
    @Override
    public Iterable<Friendship> findAll(){
        List<Friendship> friendships = new ArrayList<>();

        String sql = "SELECT id1, id2, friends_from FROM friendships";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");

                LocalDateTime friendsFrom =
                        resultSet.getTimestamp("friends_from").toLocalDateTime();

                friendships.add(new Friendship(id1, id2, friendsFrom));
            }

        } catch (SQLException e) {
            throw new RepositoryException(
                    "Error retrieving friendships from database.", e
            );
        }

        return friendships;
    }

    /**
     * Saves a new friendship in the database.
     *
     * @param entity the friendship to save
     * @return an empty Optional if saved successfully,
     *         or an Optional containing the existing friendship
     *         if its identifier is already present
     * @throws IllegalArgumentException if the entity is null
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<Friendship> save(Friendship entity){
        if (entity == null) {
            throw new IllegalArgumentException("Friendship cannot be null.");
        }

        validator.validate(entity);

        Optional<Friendship> existingFriendship = findOne(entity.getId());

        if (existingFriendship.isPresent()) {
            return existingFriendship;
        }

        String sql = "INSERT INTO friendships(id1, id2, friends_from) VALUES(?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, entity.getIdUser1());
            statement.setLong(2, entity.getIdUser2());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getFriendsFrom()));

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected != 1) {
                throw new RepositoryException("Friendship was not inserted.");
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RepositoryException(
                    "Error saving friendship to database.", e
            );
        }
    }

    /**
     * Deletes a friendship from the database using its identifier.
     *
     * @param id the tuple identifying the friendship to delete
     * @return an Optional containing the deleted friendship,
     *         or an empty Optional if the friendship does not exist
     * @throws IllegalArgumentException if the identifier or its components are null
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id){
        if (id == null || id.getFirst() == null || id.getSecond() == null) {
            throw new IllegalArgumentException("Friendship ID cannot be null.");
        }

        Optional<Friendship> existingFriendship = findOne(id);

        if (existingFriendship.isEmpty()) {
            return Optional.empty();
        }

        Friendship friendship = existingFriendship.get();

        String sql = "DELETE FROM friendships WHERE id1 = ? AND id2 = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, friendship.getIdUser1());
            statement.setLong(2, friendship.getIdUser2());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                return Optional.empty();
            }

            return Optional.of(friendship);

        } catch (SQLException e) {
            throw new RepositoryException(
                    "Error deleting friendship with ID " + id, e
            );
        }
    }

    /**
     * Updates an existing friendship in the database.
     *
     * @param entity the friendship containing the updated information
     * @return an empty Optional if updated successfully,
     *         or an Optional containing the provided friendship
     *         if its identifier does not exist
     * @throws IllegalArgumentException if the entity is null
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<Friendship> update(Friendship entity){
        if (entity == null) {
            throw new IllegalArgumentException("Friendship cannot be null.");
        }

        validator.validate(entity);

        String sql = "UPDATE friendships SET friends_from = ? WHERE id1 = ? AND id2 = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(entity.getFriendsFrom()));
            statement.setLong(2, entity.getIdUser1());
            statement.setLong(3, entity.getIdUser2());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                return Optional.of(entity);
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RepositoryException(
                    "Error updating friendship with ID " + entity.getId(), e
            );
        }
    }
}
