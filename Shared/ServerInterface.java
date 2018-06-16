package Shared;

import javafx.util.Pair;

import java.rmi.*;

public interface ServerInterface extends Remote {
    void setInitialData(int workerId, int nodesCount, Pair<Integer, Integer> ranges, int[][] weights) throws RemoteException;
    int[] calculateDistances(Integer currentNode, int distanceToCurrentNode) throws RemoteException;
    int[] getWorkerPrevNodesPart() throws RemoteException;
}
