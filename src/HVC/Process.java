package HVC;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;


public class Process implements ProcessRMI {

    Registry registry;
    ProcessRMI stub;

    int id;
    int localIdx;
    String[] peers;
    int[] ports;
    ArrayList<ArrayList<Integer>> vc;
    ProcessGroup parent;
    ProcessGroup self;

    public Process(int id, int localIdx, String[] peers, int[] ports, int height, ProcessGroup parent, int level){
        this.id = id;
        this.localIdx = localIdx;
        this.peers = peers;
        this.ports = ports;
        this.parent = parent;
        this.self = new ProcessGroup(id, level, (ProcessGroup[]) null);
        vc = new ArrayList<ArrayList<Integer>>(height);

        ProcessGroup next = parent;
        for (int i = 0; i < height; i++) {
            vc.set(i, new ArrayList<Integer>(next.groupSize()));
            next = next.parent;
        }

        try {
            System.setProperty("java.rmi.hostname", this.peers[this.id]);
            this.registry = LocateRegistry.createRegistry(this.ports[this.id]);
            this.stub = (ProcessRMI) UnicastRemoteObject.exportObject(this, this.ports[this.id]);
            this.registry.rebind("Process", stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message Send(int id) {
        Process dest;
        try {
            Registry registry = LocateRegistry.getRegistry(this.ports[id]);
            stub = (ProcessRMI) registry.lookup("Process");
            dest = stub.GetProcess();
        } catch (Exception e) {
            return null;
        }
        int hDist = calculateHierarchicalDistance(this, dest);
        Message m;
        if (hDist != 1) {
            ArrayList<ArrayList<Integer>> mClock = new ArrayList<>(this.vc.size() - hDist + 1);
            mClock.set(0, this.vc.get(hDist-1));
            mClock.get(0).set(localIdx, this.vc.get(0).get(localIdx));
            for (int i = 1; i < this.vc.size() - hDist + 1; i++) {
                mClock.set(i, this.vc.get(hDist-1+i));
            }
            m = new Message(mClock, this.id);
        }
        else {
            m = new Message(this.vc, this.id);
        }
        try {
            Registry registry = LocateRegistry.getRegistry(this.ports[id]);
            stub = (ProcessRMI) registry.lookup("Process");
            stub.Receive(m);
        } catch (Exception e) {
            return null;
        }
        return m;
    }

    public void Receive(Message m) {
        int idx = vc.size() - m.clock.size();
        for (ArrayList<Integer> arr : m.clock) {
            int nodeIdx = 0;
            for (Integer i : arr) {
                this.vc.get(idx).set(nodeIdx, Math.max(this.vc.get(idx).get(nodeIdx), i));
                nodeIdx++;
            }
            idx++;
        }
    }

    public void InternalEvent() {
        this.vc.get(0).set(this.id, this.vc.get(0).get(this.localIdx) + 1);
    }

    public Process getProcess() {
        return this;
    }

    public int calculateHierarchicalDistance(Process sender, Process receiver) {
        int dist = 0;
        if (sender.self.id == sender.self.id) {
            return dist;
        }
        dist = 1;
        if (sender.parent.id == receiver.parent.id) {
            return dist;
        }
        else {
            ProcessGroup a = sender.parent;
            ProcessGroup b = receiver.parent;
            while (a.parent.id != b.parent.id) {
                dist++;
                a = a.parent;
                b = b.parent;
            }
        }
        return dist;
    }
}
