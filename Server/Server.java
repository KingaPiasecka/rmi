package Server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.HashSet;

import Shared.*;
import javafx.util.Pair;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private int[][] weights;
    private int fromNode;
    private int toNode;
    private int workerId;
    private int nodesCount;
    private int[] distances;
    private int[] prevNodes;
    private HashSet<Integer> visitedNodes;
    private static int MAX_INT = 2147483647;

    public Server() throws RemoteException {
        super();
    }

    public static void main(String args[]) throws RemoteException {
        if (args.length < 2) {
            return;
        }

        String host = args[0];

        for (int i = 1; i < args.length; ++i) {
            String port = args[i];
            System.setProperty("java.rmi.server.hostname", host);
            Registry reg = LocateRegistry.createRegistry(Integer.parseInt(port));
            reg.rebind("server", new Server());
        }
    }

    public void setInitialData(int workerId, int nodesCount, Pair<Integer, Integer> ranges, int[][] weights) throws RemoteException {
        this.weights = weights;
        this.workerId = workerId;
        this.fromNode = ranges.getKey();
        this.toNode = ranges.getValue();
        this.nodesCount = nodesCount;
        this.visitedNodes = new HashSet<>();
        this.distances = new int[nodesCount];
        this.prevNodes = new int[nodesCount];

        for (int i = 0; i < nodesCount; ++i)
            this.distances[i] = this.prevNodes[i] = MAX_INT;
    }

    public int[] calculateDistances(Integer currentNode, int distanceToCurrentNode) {
        distances[currentNode] = distanceToCurrentNode;

        for (int node = this.fromNode; node <= this.toNode; ++node) {
            if (visitedNodes.contains(node)) {
                continue;
            }

            if (isConnected(currentNode, node)) {
                int nodeDistance = this.weights[currentNode][node];
                int totalCostToNode = distances[currentNode] + nodeDistance;

                if (totalCostToNode < distances[node]) {
                    distances[node] = totalCostToNode;
                    prevNodes[node] = currentNode;
                }
            }
        }

        visitedNodes.add(currentNode);
        return this.getWorkerDistancesPart();
    }

    public int[] getWorkerPrevNodesPart() {
        return this.getWorkerArrayPart(this.prevNodes);
    }

    private boolean isConnected(int fromNode, int toNode) {
        return this.weights[fromNode][toNode] != -1;
    }

    private int[] getWorkerDistancesPart() {
        return this.getWorkerArrayPart(this.distances);
    }

    private int[] getWorkerArrayPart(int[] array) {
        int[] result = new int[this.toNode - this.fromNode + 1];
        for (int i = this.fromNode; i <= this.toNode; ++i) {
            result[i - this.fromNode] = array[i];
        }

        return result;
    }
}
