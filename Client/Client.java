package Client;

import Shared.ServerInterface;
import javafx.util.Pair;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private Graph graph;
    private ServerInterface[] serverNodes;
    private int workerServersCount;
    private int[] workerNodesCount;
    private int[] workerFromNodes;
    private HashSet<Integer> seenNodes;
    private int MAX_INT = 2147483647;
    private ExecutorService executor;

    public Client(Graph graph, String host, String[] serverPorts) throws Exception {
        workerServersCount = serverPorts.length;
        serverNodes = new ServerInterface[workerServersCount];
        workerNodesCount = new int[workerServersCount];
        workerFromNodes = new int[workerServersCount];
        seenNodes = new HashSet<>();

        this.graph = graph;

        for (int i = 0; i < workerServersCount; ++i) {
            Registry reg = LocateRegistry.getRegistry(host, Integer.parseInt(serverPorts[i]));
            serverNodes[i] = (ServerInterface) reg.lookup("server");
        }
        executor = Executors.newFixedThreadPool(workerServersCount);
    }

    private Pair<Integer, Integer> calculateWorkerNodeRanges(int workerNodeId) {
        int nodesCount = graph.getNumberOfVertices();

        int fromNode = (nodesCount / workerServersCount) * workerNodeId;
        int toNode = (nodesCount / workerServersCount) * (workerNodeId + 1) - 1;

        int otherNodesCount = nodesCount % workerServersCount;

        if (workerNodeId < otherNodesCount) {
            fromNode += workerNodeId;
            toNode += workerNodeId + 1;
        } else {
            fromNode += otherNodesCount;
            toNode += otherNodesCount;
        }

        return new Pair<>(fromNode, toNode);
    }

    void run() throws InterruptedException {
        final int[][] weights = graph.getWeights();
        int nodesCount = graph.getNumberOfVertices();

        int[] distances = new int[nodesCount];
        int[] prevNodes = new int[nodesCount];

        for (int i = 0; i < nodesCount; ++i) {
            distances[i] = prevNodes[i] = MAX_INT;
        }

        int initialNode = 0;
        PriorityQueue<Integer> nodesToVisit = new PriorityQueue<>();
        nodesToVisit.add(initialNode);

        List<Callable<Object>> calls = new ArrayList<>();
        for (int i = 0; i < workerServersCount; ++i) {
            final int workerId = i;
            calls.add(Executors.callable(() -> {
                Pair<Integer, Integer> nodeRanges = calculateWorkerNodeRanges(workerId);
                int fromNode = nodeRanges.getKey();
                int toNode = nodeRanges.getValue();
                workerNodesCount[workerId] = toNode - fromNode + 1;
                workerFromNodes[workerId] = fromNode;
                try {
                    serverNodes[workerId].setInitialData(workerId, nodesCount, nodeRanges, weights);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }));
        }
        executor.invokeAll(calls);

        distances[initialNode] = 0;
        seenNodes.add(initialNode);

        while (nodesToVisit.size() != 0) {
            Integer currentNode = nodesToVisit.poll();

            calls = new ArrayList<>();
            for (int i = 0; i < workerServersCount; ++i) {
                final int workerId = i;
                calls.add(Executors.callable(() -> {
                    int[] workerDistances = new int[0];
                    try {
                        workerDistances = serverNodes[workerId].calculateDistances(currentNode, distances[currentNode]);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    System.arraycopy(workerDistances, 0, distances, workerFromNodes[workerId], workerNodesCount[workerId]);
                }));
            }
            executor.invokeAll(calls);

            for (int node = 0; node < nodesCount; ++node) {
                if (!seenNodes.contains(node) && isConnected(currentNode, node)) {
                    nodesToVisit.add(node);
                    seenNodes.add(node);
                }
            }
        }

        calls = new ArrayList<>();
        for(int i = 0; i < workerServersCount; ++i) {
            final int workerId = i;
            calls.add(Executors.callable(() -> {
                try {
                    int[] workerPrevNodes = serverNodes[workerId].getWorkerPrevNodesPart();
                    System.arraycopy(workerPrevNodes, 0, prevNodes, workerFromNodes[workerId], workerNodesCount[workerId]);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }));
        }
        executor.invokeAll(calls);

        System.out.print("Distances = [");
        for (int node = 0; node < nodesCount; ++node) {
            if (distances[node] == MAX_INT)
                System.out.print("X, ");
            else
                System.out.print(distances[node] + ", ");
        }
        System.out.println("\b\b]");

        System.out.print("Path = [");
        for(int node = 0; node < nodesCount; ++node) {
            if (node == initialNode)
                System.out.print("X, ");
            else
                System.out.print(prevNodes[node] + ", ");
        }
        System.out.println("\b\b]");

        executor.shutdown();
    }

    private boolean isConnected(int fromNode, int toNode) {

        return this.graph.getWeights()[fromNode][toNode] != -1;
    }
}