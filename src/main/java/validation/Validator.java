package validation;

/**
 * Defines a generic contract for validating entities.
 *
 * @param <T> the type of entity to validate
 */
@FunctionalInterface
public interface Validator<T>{

    /**
     * Validates an entity according to the implementation rules.
     *
     * @param entity the entity to validate
     * @throws exceptions.ValidationException if validation fails
     */
    void validate(T entity);

    default Validator<T> andThen(Validator<T> other){

        if(other == null){
            throw new IllegalArgumentException("Validatorul nu poate fi null!");
        }

        return entity -> {
            this.validate(entity);
            other.validate(entity);
        };
    }
}
