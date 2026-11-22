package repository;

import domain.User;
import validation.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import exceptions.RepositoryException;

/**
 * Provides a PostgreSQL database implementation of the Repository
 * interface for User entities.
 *
 * <p>Supports CRUD operations using JDBC, prepared statements,
 * and automatic resource management through try-with-resources.</p>
 *
 * <p>User identifiers are generated automatically by PostgreSQL
 * when new users are inserted.</p>
 *
 * @see Repository
 * @see User
 */
public class UserDatabaseRepository implements Repository<Long, User>{

    private final Validator<User> validator;
    private final String url;
    private final String user;
    private final String password;

    /**
     * Creates a database repository for user entities.
     *
     * @param validator the validator used to validate user entities
     * @param url the JDBC connection URL
     * @param user the database username
     * @param password the database password
     */
    public UserDatabaseRepository(Validator<User> validator, String url, String user, String password){
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }


    /**
     * Retrieves a user from the database by their unique identifier.
     *
     * @param id the identifier of the user to retrieve
     * @return an Optional containing the matching user,
     *         or an empty Optional if the user does not exist
     * @throws IllegalArgumentException if the id is null
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<User> findOne(Long id){

        if(id == null){
            throw new IllegalArgumentException("User id cannot be null.");
        }

        String sql = "SELECT id, first_name, last_name FROM users WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url,user, password);
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setLong(1, id);

            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()){
                    Long userId = resultSet.getLong("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");

                    return Optional.of(new User(userId, firstName, lastName));
                }

                return Optional.empty();
            }
        } catch(SQLException e){
            throw new RepositoryException("Error finding user with ID " + id, e);
        }
    }

    /**
     * Retrieves all users stored in the database.
     *
     * @return an iterable collection of users
     * @throws RepositoryException if a database error occurs
     *
     */
    @Override
    public Iterable<User> findAll(){
        List<User> users = new ArrayList<>();

        String sql = "SELECT id, first_name, last_name FROM users";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                users.add(new User(id, firstName, lastName));
            }

        } catch(SQLException e){
            throw new RepositoryException("Error retrieving users from database: " + e.getMessage());
        }

        return users;
    }

    /**
     * Saves a new user in the database.
     *
     * <p>The user identifier is generated automatically by PostgreSQL.</p>
     *
     * @param entity the user to save
     * @return an empty Optional if the user was saved successfully,
     *         or an Optional containing the existing user if its ID
     *         is already present
     * @throws IllegalArgumentException if the entity is null
     * @throws RepositoryException if a database error occurs
     *         or a non-existing ID is manually assigned
     */
    @Override
    public Optional<User> save(User entity){

        if(entity == null){
            throw new IllegalArgumentException("User cannot be null.");
        }

        validator.validate(entity);

        if(entity.getId() != null){
            Optional<User> existingUser = findOne(entity.getId());

            if(existingUser.isPresent()){
                return existingUser;
            }

            throw new RepositoryException("Cannot insert a user with a manually assigned ID.");
        }

        String sql = "INSERT INTO users(first_name, last_name) VALUES(?, ?)";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected != 1){
                throw new RepositoryException("User was not inserted.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong("id"));
                } else {
                    throw new RepositoryException("Generated user ID not found.");
                }
            }

            return Optional.empty();

        } catch(SQLException e){
            throw new RepositoryException("Error saving user to database: " + e.getMessage());
        }
    }

    /**
     * Deletes a user from the database using their identifier.
     *
     * @param id the identifier of the user to delete
     * @return an Optional containing the deleted user,
     *         or an empty Optional if the user does not exist
     * @throws IllegalArgumentException if the id is null
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<User> delete(Long id){
        if(id == null){
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        Optional<User> existingUser = findOne(id);

        if(existingUser.isEmpty()){
            return Optional.empty();
        }

        String sql = "DELETE FROM users WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setLong(1, id);

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected == 1){
                return existingUser;
            }

            return Optional.empty();
        } catch(SQLException e){
            throw new RepositoryException("Error deleting user from database: " +  e.getMessage());
        }
    }

    /**
     * Updates an existing user in the database.
     *
     * @param entity the user containing the updated information
     * @return an empty Optional if the update succeeds,
     *         or an Optional containing the provided user if it does not exist
     *  @throws IllegalArgumentException if the entity or its id is null
     *  @throws RepositoryException if a database error occurs
     *
     */
    @Override
    public Optional<User> update(User entity){
         if(entity == null){
             throw new IllegalArgumentException("User cannot be null.");
         }

         if(entity.getId() == null){
             throw new IllegalArgumentException("User ID cannot be null.");
         }

         validator.validate(entity);

         String sql = "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?";

         try(Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
             statement.setString(1, entity.getFirstName());
             statement.setString(2, entity.getLastName());
             statement.setLong(3, entity.getId());

             int rowsAffected = statement.executeUpdate();

             if(rowsAffected == 0){
                 return Optional.of(entity);
             }
             return Optional.empty();
         } catch(SQLException e){
             throw new RepositoryException("Error updating user from database: " + e.getMessage());
         }
    }
}
