package domain;

import java.util.Objects;

/**
 * Represents an immutable pair of values.
 *
 * <p> Provides equality and hash code implementations based
 * on both elements of the pair.</p>
 *
 * @param <E1> type of the first element
 * @param <E2> type of the second element
 */
public class Tuple<E1, E2> {
    private final E1 first;
    private final E2 second;

    /**
     * Creates a tuple containing two values.
     *
     * @param first first value
     * @param second second value
     */
    public Tuple(E1 first, E2 second){
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element.
     *
     * @return first element
     */
    public E1 getFirst(){
        return first;
    }

    /**
     * Returns the second element.
     *
     * @return second element
     */
    public E2 getSecond(){
        return second;
    }

    /**
     * Compares two tuples by their elements.
     *
     * @param o   the reference object with which to compare.
     * @return    true if both elements are equal
     */
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }

        if(!(o instanceof Tuple<?, ?>)){
            return false;
        }

        Tuple<?, ?> other = (Tuple<?, ?>) o;

        return Objects.equals(first, other.first) && Objects.equals(second, other.second);
    }

    /**
     * Computes the hash code based on both elements.
     *
     * @return hash code of the tuple
     */
    @Override
    public int hashCode(){
        return Objects.hash(first, second);
    }


    @Override
    public String toString(){
        return "(" + first + ", " + second + ")";
    }
}
