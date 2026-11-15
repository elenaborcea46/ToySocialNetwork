package repository;

import domain.User;
import validation.Validator;
import exceptions.FileException;




/**
 * Provides file-based persistence for user entities.
 *
 * <p>Converts users to and from semicolon-separated
 * text records containing an identifier,
 * a first name, and a last name.</p>
 *
 * @see FileRepository
 */
public class UserFileRepository extends FileRepository<Long, User>{

    /**
     * Creates a file-based user repository and loads stored users.
     *
     * @param fileName the path of the users file
     * @param validator the validator for user entities
     */
    public UserFileRepository(String fileName, Validator<User> validator){
        super(fileName, validator);
        loadFromFile();
    }


    /**
     * Parses a user from a semicolon-separated text record.
     *
     * @param line the text record containing user information
     * @return the parsed user
     * @throws exceptions.FileException if the record format is invalid
     */
    @Override
    protected User extractEntity(String line){
        String[] fields = line.split(";",-1);

        if(fields.length != 3){
            throw new FileException("Invalid user format at line: " + line,null);
        }

        try{
            Long id = Long.parseLong(fields[0]);
            String firstName = fields[1].trim();
            String lastName = fields[2].trim();
            return new User(id, firstName, lastName);
        }catch(NumberFormatException e){
            throw new FileException("ID invalid in fisierul de utilizatori: " + line,e);
        }
    }

    /**
     * Converts a user into a semicolon-separated text record.
     *
     * @param user the user to serialize
     * @return the serialized user
     */
    @Override
    protected String createEntityAsString(User user){
        return user.getId() + ";" + user.getFirstName() + ";" + user.getLastName();
    }
}
