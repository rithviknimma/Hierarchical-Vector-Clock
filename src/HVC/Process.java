package HVC;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicBoolean;


public class Process implements ProcessInterface {

    int id;
    int localIdx;
    ArrayList<ArrayList<Integer>> vc;
    ProcessGroup parent;
    ProcessGroup self;
    int height;
    boolean initialized = false;

    public Process(int id, int localIdx, int height){
        this.id = id;
        this.localIdx = localIdx;
        this.height = height;
        this.self = new ProcessGroup(id, 1, (ArrayList<ProcessGroup>) null);
        this.self.setLocalIdx(localIdx);
        vc = new ArrayList<ArrayList<Integer>>(height);

    }

    public Message Send(Process dest, Object value) {
        assert(this.initialized);
        assert(dest != this);
        this.vc.get(0).set(this.localIdx, this.vc.get(0).get(this.localIdx) + 1);
        int[] hDistAndIndex = calculateHierarchicalDistanceAndIndex(this, dest);
        Message m;
        if (hDistAndIndex[0] != 1) {
            ArrayList<ArrayList<Integer>> mClock = new ArrayList<>(this.vc.size() - hDistAndIndex[0] + 1);
            ArrayList<Integer> row = new ArrayList<>();
            for (Integer i : this.vc.get(hDistAndIndex[0]-1)) {
                row.add(i);
            }
            row.set(hDistAndIndex[1], this.vc.get(0).get(this.localIdx));
            mClock.add(new ArrayList<Integer>());
            for(Integer i : row) {
                mClock.get(0).add(i);
            }
            for (int i = 1; i < this.vc.size() - hDistAndIndex[0] + 1; i++) {
                row.clear();
                for (Integer j : this.vc.get(hDistAndIndex[0]-1+i)) {
                    row.add(j);
                }
                mClock.add(new ArrayList<Integer>());
                for(Integer j : row) {
                    mClock.get(i).add(j);
                }
            }
            m = new Message(mClock, value);
        }
        else {
            m = new Message(this.vc, value);
        }
        return m;
    }

    public void Receive(Message m) {
        assert(this.initialized);
        int idx = vc.size() - m.clock.size();
        this.vc.get(0).set(this.localIdx, this.vc.get(0).get(this.localIdx) + 1);
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
        this.vc.get(0).set(this.localIdx, this.vc.get(0).get(this.localIdx) + 1);
    }

    public int[] calculateHierarchicalDistanceAndIndex(Process sender, Process receiver) {
        assert (sender.self.id != receiver.self.id);
        int dist = 1;
        int idx = sender.localIdx;
        if (sender.parent.id == receiver.parent.id) {
            return new int[] {dist, idx};
        }
        else {
            dist = 2;
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

    public void setParent(ProcessGroup p) {
        this.parent = p;
    }

    public void initializeClock() {
        ProcessGroup current = this.parent;
        for (int i = 0; i < height; i++) {
            vc.add(new ArrayList<Integer>(current.groupSize()));
            for (int j = 0; j < current.groupSize(); j++) {
                vc.get(i).add(0);
            }
            current = current.parent;
        }
        this.initialized = true;
    }
}
