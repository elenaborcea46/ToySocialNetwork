package domain;

import java.util.Objects;

/**
 * Represents a generic entity identified by a unique ID.
 *
 * @param <ID> type of the entity identifier
 */
public class Entity<ID>{
    private ID id;

    /**
     * Creates an entity without an initial identifier.
     */
    public Entity() {
    }

    /**
     * Returns the entity identifier.
     *
     * @return entity identifier
     */
    public ID getId(){
        return id;
    }

    /**
     * Sets the entity identifier.
     *
     * @param id new identifier
     */
    public void setId(ID id){
        this.id = id;
    }

    /**
     * Compares two entities based on their identifiers.
     *
     * @param o   the reference object with which to compare.
     * @return    true if both entities have equal identifiers
     */
    @Override
    public boolean equals(Object o){
        if (this == o){
            return true;
        }

        if(!(o instanceof Entity<?>)){
            return false;
        }

        Entity<?> other = (Entity<?>) o;

        return Objects.equals(id, other.id);
    }

    /**
     * Computes the hash code based on the entity identifier.
     *
     * @return hash code of the identifier
     */
    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}
