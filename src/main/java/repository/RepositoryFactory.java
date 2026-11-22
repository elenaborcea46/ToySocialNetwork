package repository;

import domain.User;
import domain.Friendship;
import domain.Tuple;
import validation.Validator;

import java.util.Map;
import java.util.EnumMap;
import java.util.function.Supplier;

/**
 * Creates repositories according to the selected
 * persistence mechanism.
 *
 * <p>Centralizes the instantiation of concrete repositories
 * and allows the application to depend on the generic
 * Repository interface.</p>
 *
 * <p>Implements the Simple Factory design pattern.</p>
 *
 * @see Repository
 * @see RepositoryType
 */
public class RepositoryFactory {

    /**
     * Prevents instantiation of this utility class.
     */
     private RepositoryFactory() {}

    /**
     * Creates a repository for users.
     *
     * @param type selected persistence type
     * @param fileName path to the users file
     * @param validator validator for users
     * @return the created user repository
     */
    public static Repository<Long, User> createUserRepository(RepositoryType type, String fileName, Validator<User> validator, String url, String user, String password){
        if(type == null){
            throw new IllegalArgumentException("Repository type cannot be null");
        }

        Map<RepositoryType, Supplier<Repository<Long, User>>> suppliers = new EnumMap<>(RepositoryType.class);

        suppliers.put(RepositoryType.MEMORY, () -> new InMemoryRepository<>(validator));

        suppliers.put(RepositoryType.FILE, () -> new UserFileRepository(fileName, validator));

        suppliers.put(RepositoryType.DATABASE, () -> new UserDatabaseRepository(validator, url, user, password));

        Supplier<Repository<Long, User>> supplier = suppliers.get(type);

        if(supplier == null){
            throw new IllegalArgumentException("unsupported repository type: " + type);
        }

        return supplier.get();
    }


    /**
     * Creates a repository for friendships.
     *
     * @param type selected persistence type
     * @param fileName path to the friendships file
     * @param validator validator for friendships
     * @return the created friendship repository
     */
    public static Repository<Tuple<Long, Long>, Friendship> createFriendshipRepository(RepositoryType type, String fileName, Validator<Friendship> validator,  String url, String user, String password){

        if(type == null){
            throw new IllegalArgumentException("Repository type cannot be null.");
        }

        Map<RepositoryType, Supplier<Repository<Tuple<Long, Long>, Friendship>>> suppliers = new EnumMap<>(RepositoryType.class);

        suppliers.put(RepositoryType.MEMORY, () -> new InMemoryRepository<>(validator));

        suppliers.put(RepositoryType.FILE, () -> new FriendshipFileRepository(fileName, validator));

        suppliers.put(RepositoryType.DATABASE, () -> new FriendshipDatabaseRepository(validator, url, user, password));

        Supplier<Repository<Tuple<Long, Long>, Friendship>> supplier = suppliers.get(type);

        if(supplier == null){
            throw new IllegalArgumentException("unsupported repository type: " + type);
        }

        return supplier.get();

    }
}
