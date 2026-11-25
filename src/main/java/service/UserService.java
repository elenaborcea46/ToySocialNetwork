package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.Repository;

import util.paging.Page;
import util.paging.Pageable;
import repository.PagingRepository;


import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import java.util.function.Predicate;

/**
 * Provides business operations for managing social network users.
 *
 * <p>Supports adding, removing, searching, and listing users.
 * Removing a user also removes their associated friendships
 * to maintain network consistency.</p>
 *
 * @see User
 * @see Repository
 */
public class UserService {
    private final Repository<Long, User> userRepository;

    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;

    private final boolean databaseGeneratedIds;
    /**
     * Creates a user service using the specified repositories.
     *
     * @param userRepository the repository containing users
     * @param friendshipRepository the repository containing friendships
     */
    public UserService(Repository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship> friendshipRepository, boolean databaseGeneratedIds) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.databaseGeneratedIds = databaseGeneratedIds;
    }


    private Long generateNextId(){
        //findAll() returneaza un Iterable<User>,nu un Stream<User>
        //De aceea folosim StreamSupport.stream() pt a putea aplica operatiile specifice Stream API
        //false-inseamna ca stream-ul e secvential, nu paralel
        return StreamSupport.stream(userRepository.findAll().spliterator(), false
        )
                .map(User::getId)
                .max(Long::compareTo)
                .map(id -> id + 1)
                .orElse(1L);
    }


    /**
     * Creates and saves a new user with an automatically generated ID.
     *
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @return the generated user ID
     * @throws IllegalArgumentException if the user already exists
     */
    public Long addUser(String firstName, String lastName) {

        Long id = databaseGeneratedIds ? null : generateNextId();

        User user = new User(id, firstName, lastName);

        Optional<User> result = userRepository.save(user);

        if(result.isPresent()){
            throw new RepositoryException("A user with this ID already exists.");
        }

        return user.getId();
    }


    /**
     * Removes a user and all friendships involving that user.
     *
     * @param id the identifier of the user to remove
     * @throws IllegalArgumentException if the user does not exist
     */
    public void removeUser(Long id){

        findUser(id);

        List<Tuple<Long, Long>> friendshipsToRemove = StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                .filter(friendship -> friendship.getIdUser1().equals(id) || friendship.getIdUser2().equals(id))
                .map(Friendship::getId)
                .toList();

        friendshipsToRemove.forEach(friendshipRepository::delete);

        userRepository.delete(id);

    }

    /**
     * Updates an existing user's information.
     *
     * @param id the identifier of the user to update
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @throws RepositoryException  if the user does not exist
     */
    public void updateUser(Long id, String firstName, String lastName){

        User user = new User(id, firstName, lastName);

        Optional<User> result = userRepository.update(user);

        if(result.isPresent()){
            throw new RepositoryException("Nu exista utilizatorul cu ID-ul " + id);
        }
    }


    /**
     * Finds a user by their identifier.
     *
     * @param id the user identifier
     * @return the matching user
     * @throws RepositoryException if the user does not exist
     */
    public User findUser(Long id){

        return userRepository.findOne(id).orElseThrow(() -> new RepositoryException("Nu exista utilizatorul cu ID-ul " + id));
    }


    /**
     * Returns all users registered in the social network.
     *
     * @return an iterable collection of users
     */
    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * Returns all users matching the given predicate.
     *
     * @param filter the condition used to select users
     * @return the matching users
     */
    public List<User> findUsers(Predicate<User> filter){
        return userRepository.findAll(filter);
    }


    /**
     * Retrieves a page of users from a repository that supports pagination.
     *
     * @param pageable the pagination request
     * @return a page containing users and their total count
     * @throws UnsupportedOperationException if pagination is not supported
     */
    public Page<User> getUsersOnPage(Pageable pageable){
        if(userRepository instanceof PagingRepository<?, ?>){
            @SuppressWarnings("unchecked")
                    PagingRepository<Long, User> pagingRepository = (PagingRepository<Long, User>) userRepository;

            return pagingRepository.findAllOnPage(pageable);
        }

        throw new UnsupportedOperationException("Pagination is only supported for DATABASE repositories.");
    }
}
