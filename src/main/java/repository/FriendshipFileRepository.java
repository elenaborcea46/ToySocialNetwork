package repository;

import domain.Friendship;
import domain.Tuple;
import validation.Validator;
import exceptions.FileException;

import java.time.LocalDateTime;


/**
 * Provides file-based persistence for friendship entities.
 *
 * <p>Each record contains two user identifiers and
 * the date and time when the friendship was created.</p>
 *
 * @see FileRepository
 */
public class FriendshipFileRepository extends FileRepository<Tuple<Long, Long>, Friendship>{

    /**
     * Creates a file-based friendship repository
     * and loads stored friendships.
     *
     * @param fileName the path of the friendships file
     * @param validator the validator for friendship entities
     */
    public FriendshipFileRepository(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
        loadFromFile();
    }


    /**
     * Parses a friendship from a semicolon-separated text record.
     *
     * @param line the text record containing friendship information
     * @return the parsed friendship
     * @throws exceptions.FileException if the record format is invalid
     */
    @Override
    protected Friendship extractEntity(String line){
        String[] fields = line.split(";", -1);

        if (fields.length != 3) {
            throw new FileException(
                    "Invalid friendship format at line: " + line,
                    null
            );
        }

        try {
            Long idUser1 = Long.parseLong(fields[0].trim());
            Long idUser2 = Long.parseLong(fields[1].trim());

            LocalDateTime friendsFrom = LocalDateTime.parse(fields[2].trim());

            return new Friendship(idUser1, idUser2, friendsFrom);

        } catch (NumberFormatException e) {
            throw new FileException(
                    "ID invalid in fisierul de prietenii: " + line,
                    e
            );
        }
    }


    /**
     * Converts a friendship into a semicolon-separated text record.
     *
     * @param friendship the friendship to serialize
     * @return the serialized friendship
     */
    @Override
    protected String createEntityAsString(Friendship friendship){
        return  friendship.getIdUser1() + ";" + friendship.getIdUser2() + ";" + friendship.getFriendsFrom();
    }

}
