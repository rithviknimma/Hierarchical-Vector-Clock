package HVC;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProcessRMI extends Remote {
    Message Send(Message m) throws RemoteException;
    void Receive(Message m) throws RemoteException;
    void InternalEvent();
    Process GetProcess() throws RemoteException;
}
