package repository;

/**
 * Defines the supported repository storage mechanisms.
 *
 * <p>MEMORY stores entities in memory, FILE enables
 * text-file persistence, and DATABASE represents
 * the database persistence option.</p>
 */
public enum RepositoryType {
    /**
     * Stores entities only in memory.
     */
    MEMORY,

    /**
     * Stores entities in text files.
     */
    FILE,

    /**
     * Represents database-based persistence.
     */
    DATABASE
}
