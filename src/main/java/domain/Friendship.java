package domain;

import java.time.LocalDateTime;

/**
 * Represents an undirected friendship between two users.
 *
 * <p>The friendship is identified by a normalized pair of
 * user identifiers, ensuring that (A, B) and (B, A)
 * represent the same relationship.</p>
 *
 * <p>Stores the date and time when the friendship was created.</p>
 */
public class Friendship extends Entity<Tuple<Long, Long>>{

    private LocalDateTime friendsFrom;

    /**
     * Creates a friendship between two users using the current date and time.
     *
     * @param idUser1 the first user identifier
     * @param idUser2 the second user identifier
     * @throws IllegalArgumentException if a user identifier is null
     */
    public Friendship(Long idUser1, Long idUser2){
      this(idUser1, idUser2, LocalDateTime.now());
    }

    /**
     * Creates a friendship with a specified date and time.
     *
     * @param idUser1 first user identifier
     * @param idUser2 second user identifier
     * @param friendsFrom date and time when the frienship started
     */
   public Friendship(Long idUser1, Long idUser2, LocalDateTime friendsFrom){
        if(idUser1 == null || idUser2 == null){
            throw new IllegalArgumentException("User IDs cannot be null.");
        }

        if(idUser1 <= idUser2){
            setId(new Tuple<>(idUser1, idUser2));
        } else{
            setId(new Tuple<>(idUser2, idUser1));
        }

        this.friendsFrom = friendsFrom;
   }

    /**
     * Returns the first user identifier.
     *
     * @return first user ID
     */
   public Long getIdUser1(){
        return getId().getFirst();
   }

    /**
     * Returns the second user identifier.
     *
     * @return second user ID
     */
   public Long getIdUser2(){
        return getId().getSecond();
   }

    /**
     * Returns the friendship creation date.
     *
     * @return friendship date and time
     */
   public LocalDateTime getFriendsFrom(){
        return friendsFrom;
   }

    @Override
    public String toString() {
        return "Friendship{"  + "idUser1=" + getIdUser1() + ", idUser2=" + getIdUser2() + ", friendsFrom=" + friendsFrom + '}';
    }
}
