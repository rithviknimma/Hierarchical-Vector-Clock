package HVC;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;


public class Process implements ProcessRMI, Runnable {

    Registry registry;
    ProcessRMI stub;

    int id;
    String[] peers;
    int[] ports;
    int[] vc;

    public Process(int id, String[] peers, int[] ports){
        this.id = id;
        this.peers = peers;
        this.ports = ports;
        vc = new int[ports.length];

        try {
            System.setProperty("java.rmi.hostname", this.peers[this.id]);
            this.registry = LocateRegistry.createRegistry(this.ports[this.id]);
            this.stub = (ProcessRMI) UnicastRemoteObject.exportObject(this, this.ports[this.id]);
            this.registry.rebind("Process", stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message Send() {
        Message m = new Message();
        try {
            Registry registry = LocateRegistry.getRegistry(this.ports[id]);
            stub = (ProcessRMI) registry.lookup("Process");
            stub.Receive(m);
        } catch (Exception e) {
            return null;
        }
    }

    public void Receive(Message m) {
        int idx = vc.length - m.clock.length;
        for (int i : m.clock) {
            vc[idx] = Math.max(vc[idx], i);
            idx++;
        }
    }

    public void InternalEvent() {
        this.vc[this.id] += 1;
    }
}
