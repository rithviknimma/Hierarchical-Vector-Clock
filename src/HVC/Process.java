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
    int height;
    boolean initialized = false;

    public Process(int id, int localIdx, String[] peers, int[] ports, int height){
        this.id = id;
        this.localIdx = localIdx;
        this.peers = peers;
        this.ports = ports;
        this.height = height;
        this.self = new ProcessGroup(id, 1, 0, (ArrayList<ProcessGroup>) null);
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
        assert(this.initialized);
        Process dest;
        try {
            Registry registry = LocateRegistry.getRegistry(this.ports[id]);
            stub = (ProcessRMI) registry.lookup("Process");
            dest = stub.GetProcess();
        } catch (Exception e) {
            return null;
        }
        int[] hDistAndIndex = calculateHierarchicalDistanceAndIndex(this, dest);
        Message m;
        if (hDistAndIndex[0] != 1) {
            ArrayList<ArrayList<Integer>> mClock = new ArrayList<>(this.vc.size() - hDistAndIndex[0] + 1);
            mClock.set(0, this.vc.get(hDistAndIndex[0]-1));
            mClock.get(0).set(hDistAndIndex[1], this.vc.get(0).get(this.localIdx));
            for (int i = 1; i < this.vc.size() - hDistAndIndex[0] + 1; i++) {
                mClock.set(i, this.vc.get(hDistAndIndex[0]-1+i));
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
        assert(this.initialized);
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
        assert(this.initialized);
        this.vc.get(0).set(this.id, this.vc.get(0).get(this.localIdx) + 1);
    }

    public Process GetProcess() {
        return this;
    }

    public int[] calculateHierarchicalDistanceAndIndex(Process sender, Process receiver) {
        assert (sender.self.id != receiver.self.id);
        int dist = 1;
        int idx = sender.localIdx;
        if (sender.parent.id == receiver.parent.id) {
            return new int[] {dist, idx};
        }
        else {
            ProcessGroup a = sender.parent;
            idx = a.localIdx;
            ProcessGroup b = receiver.parent;
            while (a.parent.id != b.parent.id) {
                dist++;
                a = a.parent;
                b = b.parent;
                idx = a.localIdx;
            }
        }
        return new int[]{dist, idx};
    }

    public void setParentAndInitialize(ProcessGroup p) {
        this.parent = p;
        ProcessGroup next = parent;
        for (int i = 0; i < height; i++) {
            vc.set(i, new ArrayList<Integer>(next.groupSize()));
            next = next.parent;
        }
        this.initialized = true;
    }
}
