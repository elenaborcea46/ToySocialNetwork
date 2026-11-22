# ToySocialNetwork

A Java console application for managing users, friendships, and social network communities.

The project was developed as part of the **Advanced Programming Methods (MAP)** laboratory at Babeș-Bolyai University, Faculty of Mathematics and Computer Science.

## Features

### User Management
- Add users with automatically generated IDs.
- Delete existing users.
- Display all registered users.
- Filter users using different criteria.

### Friendship Management
- Create friendships between existing users.
- Delete friendships.
- Display all friendships.
- Prevent duplicate friendships.
- Store the date and time when a friendship was created.
- Automatically remove associated friendships when a user is deleted.

### Social Network Analysis
- Calculate the number of connected communities in the network.
- Identify the most sociable community based on the longest simple path.
- Represent friendships as an undirected graph.
- Use Depth-First Search (DFS) for graph traversal.

### Data Persistence
The application supports three repository implementations:

| Repository | Description |
|---|---|
| In-Memory | Stores entities in memory during execution. |
| File | Persists users and friendships in text files. |
| Database | Stores users and friendships in PostgreSQL using JDBC. |

The repository implementation can be selected through application configuration.

## Technologies

- **Java 21**
- **Gradle**
- **PostgreSQL**
- **JDBC**
- **Git & GitHub**
- **IntelliJ IDEA**

## Project Architecture

The application follows a layered architecture to separate responsibilities.

```text
src/main/
├── java/
│   ├── domain/
│   │   ├── Entity.java
│   │   ├── User.java
│   │   ├── Friendship.java
│   │   └── Tuple.java
│   ├── validation/
│   │   ├── Validator.java
│   │   ├── UserValidator.java
│   │   └── FriendshipValidator.java
│   ├── repository/
│   │   ├── Repository.java
│   │   ├── InMemoryRepository.java
│   │   ├── FileRepository.java
│   │   ├── UserFileRepository.java
│   │   ├── FriendshipFileRepository.java
│   │   ├── UserDatabaseRepository.java
│   │   ├── FriendshipDatabaseRepository.java
│   │   └── RepositoryFactory.java
│   ├── service/
│   │   ├── UserService.java
│   │   ├── FriendshipService.java
│   │   └── NetworkService.java
│   ├── ui/
│   │   └── ConsoleUI.java
│   └── main.java
└── resources/
    ├── config.properties
    └── config.example.properties
```

Additional configuration, exception, and repository-related classes are included in the project.

### Architecture Layers

**Domain Layer:** Defines entities and their relationships.

**Validation Layer:** Ensures that entities satisfy application rules.

**Repository Layer:** Provides CRUD operations through interchangeable persistence implementations.

**Service Layer:** Implements business logic and social network analysis.

**UI Layer:** Provides a console-based interface for interacting with the application.

## Database Structure

The PostgreSQL database is named `socialnetwork` and contains two main tables.

### Users

```sql
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL
);
```

User IDs are generated automatically by PostgreSQL.

### Friendships

```sql
CREATE TABLE friendships (
    id1 BIGINT NOT NULL,
    id2 BIGINT NOT NULL,
    friends_from TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id1, id2),

    FOREIGN KEY (id1)
        REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY (id2)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CHECK (id1 < id2)
);
```

Friendships are undirected. User IDs are normalized to ensure that `(A, B)` and `(B, A)` represent the same relationship.

The database enforces referential integrity and automatically removes associated friendships when a user is deleted.

## Configuration

Create a `config.properties` file based on `config.example.properties`.

Configure the repository type and PostgreSQL connection details using the property names expected by the application.

For database persistence, the JDBC URL follows this format:

```properties
jdbc.url=jdbc:postgresql://localhost:5432/socialnetwork
jdbc.user=postgres
jdbc.password=YOUR_PASSWORD
```

Select the `DATABASE`, `FILE`, or `MEMORY` repository implementation using the project's repository configuration.

**Security:** Do not commit real database passwords or other credentials to GitHub. Keep `config.properties` out of version control and provide a safe example configuration instead.

## Running the Application

### Prerequisites

- JDK 21
- Gradle or the included Gradle Wrapper
- PostgreSQL, when using database persistence

### Steps

1. Clone the repository.
2. Open the project in IntelliJ IDEA.
3. Configure the application properties.
4. Create the PostgreSQL database and tables if using database persistence.
5. Start the PostgreSQL server.
6. Run the application's main class.

Alternatively, use the Gradle run task if configured in the project.

## Console Menu

```text
===== TOY SOCIAL NETWORK =====
1. Adauga utilizator
2. Sterge utilizator
3. Afiseaza utilizatori
4. Adauga prietenie
5. Sterge prietenie
6. Afiseaza prietenii
7. Numar comunitati
8. Cea mai sociabila comunitate
9. Testare filtrare utilizatori
0. Iesire
```

## Graph Algorithms

The social network is modeled as an undirected graph:

- **Vertices:** Users
- **Edges:** Friendships

### Connected Communities

DFS is used to identify connected components and calculate the number of communities in the network.

### Most Sociable Community

The application identifies the community containing the longest simple path, using DFS with backtracking.

## Database Operations and Security

The database repositories implement CRUD operations using JDBC.

Key implementation details include:

- `PreparedStatement` for parameterized SQL queries.
- `ResultSet` for reading database records.
- `executeQuery()` for SELECT operations.
- `executeUpdate()` for INSERT, UPDATE, and DELETE operations.
- `Optional<T>` for repository operation results.
- Try-with-resources for automatic JDBC resource management.
- Custom repository exceptions for database errors.

### SQL Injection Protection

User input is passed to SQL statements through `PreparedStatement` parameters rather than concatenated directly into SQL queries.

For example, a name containing:

```text
'); DROP TABLE users; --
```

is treated as ordinary text instead of an executable SQL statement.

### Database Availability

The application handles PostgreSQL connection failures through repository exception handling, providing an error message when the database server is unavailable.

## Author

Developed as part of the Advanced Programming Methods laboratory at **Babeș-Bolyai University**, Faculty of Mathematics and Computer Science.

