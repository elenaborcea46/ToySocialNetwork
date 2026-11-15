package domain;

/**
 * Represents a user of the social network.
 *
 * <p>Each user has a unique identifier, a first name,
 * and a last name.</p>
 *
 */
public class User extends Entity<Long>{
    private String firstName;
    private String lastName;

    /**
     * Creates a user with the specified identifier and name.
     *
     * @param id the unique user identifier
     * @param firstName the user's first name
     * @param lastName the user's last name
     */
    public User(Long id, String firstName, String lastName){
        setId(id);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Returns the user's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }



    /**
     * Returns the user's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }




    /**
     * Returns a string representation of the user.
     *
     * @return a string containing the user's information
     */
    @Override
    public String toString() {
        return "User{" + "id=" + getId() + ", firstName=" + firstName + ", lastName=" + lastName + '}';
    }
}
