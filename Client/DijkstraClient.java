package Client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.*;
import java.util.*;
import java.rmi.*;
import Shared.*;

public class DijkstraClient {
    private Map map;
    private int workerServersCount;
    private ExecutorService executor;
    private ServerInterface[] workerServers;
    private int[] workerNodesCount;
    private int[] workerFromNodes;
    private HashSet<Integer> nodesAlreadySeen;

    public DijkstraClient(Map map, String host, String[] serverPorts) throws Exception {
        workerServersCount = serverPorts.length;
        workerServers = new ServerInterface[workerServersCount];
        workerNodesCount = new int[workerServersCount];
        workerFromNodes = new int[workerServersCount];
        nodesAlreadySeen = new HashSet<>();
        
        this.map = map;
        
        for(int i=0; i<workerServersCount; ++i) {
            Registry reg = LocateRegistry.getRegistry(host, Integer.parseInt(serverPorts[i]));
            workerServers[i] = (ServerInterface) reg.lookup("server");
        }
        executor = Executors.newFixedThreadPool(workerServersCount);
    }

    private int[] calculateWorkerNodeRanges(int workerNodeId) {
        int nodesCount = map.getNodesCount();
        int[] resultsPair = new int[2];

        int fromNode = (nodesCount / workerServersCount) * workerNodeId;
        int toNode = (nodesCount / workerServersCount) * (workerNodeId + 1) - 1;

        int otherNodesCount = nodesCount % workerServersCount;

        if (workerNodeId < otherNodesCount) {
            fromNode += workerNodeId;
            toNode += workerNodeId + 1;
        }
        else {
            fromNode += otherNodesCount;
            toNode += otherNodesCount;
        }

        resultsPair[0] = fromNode;
        resultsPair[1] = toNode;

        return resultsPair;
    }

    public void run() throws InterruptedException, RemoteException {
        final int[][] weights = map.getWeights();
        int nodesCount = map.getNodesCount();
        
        int[] distances = new int[nodesCount];
        int[] prevNodes = new int[nodesCount];
        
        for(int i=0; i<nodesCount; ++i)
            distances[i] = prevNodes[i] = Integer.MAX_VALUE;
        
        int initialNode = 0;
        PriorityQueue<Integer> nodesToVisitQ = new PriorityQueue<>();
        nodesToVisitQ.add(initialNode);
        
        System.out.println("Sending weights to workers...");
        List<Callable<Object>> calls = new ArrayList<>();
        for(int i=0; i<workerServersCount; ++i) {
            final int workerId = i;
            calls.add(Executors.callable(() -> {
                System.out.println("Sending weights to worker " + workerId);
                try {
                    int[] nodeRanges = calculateWorkerNodeRanges(workerId);
                    int fromNode = nodeRanges[0];
                    int toNode = nodeRanges[1];
                    workerNodesCount[workerId] = toNode - fromNode + 1;
                    workerFromNodes[workerId] = fromNode;
                    workerServers[workerId].setInitialData(workerId, nodesCount, nodeRanges, weights);
                }
                catch(RemoteException e) {
                    e.printStackTrace();
                }
            }));
        }
        executor.invokeAll(calls);

        distances[initialNode] = 0;
        nodesAlreadySeen.add(initialNode);
        
        while(nodesToVisitQ.size() != 0) {
            Integer currentNode = nodesToVisitQ.poll();
            System.out.println("Going through node = " + currentNode);
            
            calls = new ArrayList<>();
            for(int i=0; i<workerServersCount; ++i) {
                final int workerId = i;
                calls.add(Executors.callable(() -> {
                    System.out.println("Sending weights to worker " + workerId);
                    try {
                        int[] workerDistances = workerServers[workerId].calculateDistances(currentNode, distances[currentNode]);
                        System.arraycopy(workerDistances, 0, distances, workerFromNodes[workerId], workerNodesCount[workerId]);
                    }
                    catch(RemoteException e) {
                        e.printStackTrace();
                    }
                }));
            }
            executor.invokeAll(calls);
            
            for(int node=0; node<nodesCount; ++node) {
                if (nodesAlreadySeen.contains(node) == false && isConnected(currentNode, node)) {
                    nodesToVisitQ.add(node);
                    nodesAlreadySeen.add(node);
                }
            }

        }
        
        calls = new ArrayList<>();
        for(int i=0; i<workerServersCount; ++i) {
            final int workerId = i;
            calls.add(Executors.callable(() -> {
                try {
                    int[] workerPrevNodes = workerServers[workerId].getWorkerPrevNodesPart();
                    System.out.println(workerId + ", fromNode=" + workerFromNodes[workerId] + ", count=" + workerNodesCount[workerId]);
                    System.arraycopy(workerPrevNodes, 0, prevNodes, workerFromNodes[workerId], workerNodesCount[workerId]);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }));
        }
        executor.invokeAll(calls);
        
        System.out.println("Dijkstra algorithm over");
        System.out.println("Started from node index = " + initialNode);
        System.out.print("Distances (X means no path) = [");
        for(int node=0; node<nodesCount; ++node) {
            if (distances[node] == Integer.MAX_VALUE)
                System.out.print("X, ");
            else
                System.out.print(distances[node] + ", ");
        }
        System.out.println("\b\b]");
        
        System.out.print("PrevNodes (X means initialNode) = [");
        for(int node=0; node<nodesCount; ++node) {
            if (node == initialNode)
                System.out.print("X, ");
            else
                System.out.print(prevNodes[node] + ", ");
        }
        System.out.println("\b\b]");
        
        executor.shutdown();
    }
    
    private boolean isConnected(int fromNode, int toNode) {
        return this.map.getWeights()[fromNode][toNode] != -1;
    }


}
