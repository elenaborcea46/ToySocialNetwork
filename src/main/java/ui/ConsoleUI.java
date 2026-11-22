package ui;

import domain.User;
import domain.Friendship;
import service.UserService;
import service.FriendshipService;
import service.NetworkService;
import exceptions.RepositoryException;
import exceptions.ValidationException;

import criteria.UserCriteria;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;

import java.util.Scanner;
import java.util.stream.StreamSupport;

/**
 * Provides a console-based user interface for ToySocialNetwork.
 *
 * <p>Displays an interactive menu, reads user input,
 * and delegates operations to the appropriate services.</p>
 *
 * <p>Reports application errors without terminating
 * the interactive session unexpectedly.</p>
 *
 * @see UserService
 * @see FriendshipService
 * @see NetworkService
 */
public class ConsoleUI {

    private final UserService userService;
    private final FriendshipService friendshipService;
    private final NetworkService networkService;

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Creates the console interface using the required services.
     *
     * @param userService the service responsible for user operations
     * @param friendshipService the service responsible for friendships
     * @param networkService the service responsible for network analysis
     */
    public ConsoleUI(UserService userService, FriendshipService friendshipService, NetworkService networkService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.networkService = networkService;
    }

    /**
     * Displays the main menu of the application.
     */
    private void printMenu() {
        System.out.println("\n===== TOY SOCIAL NETWORK =====");
        System.out.println("1. Adauga utilizator");
        System.out.println("2. Sterge utilizator");
        System.out.println("3. Modifica utilizator");
        System.out.println("4. Afiseaza utilizatori");
        System.out.println("5. Adauga prietenie");
        System.out.println("6. Sterge prietenie");
        System.out.println("7. Modifica prietenie");
        System.out.println("8. Afiseaza prietenii");
        System.out.println("9. Numar comunitati");
        System.out.println("10. Cea mai sociabila comunitate");
        System.out.println("11. Testare filtrare utilizatori");
        System.out.println("0. Iesire");
        System.out.print("Alege o optiune: ");
    }

    /**
     * Reads a long integer from standard input.
     *
     * @param message the prompt displayed before reading
     * @return the entered numeric value
     * @throws NumberFormatException if the input is not a valid long integer
     */
    private Long readLong(String message){
        System.out.print(message);
        return Long.parseLong(scanner.nextLine().trim());
    }

    /**
     * Reads user information and requests the creation of a new user.
     */
    private void addUser(){

        System.out.print("Prenume: ");
        String firstName = scanner.nextLine();

        System.out.print("Nume: ");
        String lastName = scanner.nextLine();

        Long id = userService.addUser(firstName, lastName);

        System.out.println("Utilizator adaugat cu succes! ID generat:" + id);
    }

    /**
     * Reads a user identifier and requests the removal
     * of the corresponding user.
     */
    private void removeUser(){
        Long id = readLong("ID utlizator: ");

        userService.removeUser(id);

        System.out.println("Utilizator sters cu succes!");
    }

    private void updateUser(){
        Long id = readLong("ID utilizator: ");

        System.out.println("Prenume nou:");
        String firstName = scanner.nextLine();

        System.out.println("Nume nou:");
        String lastName = scanner.nextLine();

        userService.updateUser(id, firstName, lastName);

        System.out.println("Utilizator modificat cu succes!");
    }

    /**
     * Displays all users registered in the social network.
     */
    private void printUsers(){
        StreamSupport.stream(userService.getAllUsers().spliterator(), false).forEach(System.out::println);
    }


    /**
     * Reads two user identifiers and requests
     * the creation of a friendship between them.
     */
    private void addFriendship() {
        Long idUser1 = readLong("ID primul utilizator: ");
        Long idUser2 = readLong("ID al doilea utilizator: ");

        friendshipService.addFriendship(idUser1, idUser2);

        System.out.println("Prietenie adaugata cu succes!");
    }


    /**
     * Reads two user identifiers and requests
     * the removal of their friendship.
     */
    private void removeFriendship() {
        Long idUser1 = readLong("ID primul utilizator: ");
        Long idUser2 = readLong("ID al doilea utilizator: ");

        friendshipService.removeFriendship(idUser1, idUser2);

        System.out.println("Prietenie stearsa cu succes!");
    }

    private void updateFriendship(){
        Long idUser1 = readLong("ID primul utilizator: ");
        Long idUser2 = readLong("ID al doilea utilizator: ");

        System.out.println("Data noua (yyyy-MM-dd HH:mm): ");
        String dateInput = scanner.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime friendsFrom;

        try{
            friendsFrom = LocalDateTime.parse(dateInput, formatter);
        }catch(DateTimeParseException e){
            throw new IllegalArgumentException("Format invalid! Foloseste yyyy-MM-dd HH:mm");
        }

        friendshipService.updateFriendship(idUser1, idUser2, friendsFrom);

        System.out.println("Prietenie modificata cu succes!");

    }


    /**
     * Displays all friendships stored in the social network.
     */
    private void printFriendships() {
        StreamSupport.stream(friendshipService.getAllFriendships().spliterator(),false).forEach(System.out::println);
    }

    private void testUserFilters(){
        Predicate<User> startsWithP = UserCriteria.lastNameStartsWith("P");

        Predicate<User> idGreaterThan3 = UserCriteria.idGreaterThan(3L);

        System.out.println("\n AND ");
        userService.findUsers(startsWithP.and(idGreaterThan3)).forEach(System.out::println);

        System.out.println("\n OR ");
        userService.findUsers(startsWithP.or(idGreaterThan3)).forEach(System.out::println);

        System.out.println("\n NEGATE ");
        userService.findUsers(startsWithP.negate()).forEach(System.out::println);

        System.out.println("\n PREDICATE.NOT ");
        userService.findUsers(Predicate.not(idGreaterThan3)).forEach(System.out::println);

        // New search criterion defined using a lambda expression
        Predicate<User> firstNameStartsWithA =
                user -> user.getFirstName().startsWith("A");

        System.out.println("\n FIRST NAME STARTS WITH A ");
        userService.findUsers(firstNameStartsWithA)
                .forEach(System.out::println);
    }


    /**
     * Starts the interactive console interface.
     *
     * <p>Repeatedly displays the menu, reads the selected option,
     * and executes the corresponding operation.</p>
     *
     * <p>The application continues running until the user
     * selects the exit option.</p>
     */
    public void run(){
        boolean running = true;

        while(running){
            printMenu();

            String option = scanner.nextLine().trim();
            try {
                switch (option) {
                    case "1":
                        addUser();
                        break;
                    case "2":
                        removeUser();
                        break;
                    case "3":
                        updateUser();
                        break;
                    case "4":
                        printUsers();
                        break;
                    case "5":
                        addFriendship();
                        break;
                    case "6":
                        removeFriendship();
                        break;
                    case "7":
                        updateFriendship();
                        break;
                    case "8":
                        printFriendships();
                        break;

                    case "9":
                        System.out.println(
                            "Numar comunitati: " +
                                    networkService.getNumberOfCommunities()
                    );
                        break;

                    case "10":
                        System.out.println(
                            "Cea mai sociabila comunitate: " +
                                    networkService.getMostSociableCommunity()
                    );
                        break;

                    case "11":
                        testUserFilters();
                        break;

                    case "0":
                        running = false;
                        System.out.println("La revedere!");
                        break;

                    default:
                        System.out.println("Optiune invalida!");
                        break;
                }

            } catch (UnsupportedOperationException e) {
                System.out.println("Operatie indisponibila: " + e.getMessage());
            }
            catch (RepositoryException e) {
                System.out.println("Eroare repository: "
                        + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Date invalide: " + e.getMessage());
            } catch (ValidationException e) {
                System.out.println("Eroare de validare: " + e.getMessage());

            }

        }
    }
}
