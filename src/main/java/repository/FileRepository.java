package repository;

import domain.Entity;
import exceptions.FileException;

import exceptions.ValidationException;
import validation.Validator;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.List;
import java.util.stream.StreamSupport;


/**
 * Provides a generic file-based repository implementation.
 *
 * <p>Extends in-memory storage with persistent file operations.
 * Entities are loaded when the repository is initialized
 * and written to the file after successful modifications.</p>
 *
 * <p>Uses the Template Method design pattern by delegating
 * entity parsing and serialization to concrete subclasses.</p>
 *
 * @param <ID> the type of the entity identifier
 * @param <E> the type of entity stored in the repository
 *
 * @see InMemoryRepository
 *
 */
public abstract class FileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E>{

    private final String fileName;


    /**
     * Creates a file-based repository.
     *
     * @param fileName the path of the persistence file
     * @param validator the validator used for entities
     */
    public FileRepository(String fileName, Validator<E> validator){
        super(validator);
        this.fileName = fileName;
    }

    /**
     * Converts a text record into an entity.
     *
     * <p>The parsing logic is implemented by concrete subclasses.</p>
     *
     * @param line the text record to parse
     * @return the extracted entity
     */
    protected abstract E extractEntity(String line);

    /**
     * Converts an entity into a text record suitable for persistence.
     *
     * @param entity the entity to serialize
     * @return the serialized entity
     */
    protected abstract String createEntityAsString(E entity);


    /**
     * Loads entities from the configured persistence file.
     *
     *  <p>Uses Stream API to process file lines and preserves
     *   line numbers when reporting invalid records.</p>
     *
     *
     * <p>If the file does not exist, the repository starts empty.</p>
     *
     * @throws exceptions.FileException if file loading fails
     */
    protected final void loadFromFile(){

        Path path = Path.of(fileName);

        if(Files.notExists(path)){
            return;
        }

        try(var linesStream = Files.lines(path)){

            List<String> lines = linesStream.toList();

            IntStream.range(0, lines.size()).filter(index -> !lines.get(index).isBlank())
                    .forEach(index -> {

                        String line = lines.get(index);
                        int lineNumber = index + 1;

                        try{
                            E entity = extractEntity(line);

                            Optional<E> existing = super.save(entity);

                            if(existing.isPresent()){
                                throw new FileException("ID duplicat la linia " + lineNumber + " din fisierul" + fileName, null);
                            }
                        } catch(RuntimeException e){
                            throw new FileException(
                                    "Date invalide la linia " + lineNumber + " din fisierul" + fileName, e
                            );
                        }
                    });
        } catch(IOException e){
            throw new FileException("Eroare la citirea fisierului: " + fileName, e);
        }
    }


    /**
     * Writes all entities to the persistence file using Stream API.
     *
     * @throws exceptions.RepositoryException if writing fails
     */
    private void writeToFile(){

        Path path = Path.of(fileName);

        List<String> lines = StreamSupport.stream(findAll().spliterator(), false).map(this::createEntityAsString).toList();

        try{
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch(IOException e){
            throw new FileException("Eroare la scrierea fisierului: " + fileName, e);
        }
    }


    /**
     * Saves a new entity and persists the changes to the file.
     *
     * <p>The entity is first saved in memory using the superclass
     * implementation. If the operation succeeds, the repository
     * contents are written to the persistence file.</p>
     *
     * @param entity the entity to save
     * @return an empty Optional if saved successfully,
     *         or an Optional containing the existing entity
     *         if its identifier is already present
     * @throws IllegalArgumentException if the entity is null
     * @throws exceptions.ValidationException if the entity is invalid
     * @throws exceptions.RepositoryException if writing to the file fails
     */
    @Override
    public Optional<E> save(E entity){

        Optional<E> result = super.save(entity);

        if(result.isEmpty()){
            writeToFile();
        }
        return result;
    }


    /**
     * Deletes an entity and persists the changes to the file.
     *
     * <p>The entity is first removed from the in-memory repository.
     * If the entity exists and is successfully deleted, the updated
     * repository contents are written to the persistence file.</p>
     *
     * @param id the identifier of the entity to delete
     * @return   an Optional containing the deleted entity,
     *           or an empty Optional if the entity does not exist
     * @throws IllegalArgumentException if the identifier is null
     * @throws exceptions.RepositoryException if writing to the file fails
     */
    @Override
    public Optional<E> delete(ID id){
        Optional<E> result = super.delete(id);

        if(result.isPresent()){
            writeToFile();
        }
        return result;
    }


    /**
     * Updates an existing entity and persists the changes to the file.
     *
     * <p>The entity is first updated in memory using the superclass
     * implementation. If the update succeeds, the modified repository
     * contents are written to the persistence file.</p>
     *
     * @param entity the entity containing the updated information
     * @return   an empty Optional if updated successfully,
     * *         or an Optional containing the provided entity
     * *         if its identifier does not exist
     * @throws IllegalArgumentException if the entity is null
     * @throws exceptions.ValidationException if the entity is invalid
     * @throws exceptions.RepositoryException if writing to the file fails
     */
    @Override
    public Optional<E> update(E entity){
        Optional<E> result = super.update(entity);

        if(result.isEmpty()){
            writeToFile();
        }

        return result;
    }
}
