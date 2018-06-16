package Client;

public class Client
{
    public static void main( String args[] ) throws Exception
    {
        System.out.println("Client started, args count = " + args.length);

        if (args.length < 3)
        {
            System.out.println("Usage: <case> <host> <serverPorts>...");
            return;
        }

        String testcase = args[0];
        String host = args[1];
        String[] serversPorts = new String[args.length - 2];

        for(int i = 2;  i < args.length; ++i) {
            serversPorts[i - 2] = args[i];
        }


        System.out.println(serversPorts[0] + " " + serversPorts[1]);
        System.out.println("Getting map");
        Graph graph = Graph.mapGraphFromFile("testcases/" + testcase);
        graph.printWeights();

        System.out.println("Launching Dijkstra");
        new DijkstraClient(graph, host, serversPorts).run();


    }
}
