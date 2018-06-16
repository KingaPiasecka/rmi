package Shared;


import java.rmi.*;

public interface ServerInterface extends Remote {
    public void setInitialData(int workerId, int nodesCount, int[] ranges, int[][] weights) throws RemoteException;
    public int[] computeDistances(Integer currentNode, int distanceToCurrentNode) throws RemoteException;
    public int[] getWorkerPrevNodesPart() throws RemoteException;
}
