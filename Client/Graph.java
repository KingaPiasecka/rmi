package Client;

import java.io.BufferedReader;
import java.io.FileReader;


public class Graph {
    private static final int NO_EDGE = -1;
    private static final String NO_WEIGHT = "0";
    private final static String DELIMITER = " ";

    private int[][] weights;
    private int numberOfVertices;

    private Graph(int numberOfVertices) {
        this.weights = new int[numberOfVertices][numberOfVertices];
        this.numberOfVertices = numberOfVertices;
    }

    public int[][] getWeights() {
        return weights;
    }

    public int getNumberOfVertices() {
        return numberOfVertices;
    }

    public static Graph mapGraphFromFile(String filename) throws Exception {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String header = bufferedReader.readLine();

            int numberOfVertices = Integer.parseInt(header);

            if (validateNumberOfVertices(numberOfVertices)) {
                Graph graph = new Graph(numberOfVertices);

                for (int i = 0; i < numberOfVertices; ++i) {
                    String line = bufferedReader.readLine();
                    String[] weights = line.split(DELIMITER);

                    for (int j = 0; j < numberOfVertices; ++j) {
                        if (noConnection(weights[j])) {
                            graph.weights[i][j] = NO_EDGE;
                        } else {
                            graph.weights[i][j] = Integer.parseInt(weights[j]);
                        }
                    }
                }
                return graph;
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("Wrong input file format - invalid argument for the number of vertices");
            throw e;
        } catch (Exception e) {
            System.out.println("Wrong input file format");
            throw e;
        }
    }

    private static boolean noConnection(String weight) {
        return weight.contains(NO_WEIGHT);
    }

    private static boolean validateNumberOfVertices(int numberOfVertices) {
        return numberOfVertices > 0;
    }
}

