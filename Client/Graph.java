package Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class Graph
{
    private static final int NO_EDGE = -1;
    private static final int NO_WEIGHT = '0';
    private final static String DELIMITER = " ";

    private List<String> nodes;
    private int[][] weights;
    private int numberOfVertices;

    private Graph(int numberOfVertices)
    {
        this.nodes = new ArrayList<>();
        this.weights = new int[numberOfVertices][numberOfVertices];
        this.numberOfVertices = numberOfVertices;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public int[][] getWeights() {
        return weights;
    }

    public int getNumberOfVertices() {
        return numberOfVertices;
    }

    public static Graph mapGraphFromFile(String filename) throws Exception
    {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String header = br.readLine();
            if (!header.startsWith("vertices"))
                throw new RuntimeException("Invalid testcase format.");

            int verticesCount = Integer.parseInt(header.replaceAll("[^0-9]", ""));
            Graph m = new Graph(verticesCount);

            for (int i = 0; i < verticesCount; ++i) {
                String line = br.readLine();
                String[] cases = line.split(DELIMITER);

                for (int j = 0; j < verticesCount; ++j) {
                    String numstr = cases[j].trim();
                    if (numstr.contains("-"))
                        m.weights[i][j] = NO_EDGE;
                    else
                        m.weights[i][j] = Integer.parseInt(numstr);
                }
            }

            for (int i = 0; i < verticesCount; ++i) {
                String nodeName = String.valueOf((char)('A' + i));
                System.out.println("Adding node: " + nodeName);
                m.nodes.add(nodeName);
            }

            return m;
        }
        catch (Exception e) {
            System.out.println("Couldn't handle file properly: " + e.getMessage());
            throw e;
        }
    }

    void printWeights()
    {
        for (int i = 0; i < numberOfVertices; ++i) {
            for (int j = 0; j < numberOfVertices; ++j) {
                if (weights[i][j] != NO_EDGE)
                    System.out.print(weights[i][j] + " ");
                else
                    System.out.print("-" + " ");
            }
            System.out.println();
        }
    }
}

