package Client;

public class Main {
    public static void main(String args[]) throws Exception {
        String example = args[0];
        String hostIP = args[1];
        String[] serversPorts = new String[args.length - 2];

        for(int i = 2;  i < args.length; ++i) {
            serversPorts[i - 2] = args[i];
        }

        // graph = Graph.mapGraphFromFile("cases/" + example);
        //graph.printWeights();
        Map graph = Map.fromFile("cases/testcase0");

        new Client(graph, hostIP, serversPorts).run();
    }
}
