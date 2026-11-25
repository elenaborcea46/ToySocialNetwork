package repository;

import domain.Entity;

import util.paging.Page;
import util.paging.Pageable;

/**
 * Extends the standard repository with pagination support.
 *
 * @param <ID> the type of entity identifier
 * @param <E> the type of entity
 */
public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {

    /**
     * Retrieves a page of entities.
     *
     * @param pageable the pagination request
     * @return a page containing the requested entities
     *         and the total number of entities
     */
    Page<E> findAllOnPage(Pageable pageable);
}
