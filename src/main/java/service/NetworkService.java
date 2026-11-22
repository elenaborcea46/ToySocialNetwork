package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import exceptions.RepositoryException;
import repository.Repository;
import java.util.*;

import java.util.stream.StreamSupport;
import java.util.stream.Collectors;
import java.util.function.Function;

/**
 * Analyzes the structure and integrity of the social network.
 *
 * <p>Models users as vertices and friendships as undirected
 * edges of a graph.</p>
 *
 * <p>Uses depth-first search to identify connected components
 * and determine the number of communities.</p>
 *
 * <p>Identifies the most sociable community based on
 * the longest simple path within each component.</p>
 *
 * @see User
 * @see Friendship
 */
public class NetworkService {

    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;


    /**
     * Creates a network service using the specified repositories.
     *
     * @param userRepository the repository containing users
     * @param friendshipRepository the repository containing friendships
     */
    public NetworkService(Repository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship> friendshipRepository){
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }


    /**
     * Validates the integrity of the social network.
     *
     * <p>Checks that all friendship endpoints refer to existing
     * users and that no duplicate relationships are present.</p>
     *
     * @throws exceptions.RepositoryException if the network
     *         contains invalid or duplicate relationships
     */
    public void validateNetwork(){
        Set<Tuple<Long, Long>> friendshipPairs = new HashSet<>();

        StreamSupport.stream(
                friendshipRepository.findAll().spliterator(),
                false
        ).forEach(friendship -> {

            Long id1 = friendship.getIdUser1();
            Long id2 = friendship.getIdUser2();

            if (userRepository.findOne(id1).isEmpty() ||
                    userRepository.findOne(id2).isEmpty()) {

                throw new RepositoryException(
                        "Friendship " + friendship.getId()
                                + " references users that do not exist!"
                );
            }

            Tuple<Long, Long> pair = friendship.getId();

            if (!friendshipPairs.add(pair)) {
                throw new RepositoryException(
                        "Duplicate friendship between users "
                                + id1 + " and " + id2
                );
            }
        });

    }


    /**
     * Builds an undirected graph using adjacency lists.
     *
     * <p>Each user is represented by a vertex, and each
     * friendship is represented by an undirected edge.</p>
     *
     * @return a map containing the adjacency list of each user
     */
    private Map<Long, List<Long>> buildGraph() {
         Map<Long, List<Long>> graph = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                 .collect(Collectors.toMap(User::getId, user -> new ArrayList<>()));

         StreamSupport.stream(friendshipRepository.findAll().spliterator(), false)
                 .forEach( friendship -> {
                     Long id1 = friendship.getIdUser1();
                     Long id2 = friendship.getIdUser2();

                     graph.get(id1).add(id2);
                     graph.get(id2).add(id1);
                 });

         return graph;
    }


    /**
     * Performs depth-first search starting from a specified vertex.
     *
     * @param node the current vertex
     * @param graph the graph represented by adjacency lists
     * @param visited the set of already visited vertices
     */
    private void dfs(Long node, Map<Long, List<Long>> graph, Set<Long> visited){
        visited.add(node);

        graph.get(node).forEach(neighbor -> {
           if(!visited.contains(neighbor)){
               dfs(neighbor, graph, visited);
           }
        });
    }


    /**
     * Computes the number of connected components in the network.
     *
     * <p>Each isolated user is considered a separate community.</p>
     *
     * <p>The algorithm uses depth-first search and has
     * a time complexity of O(V + E).</p>
     *
     * @return the number of connected components
     */
    public int getNumberOfCommunities(){

        Map<Long, List<Long>> graph = buildGraph();

        Set<Long> visited = new HashSet<>();
        List<List<Long>> communities = new ArrayList<>();

        graph.keySet().forEach(node -> {
            if (!visited.contains(node)) {

                List<Long> component = new ArrayList<>();

                dfsComponent(node, graph, visited, component);

                communities.add(component);
            }
        });

        return communities.size();
    }


    /**
     * Finds the maximum length of a simple path starting
     * from the specified vertex.
     *
     * <p>Uses depth-first search with backtracking
     * to explore paths without repeating vertices.</p>
     *
     * @param node the current vertex
     * @param graph the graph represented by adjacency lists
     * @param visited the vertices belonging to the current path
     * @return the maximum path length measured in edges
     */
    private int longestPathDFS(Long node, Map<Long, List<Long>> graph, Set<Long> visited){

        visited.add(node);

        int maxLength = 0;

        for (Long neighbor : graph.get(node)) {
            if (!visited.contains(neighbor)) {
                int length = 1 + longestPathDFS(neighbor, graph, visited);
                maxLength = Math.max(maxLength, length);
            }
        }

        visited.remove(node);

        return maxLength;
    }

    /**
     * Collects all vertices belonging to a connected component.
     *
     * @param node the current vertex
     * @param graph the graph represented by adjacency lists
     * @param visited the set of visited vertices
     * @param component the list of vertices in the current component
     */
    private void dfsComponent(Long node, Map<Long, List<Long>> graph, Set<Long> visited, List<Long> component){
        visited.add(node);
        component.add(node);//memoram in lista nodurile componentei conexe

        graph.get(node).forEach(neighbor -> {
            if (!visited.contains(neighbor)) {
                dfsComponent(neighbor, graph, visited, component);
            }
        });
    }

    /**
     * Computes the maximum simple path length within a component.
     *
     * @param component the vertices of the connected component
     * @param graph the graph represented by adjacency lists
     * @return the maximum path length measured in edges
     */
    private int getLongestPathLength(
            List<Long> component,
            Map<Long, List<Long>> graph) {

        return component.stream()
                .mapToInt(node ->
                        longestPathDFS(node, graph, new HashSet<>())
                )
                .max()
                .orElse(0);
    }


    /**
     * Identifies the most sociable community in the social network.
     *
     * <p>The most sociable community is the connected component
     * containing the longest simple path.</p>
     *
     * <p>Simple paths are explored using depth-first search
     * with backtracking.</p>
     *
     * @return the identifiers of users belonging to the most
     *         sociable community, or an empty list if no users exist
     */
    public List<Long> getMostSociableCommunity() {

        Map<Long, List<Long>> graph = buildGraph();

        Set<Long> visited = new HashSet<>();
        List<List<Long>> communities = new ArrayList<>();

        graph.keySet().forEach(node -> {
            if (!visited.contains(node)) {
                List<Long> component = new ArrayList<>();
                dfsComponent(node, graph, visited, component);
                communities.add(component);
            }
        });

        return communities.stream()
                .reduce((first, second) ->
                        getLongestPathLength(first, graph)
                                >= getLongestPathLength(second, graph)
                                ? first : second
                )
                .orElseGet(ArrayList::new);
    }


}
