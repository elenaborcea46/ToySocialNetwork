package repository;

import domain.Entity;
import validation.Validator;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Implements a generic repository using an in-memory map.
 *
 * <p>Entities are indexed by their unique identifiers
 * and validated before insertion or update.</p>
 *
 * <p>Data is not preserved between application executions
 * unless another persistence mechanism is used.</p>
 *
 * @param <ID> the type of the entity identifier
 * @param <E> the type of entity stored in the repository
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private final Validator<E> validator;

    /**
     * Stores entities in memory, indexed by their unique identifiers.
     */
    protected Map<ID, E> entities = new HashMap<>();

    /**
     * Creates an in-memory repository with a validation strategy.
     *
     * @param validator used for stored entities
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
    }


    /**
     * Finds an entity by its identifier.
     *
     * @param id identifier to search for
     * @return an Optional containing the entity,
     *         or an empty Optional if it does not exist
     * @throws IllegalArgumentException if id is null
     */
    @Override
    public Optional<E> findOne(ID id){
        if(id == null){
            throw new IllegalArgumentException("ID cannot be null.");
        }

        return Optional.ofNullable(entities.get(id));
    }


    /**
     * Returns all stored entities.
     *
     * @return iterable collection of entities
     */
    @Override
    public Iterable<E> findAll(){
        return entities.values();
    }


    /**
     * Saves a new entity after validation.
     *
     * @param entity entity to save
     * @return an empty Optional if saved successfully,
     *         or an Optional containing the existing entity
     */
    @Override
    public Optional<E> save(E entity){
        if(entity == null){
            throw new IllegalArgumentException("Entity cannot be null.");
        }

        validator.validate(entity);

        if(entities.containsKey(entity.getId())){
            return Optional.of(entities.get(entity.getId()));
        }

        entities.put(entity.getId(), entity);
        return Optional.empty();
    }

    /**
     * Deletes an entity by its identifier.
     *
     * @param id identifier of the entity to delete
     * @return an Optional containing the removed entity,
     *         or an empty Optional if not found
     * @throws IllegalArgumentException if id is null
     */
    @Override
    public Optional<E> delete(ID id){
        if(id == null){
            throw new IllegalArgumentException("ID cannot be null.");
        }

        return Optional.ofNullable(entities.remove(id));
    }


    /**
     * Updates an existing entity after validation.
     *
     * @param entity entity containing updated data
     * @return an empty Optional if updated successfully,
     *         or an Optional containing the provided entity
     *         if its identifier does not exist
     *
     */
    @Override
    public Optional<E> update(E entity){
        if(entity == null){
            throw new IllegalArgumentException("Entity cannot be null.");
        }

        validator.validate(entity);

        if(!entities.containsKey(entity.getId())){
            return Optional.of(entity);
        }

        entities.put(entity.getId(), entity);
        return Optional.empty();
    }
}
