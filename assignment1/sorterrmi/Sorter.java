import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Sorter extends Remote {
    public void pushValue(int val) throws RemoteException;
    public void pushOperator(String operator) throws RemoteException;
    public int pop() throws RemoteException;
    public boolean isEmpty() throws RemoteException;
    public int delayPop(int millis) throws RemoteException;
    public void delay(int millis) throws RemoteException;
}
