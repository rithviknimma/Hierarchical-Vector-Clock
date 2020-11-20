package HVC;

import org.junit.Test;

import java.util.ArrayList;

public class HVCTest {

    private Process[] initProcessesAndGroups(int nProcesses, ArrayList<Integer[]> groupings) {
        Process[] processes = new Process[nProcesses];
        int idx = 0;
        int sum = groupings.get(0)[0];
        int count = 0;
        int offset = 1;
        ArrayList<ProcessGroup> firstLevel = new ArrayList<>();
        ArrayList<Process> subgroup = new ArrayList<>();
        ArrayList<ProcessGroup> nextLevel = new ArrayList<>();

        for (int i = 0; i < nProcesses; i++) {
            processes[i] = new Process(i, count%groupings.get(0)[idx], groupings.size());
            processes[i].self.setLocalIdx(count%groupings.get(0)[idx]);
            firstLevel.add(processes[i].self);
            subgroup.add(processes[i]);
            count++;
            if(i + 1 >= sum) {
                idx++;
                if (idx < groupings.get(0).length)
                    sum += groupings.get(0)[idx];
                else
                    sum = Integer.MAX_VALUE;
                count = 0;
                ProcessGroup g = new ProcessGroup(nProcesses + offset, 2, firstLevel);
                for(Process p : subgroup) {
                    p.setParent(g);
                }
                nextLevel.add(g);
                offset++;
                firstLevel.clear();
                subgroup.clear();
            }

        }

        int id = nProcesses + offset;

        for (int i = 1; i < groupings.size(); i++) {
            count = 0;
            ArrayList<ProcessGroup> temp = new ArrayList<>();
            for (int j = 0; j < groupings.get(i).length; j++) {
                if (groupings.get(i).length == 1) {
                    ProcessGroup root = new ProcessGroup(id, i+2, nextLevel);
                    root.setParent(null);
                    int local = 0;
                    for(ProcessGroup pg : nextLevel) {
                        pg.setParent(root);
                        pg.setLocalIdx(local);
                        local++;
                    }
                    id++;
                }
                else {
                    ArrayList<ProcessGroup> subList = new ArrayList<>();
                    for (int k = 0; k < groupings.get(i)[j]; k++) {
                        subList.add(nextLevel.get(count));
                        count++;
                    }
                    ProcessGroup newLevel = new ProcessGroup(id, i+2, subList);
                    int localIdx = 0;
                    for (ProcessGroup pg : subList) {
                        pg.setParent(newLevel);
                        pg.setLocalIdx(localIdx);
                        localIdx++;
                    }
                    subList.clear();
                    id++;
                    temp.add(newLevel);
                }
            }
            nextLevel.clear();
            for (ProcessGroup node : temp) {
                nextLevel.add(node);
            }
        }

        for (Process p : processes) {
            p.initializeClock();
        }

        return processes;
    }

    private Message transmitMessage (Process sender, Process receiver) {
        Message m = sender.Send(receiver);
        receiver.Receive(m);
        return m;
    }

    private boolean happenedBefore(ArrayList<ArrayList<Integer>> clockA, ArrayList<ArrayList<Integer>> clockB, int hDist, int localIdx) {
        assert (clockA.size() == clockB.size());
        for (int i = clockA.size() - 1; i > hDist; i--) {
            for (int j = 0; j < clockA.get(i).size(); j++) {
                if(clockA.get(i).get(j) > clockB.get(i).get(j)) {
                    return false;
                }
            }
        }
        //todo: happenedBefore logic with localidx
        if (clockA.get(0).get(localIdx))
        return true;
    }

    @Test
    public void TestBasic() {
        int numProcesses = 5;
        ArrayList<Integer[]> org = new ArrayList<>();
        org.add(new Integer[]{2, 2, 1});
        org.add(new Integer[]{3});
        Process[] processes = initProcessesAndGroups(numProcesses, org);

        ArrayList<ArrayList<Integer>> clockA;
        ArrayList<ArrayList<Integer>> clockB;

        processes[0].InternalEvent();
        processes[0].InternalEvent();
        transmitMessage(processes[0], processes[1]);
        clockA = processes[0].vc;
        transmitMessage(processes[1], processes[4]);
        clockB = processes[4].vc;
        processes[3].InternalEvent();
        transmitMessage(processes[3], processes[4]);

        assert happenedBefore(clockA, clockB, processes[0].calculateHierarchicalDistanceAndIndex(processes[0], processes[4])[0]);
    }

    @Test
    public void TestIntermediate() {
        int numProcesses = 8;
        ArrayList<Integer[]> org = new ArrayList<>();
        org.add(new Integer[]{2, 2, 2, 2});
        org.add(new Integer[]{2, 2});
        org.add(new Integer[]{2});
        Process[] processes = initProcessesAndGroups(numProcesses, org);

        processes[0].InternalEvent();
        transmitMessage(processes[1], processes[0]);
        processes[3].InternalEvent();
        transmitMessage(processes[4], processes[3]);
        processes[7].InternalEvent();
        transmitMessage(processes[5], processes[7]);
        processes[2].InternalEvent();
        processes[2].InternalEvent();
        processes[2].InternalEvent();
        transmitMessage(processes[0], processes[2]);
        processes[5].InternalEvent();
        processes[5].InternalEvent();
        transmitMessage(processes[3], processes[5]);
        processes[6].InternalEvent();
        processes[6].InternalEvent();
        processes[6].InternalEvent();
        transmitMessage(processes[7], processes[6]);
        processes[4].InternalEvent();
        processes[4].InternalEvent();
        processes[4].InternalEvent();
        processes[4].InternalEvent();
        transmitMessage(processes[6], processes[4]);
        processes[0].InternalEvent();
        transmitMessage(processes[3], processes[0]);
        processes[7].InternalEvent();

        for(Process p : processes) {
            System.out.println("Process ID: " + p.id + ", HVC: " + p.vc);
        }

        assert(happenedBefore(processes[1].vc, processes[0].vc, 1, processes[1].localIdx));
        assert(happenedBefore(processes[6].vc, processes[4].vc, 2, processes[6].localIdx));
    }
}

