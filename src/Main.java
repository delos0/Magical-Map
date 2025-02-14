import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.function.DoublePredicate;

public class Main extends Functions {
    public static void main(String[] args) {

        long startTime = System.nanoTime();


        /*
        Reading all the input files
         */

        File nodesFile = new File(args[0]);
        File edgesFile = new File(args[1]);
        File missionFile = new File(args[2]);

        File outputFile = new File(args[3]);
        PrintStream output;

        try {
            output = new PrintStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Scanner reader;
        try {
            reader = new Scanner(nodesFile);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find nodes file");
            return;
        }

        String line = reader.nextLine();
        String[] lineParts = line.split(" ");
        int width = Integer.parseInt(lineParts[0]);
        int height = Integer.parseInt(lineParts[1]);
        int[][] nodes = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                nodes[i][j] = -1;
            }
        }


        HashTable<Integer> sight = new HashTable<>(height * width); // Hashtable used to store impassable nodes
        while (reader.hasNextLine()) {
            line = reader.nextLine();
            lineParts = line.split(" ");
            int x = Integer.parseInt(lineParts[0]);
            int y = Integer.parseInt(lineParts[1]);
            int type = Integer.parseInt(lineParts[2]);
            nodes[y][x] = type;
            if(type == 1) sight.insert(y*height + x); // Inserting impassable nodes with type 1 to a hashtable
        }

        try {
            reader = new Scanner(edgesFile);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find edges file");
            return;
        }

        int graphSize = width * height; // adjacency list to store adjacent nodes
        ArrayList<ArrayList<Pair>> graph = new ArrayList<>(graphSize);
        for (int i = 0; i < graphSize; i++) {
            graph.add(new ArrayList<>());
        }

        while (reader.hasNextLine()) {
            line = reader.nextLine();
            lineParts = line.split(" ");

            double weight = Double.parseDouble(lineParts[1]);
            String[] coordinates = lineParts[0].split(",");
            String[] xy1 = coordinates[0].split("-");
            String[] xy2 = coordinates[1].split("-");

            int x1 = Integer.parseInt(xy1[0]);
            int y1 = Integer.parseInt(xy1[1]);
            int x2 = Integer.parseInt(xy2[0]);
            int y2 = Integer.parseInt(xy2[1]);

            int vertex1 = y1 * height + x1;
            int vertex2 = y2 * height + x2;
            graph.get(vertex1).add(new Pair(vertex2, weight)); // Inserting adjacent nodes
            graph.get(vertex2).add(new Pair(vertex1, weight));
        }

        try {
            reader = new Scanner(missionFile);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find objs file");
            return;
        }

        int radius = Integer.parseInt(reader.nextLine());
        String[] xy = reader.nextLine().split(" ");
        int startX = Integer.parseInt(xy[0]);
        int startY = Integer.parseInt(xy[1]);
        ArrayList<ArrayList<Integer>> missions = new ArrayList<>();
        while (reader.hasNextLine()) {
            lineParts = reader.nextLine().split(" ");
            ArrayList<Integer> objective = new ArrayList<>();
            for (String linePart : lineParts) {
                objective.add(Integer.parseInt(linePart));
            }
            missions.add(objective); // Saving all the objectives

        }
        ArrayList<Double> lengths = new ArrayList<>();
        HashTable<Integer> choicesMade = new HashTable<>(height * width);
        int[] choices = new int[0];
        int exclude = -1;


        // Main loop to process missions one by one
        for (int i = 0; i < missions.size(); i++) {
            int x = missions.get(i).get(0);
            int y = missions.get(i).get(1);
            SeeSight(sight, nodes, startX, startY, radius); // Saving impassable nodes that are in radius of sight

            int choiceMade = -1;
            double shortest = Double.MAX_VALUE;

            /*
            Loop to check if the traveler has given options to choose from
            And choosing the best option among them
             */
            for (int j = 0; j < choices.length; j++) {
                exclude = choices[j];
                ArrayList<Double> length = new ArrayList<>();
                if (choicesMade.contains(choices[j])) continue;
                Dijkstra(width, height, nodes, graph, sight, startX, startY, x, y, length, exclude);
                if (shortest > length.get(0)) {
                    shortest = length.get(0);
                    choiceMade = choices[j];
                }
            }
            exclude = -1;

            // Updating impassable nodes considering the choice made by the traveler
            if(choiceMade != -1) {
                choicesMade.insert(choiceMade);
                for(int j = 0; j < height; j++) {
                    for (int k = 0; k < width; k++) {
                        if(nodes[j][k] == choiceMade) {
                            nodes[j][k] = 0;
                            int vertice = j * height + k;
                            sight.remove(vertice);
                        }
                    }
                }
                output.println("Number " + choiceMade + " is chosen!");
            }

            // Calculating the shortest path

            ArrayList<Integer> path = Dijkstra(width, height, nodes, graph, sight, startX, startY, x, y, lengths, exclude);

            // Loop to move in the path
            boolean reached = false;
            while (!reached) {
                int j = 0;
                while (j < path.size()) {
                    startY = path.get(j) / height;
                    startX = path.get(j) % width;
                    SeeSight(sight, nodes, startX, startY, radius); // Saving impassable nodes that are in radius of sight
                    output.println("Moving to " + startX + "-" + startY);
                    if (inconsistent(path, sight, j)) { // Checking if the path is still passable with the new nodes revealed
                        output.println("Path is impassable!");
                        break;
                    }
                    j++;
                }
                if (j == path.size()) reached = true;

                // Recalculating the path if the objective was not reached
                path = Dijkstra(width, height, nodes, graph, sight, startX, startY, x, y, lengths, exclude);
            }
            output.println("Objective " + (i+1) + " reached!");

            //Checking if wizard proposed any options
            choices = new int[missions.get(i).size() - 2];
            for (int j = 2; j < missions.get(i).size(); j++) {
                choices[j - 2] = missions.get(i).get(j);
            }
        }


        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.print(totalTime/1000000);
        System.out.println(" ms");    }
}