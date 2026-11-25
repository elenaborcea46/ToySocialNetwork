package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.FriendshipDatabaseRepository;
import repository.Repository;
import util.paging.Page;
import util.paging.Pageable;
import validation.Validator;

import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Provides business operations for managing friendships.
 *
 * <p>Supports creating, removing, searching, and listing
 * relationships between users.</p>
 *
 * <p>Ensures that both users exist and prevents duplicate
 * undirected friendships.</p>
 *
 * @see Friendship
 * @see Repository
 */
public class FriendshipService {

    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final Repository<Long, User> userRepository;



    /**
     * Creates a friendship service.
     *
     * @param friendshipRepository repository for friendships
     * @param userRepository repository for users
     */
    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> friendshipRepository, Repository<Long, User> userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a friendship between two existing users.
     *
     * @param idUser1 first user identifier
     * @param idUser2 second user identifier
     * @throws RepositoryException if a user does not exist
     *                             or the friendship already exists
     */
    public void addFriendship(Long idUser1, Long idUser2){
        Friendship friendship = new Friendship(idUser1, idUser2);


        Optional<User> user1 = userRepository.findOne(idUser1);

        Optional<User> user2 = userRepository.findOne(idUser2);

        if(user1.isEmpty() || user2.isEmpty()){
            throw new RepositoryException("Ambii utilizatori trebuie sa existe!");
        }

        if(findFriendship(idUser1, idUser2).isPresent()){
            throw new RepositoryException("Cei doi utilizatori sunt deja prieteni!");
        }

        Optional<Friendship> result = friendshipRepository.save(friendship);

        if(result.isPresent()){
            throw new RepositoryException("These users are already friends.");
        }
    }

    /**
     * Removes the friendship between two users.
     *
     * @param idUser1 first user identifier
     * @param idUser2 second user identifier
     * @throws RepositoryException if the friendship does not exist
     */
    public void removeFriendship(Long idUser1, Long idUser2){

        Friendship friendship = findFriendship(idUser1, idUser2).orElseThrow( () -> new RepositoryException("Prietenia nu exista!"));

        friendshipRepository.delete(friendship.getId());
    }


    /**
     * Finds the friendship between two users,
     * regardless of their order.
     *
     * @param idUser1 first user identifier
     * @param idUser2 second user identifier
     * @return the friendship or null if not found
     */
    public Optional<Friendship> findFriendship(Long idUser1, Long idUser2){

       if(idUser1 == null || idUser2 == null){
           throw new IllegalArgumentException("User IDs cannot be null.");
       }

       Tuple<Long, Long> id;

       if(idUser1 <= idUser2){
           id = new Tuple<>(idUser1, idUser2);
       } else{
           id = new Tuple<>(idUser2, idUser1);
       }

       return friendshipRepository.findOne(id);
    }

    /**
     * Returns all friendships in the social network.
     *
     * @return iterable collection of friendships
     */
    public Iterable<Friendship> getAllFriendships(){
        return friendshipRepository.findAll();
    }

    /**
     * Updates an existing friendship's information.
     *
     * @param idUser1 first user identifier
     * @param idUser2 second user identifier
     * @param friendsFrom date and time when the friendship started
     * @throws RepositoryException if the friendship does not exist
     */
    public void updateFriendship(Long idUser1, Long idUser2, LocalDateTime friendsFrom){
        Friendship friendship = new Friendship(idUser1, idUser2, friendsFrom);

        Optional<Friendship> result = friendshipRepository.update(friendship);

        if(result.isPresent()){
            throw new RepositoryException("Prietenia nu exista!");
        }
    }



    /**
     * Retrieves a page of friends for a specified user.
     *
     * @param userId the ID of the user whose friends are requested
     * @param pageable the pagination request containing the page number and size
     * @return a page containing the user's friends and their total count
     * @throws UnsupportedOperationException if the repository does not support pagination
     */
    public Page<User> getFriendsOnPage(Long userId, Pageable pageable) {

        if (friendshipRepository instanceof FriendshipDatabaseRepository dbRepository) {
            return dbRepository.findFriendsOnPage(userId, pageable);
        }

        throw new UnsupportedOperationException(
                "Pagination is only supported for DATABASE repositories."
        );
    }



}
