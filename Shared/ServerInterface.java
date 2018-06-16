package Shared;

import javafx.util.Pair;

import java.rmi.*;

public interface ServerInterface extends Remote {
    public void setInitialData(int workerId, int nodesCount, Pair<Integer, Integer> ranges, int[][] weights) throws RemoteException;
    public int[] calculateDistances(Integer currentNode, int distanceToCurrentNode) throws RemoteException;
    public int[] getWorkerPrevNodesPart() throws RemoteException;
}
