package repository;

import domain.Entity;
import exceptions.ValidationException;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

/**
 * Defines a generic contract for storing and managing entities.
 *
 * <p>Supports retrieving, saving, deleting,
 * and updating entities independently of the
 * underlying persistence mechanism.</p>
 *
 * @param <ID> the type of the entity identifier
 * @param <E> the type of entity managed by the repository
 */
public interface Repository<ID, E extends Entity<ID>> {

    /**
     * Finds an entity by its identifier.
     *
     * @param id identifier of the entity
     * @return the entity or null if it does not exist
     * @throws IllegalArgumentException if id is null
     */
    E findOne(ID id);

    /**
     * Returns all stored entities.
     *
     * @return an interable collection of entities
     */
    Iterable<E> findAll();

    /**
     * Saves a new entity.
     *
     * @param entity entity to save
     * @return null if saved successfully,
     *         otherwise the entity with the duplicate identifier
     * @throws IllegalArgumentException if entity is null
     * @throws ValidationException if the entity is invalid
     */
    E save(E entity);

    /**
     * Deletes an entity by its identifier.
     *
     * @param id identifier of the entity to delete
     * @return the removed entity or null if it does not exist
     * @throws IllegalArgumentException if id is null
     */
    E delete(ID id);

    /**
     * Updates an existing entity.
     *
     * @param entity entity containing the updated information
     * @return null if updated successfully,
     *         otherwise the provided entity if its ID does not exist
     * @throws IllegalArgumentException if entity is null
     * @throws ValidationException if the entity is invalid
     */
    E update(E entity);

    /**
     * Returns all entities that satisfy the given predicate.
     *
     * @param filter the predicate used to filter entities
     * @return a list containing the matching entities
     */
    default List<E> findAll(Predicate<E> filter){
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(filter)
                .toList();
    }

}
