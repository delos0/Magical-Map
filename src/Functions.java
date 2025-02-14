import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Functions {

    public static void ProcessMissions(ArrayList<ArrayList<Pair>> graph, int[][] nodes, ArrayList<ArrayList<Integer>> missions, int startX, int startY) {

    }

    /*
    Dijkstra's algorithm to find the shortest path from some node
     */
    public static ArrayList<Integer> Dijkstra(int width, int height, int[][] nodes, ArrayList<ArrayList<Pair>> graph,
                                              HashTable<Integer> sight, int startX, int startY,
                                              int x, int y, ArrayList<Double> length, int exclude) {
        ArrayList<Integer> path = new ArrayList<>();
        double[] distance = new double[width * height];
        int[] parents = new int[width * height];
        Arrays.fill(distance, Double.MAX_VALUE);
        Arrays.fill(parents, -1);

        int source = startY * height + startX;
        distance[source] = 0;

        Comparator<Pair> compare = Comparator.comparingDouble(Pair::getWeight);
        BinaryHeap<Pair> heap = new BinaryHeap<>(width * height, compare);

        heap.insert(new Pair(source, 0));

        while (!heap.isEmpty()) {
            Pair current = heap.deleteMax();

            for (Pair neighbor : graph.get(current.getVertice())) {
                if (passable(sight, nodes, exclude, neighbor.getVertice())
                        && distance[current.getVertice()] + neighbor.getWeight()
                        < distance[neighbor.getVertice()]) {
                    distance[neighbor.getVertice()] = neighbor.getWeight() + distance[current.getVertice()];
                    heap.insert(new Pair(neighbor.getVertice(), distance[neighbor.getVertice()]));
                    parents[neighbor.getVertice()] = current.getVertice();
                }
            }
        }

        int through = y * height + x;
        length.add(distance[through]);
        while (through != source) {
            path.add(through);
            through = parents[through];
        }

        Collections.reverse(path);
        return path;
    }

    /*
    Function to check if the node is passable.
    This function contains extra variable {exclude} that is needed to check wizard's options
     */
    private static boolean passable(HashTable<Integer> sight, int[][] nodes, int exclude, int vertice) {
        if (exclude != -1) {
            int y = vertice / nodes.length;
            int x = vertice % nodes[0].length;
            if (nodes[y][x] == exclude) return true;
        }
        return !sight.contains(vertice);
    }


    /*
    Function to check if the current path contains any impassable nodes
     */
    public static boolean inconsistent(ArrayList<Integer> path, HashTable<Integer> sight, int j) {
        while (j < path.size()) {
            if (sight.contains(path.get(j))) return true;
            j++;
        }
        return false;
    }

    /*
    Function to add revealed impassable nodes to a hashtable {sight}
     */
    public static void SeeSight(HashTable<Integer> sight, int[][] nodes, int currentX, int currentY, int radius) {
        int x0 = max(currentX - radius, 0);
        int y0 = max(currentY - radius, 0);
        int height = nodes.length;
        int width = nodes[0].length;
        int x1 = min(currentX + radius, width - 1);
        int y1 = min(currentY + radius, height - 1);
        for (int i = y0; i <= y1; i++) {
            for (int j = x0; j <= x1; j++) {
                if (inSight(currentX, currentY, j, i, radius) && nodes[i][j] != 0) {
                    sight.insert(i * height + j);
                }
            }
        }
    }


    /*
    Function to calculate if the traveler can see the node i, j from currentX currentY
     */
    private static boolean inSight(int currentX, int currentY, int i, int j, int radius) {
        int x = currentX - i;
        int y = currentY - j;

        if (Math.sqrt(x * x + y * y) > (double) radius) return false;
        else return true;
    }

    public static int max(int first, int second) {
        if (first > second) return first;
        else return second;
    }

    public static int min(int first, int second) {
        if(first < second) return first;
        else return second;
    }
}
