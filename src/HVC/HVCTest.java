package HVC;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HVCTest {

    private Process[] initProcesses(int nProcesses, int[] groupings, int height) {
        String host = "127.0.0.1";
        String[] peers = new String[nProcesses];
        int[] ports = new int[nProcesses];
        Process[] processes = new Process[5];
        int idx = 0;
        int sum = groupings[0];
        int count = 0;
        int offset = 1;
        ArrayList<ProcessGroup> p = new ArrayList();

        for (int i = 0; i < nProcesses; i++) {
            processes[i] = new Process(i, count%groupings[idx], peers, ports, height);
            p.add(processes[i].self);
            count++;
            if(i > sum) {
                idx++;
                sum+= groupings[idx];
                count = 0;
                ProcessGroup g = new ProcessGroup(nProcesses + offset, 2, p);
                offset++;
                p.clear();
            }
        }

        return processes;
    }

    public void initProcessGroups(Process[] processes, ArrayList<Integer[]> groupings) {
        int idx = 0;
        int sum = groupings.get(0)[0];
        int count = 0;

        ArrayList<Process> p = new ArrayList();
        for (int j = 0; j < groupings.get(0).length; j++) {
            p.add(processes[idx]);
            if(j > sum) {
                idx++;
                sum += groupings[idx];
                new ProcessGroup()
            }
        }
    }

    @Test
    public void TestBasic() {
        int numProcesses = 5;
        Process[] processes = initProcesses(numProcesses, new int[]{2, 2, 1}, 2);
        ArrayList<Integer[]> org = new ArrayList<>();
        org.add(new Integer[]{2, 2, 1});
        org.add(new Integer[]{1});
        initProcessGroups(processes, org);

    }
}
