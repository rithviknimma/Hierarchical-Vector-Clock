package HVC;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HVCTest {

    private Process[] initProcessesAndGroups(int nProcesses, ArrayList<Integer[]> groupings, int height) {
        String host = "127.0.0.1";
        String[] peers = new String[nProcesses];
        int[] ports = new int[nProcesses];
        Process[] processes = new Process[nProcesses];
        for (int i = 0; i < nProcesses; i++) {
            ports[i] = 1100+i;
            peers[i] = host;
        }
        int idx = 0;
        int sum = groupings.get(0)[0];
        int count = 0;
        int offset = 1;
        ArrayList<ProcessGroup> firstLevel = new ArrayList<>();
        ArrayList<Process> subgroup = new ArrayList<>();
        ArrayList<ProcessGroup> nextLevel = new ArrayList<>();

        for (int i = 0; i < nProcesses; i++) {
            System.out.println("i: " + i + ", idx: " + idx + ", sum: " + sum + ", count: " + count + ", offset: " + offset);
            processes[i] = new Process(i, count%groupings.get(0)[idx], peers, ports, height);
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
                System.out.println(firstLevel);
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
                    for(ProcessGroup pg : nextLevel) {
                        pg.setParent(root);
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
                    for (ProcessGroup pg : subList) {
                        pg.setParent(newLevel);
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

    @Test
    public void TestBasic() {
        int numProcesses = 5;
        ArrayList<Integer[]> org = new ArrayList<>();
        org.add(new Integer[]{2, 2, 1});
        org.add(new Integer[]{3});
        Process[] processes = initProcessesAndGroups(numProcesses, org, 2);

        for (Process p : processes) {
            System.out.println(p.id);
        }

    }
}
