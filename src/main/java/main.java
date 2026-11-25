import database.DatabaseConnection;
import domain.Friendship;
import domain.Tuple;
import domain.User;

import exceptions.FileException;
import exceptions.RepositoryException;


import exceptions.ValidationException;
import repository.Repository;
import repository.RepositoryFactory;
import repository.RepositoryType;

import service.UserService;
import service.FriendshipService;
import service.NetworkService;

import validation.UserValidator;
import validation.FriendshipValidator;

import ui.ConsoleUI;
import validation.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Represents the entry point of the ToySocialNetwork application.
 *
 * <p>Loads application settings from config.properties,
 * selects the repository implementation through RepositoryFactory,
 * initializes validators and services, and starts the console UI.</p>
 *
 * <p>Handles initialization and configuration errors
 * before the application becomes interactive.</p>
 *
 * @see RepositoryFactory
 * @see ConsoleUI
 */
public class main {

    /**
     * Prevents instantiation of the main class.
     */
    private main() {
    }

    /**
     * Reads a required configuration property.
     *
     * @param properties loaded configuration
     * @param key property name
     * @return the non-empty property value
     * @throws IllegalArgumentException if the property is missing or empty
     */
    private static String getRequiredProperty(Properties properties, String key){

        String value = properties.getProperty(key);

        if(value == null || value.trim().isEmpty()){
            throw new IllegalArgumentException("Missing or empty configuration property: " + key);
        }

        return value.trim();
    }

    /**
     * Loads the application configuration from
     * src/main/resources/config.properties.
     *
     * @return the loaded configuration
     */
    private static Properties loadConfiguration(){

        Properties properties = new Properties();

        try(InputStream input = main.class.getClassLoader().getResourceAsStream("config.properties")){
            if(input == null){
                throw new IllegalStateException("Configuration file config.properties was not found.");
            }

            properties.load(input);
        }catch(IOException e){
            throw new IllegalStateException("Could not read config.properties.", e);
        }

        return properties;
    }

    /**
     * Starts the ToySocialNetwork application.
     *
     * @param args command-line arguments, not used
     */
    public static void main(String[] args) {

        try {
            Properties properties = loadConfiguration();

            String typeValue = getRequiredProperty(properties, "repository.type");

            RepositoryType type;

            try{
                type = RepositoryType.valueOf(typeValue);
            } catch(IllegalArgumentException e){
                throw new IllegalArgumentException("Unknown repository type: " + typeValue, e);
            }

            String usersFile = null;
            String friendshipsFile = null;

            if(type == RepositoryType.FILE){
                usersFile = getRequiredProperty(properties, "users.file");
                friendshipsFile = getRequiredProperty(properties, "friendships.file");
            }

            String url = null;
            String username = null;
            String password = null;

            int pageSize = 5;

            if(type ==  RepositoryType.DATABASE){
                url = getRequiredProperty(properties, "db.url");
                username = getRequiredProperty(properties, "db.user");
                password = getRequiredProperty(properties, "db.password");

                pageSize = Integer.parseInt(getRequiredProperty(properties, "paging.pageSize"));

                if (pageSize <= 0) {
                    throw new IllegalArgumentException(
                            "paging.pageSize must be greater than 0."

                    );
                }
            }

            Validator<User> userValidator = new UserValidator();

            Validator<User> lengthValidator = user -> {
                if(user.getFirstName().length() > 10 || user.getLastName().length() > 10){
                    throw new ValidationException("Prenumele si numele trebuie sa aiba cel mult 10 caractere!");
                }
            };

            Validator<User> combinedValidator = userValidator.andThen(lengthValidator);

            Validator<Friendship> friendshipValidator = new FriendshipValidator();

            Repository<Long, User> userRepository = RepositoryFactory.createUserRepository(type, usersFile, combinedValidator, url, username, password);

            Repository<Tuple<Long, Long>, Friendship> friendshipRepository = RepositoryFactory.createFriendshipRepository(type, friendshipsFile, friendshipValidator, url, username, password);

            UserService userService = new UserService(
                    userRepository,
                    friendshipRepository,
                    type == RepositoryType.DATABASE
            );

            FriendshipService friendshipService = new FriendshipService(
                    friendshipRepository,
                    userRepository
            );

            NetworkService networkService = new NetworkService(
                    userRepository,
                    friendshipRepository
            );

            if(type != RepositoryType.DATABASE){
                networkService.validateNetwork();
            }


            ConsoleUI ui = new ConsoleUI(
                    userService,
                    friendshipService,
                    networkService,
                    pageSize
            );

            if (type == RepositoryType.DATABASE) {
                try (Connection connection =
                             DriverManager.getConnection(url, username, password)) {

                    System.out.println("Connected to PostgreSQL successfully");

                } catch (SQLException e) {
                    throw new RepositoryException(
                            "Database connection failed: " + e.getMessage()
                    );
                }
            }

            ui.run();
        } catch (IllegalArgumentException e){
            System.out.println("Configuration error: " + e.getMessage());
        } catch (RepositoryException e){
            System.out.println("RepositoryError: " + e.getMessage());

            if(e.getCause() != null){
                System.out.println("Cauza: " + e.getCause().getMessage());
            }
        } catch (ValidationException e){
            System.out.println("Validation error: " + e.getMessage());
        } catch(IllegalStateException e){
            System.out.println("Initialization error: " + e.getMessage());
        } catch(FileException e){
            System.out.println("FileError: " + e.getMessage());

            if(e.getCause() != null){
                System.out.println("Cauza: " + e.getCause().getMessage());
            }
        }
    }
}
