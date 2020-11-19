package HVC;

import java.util.*;

public class ProcessGroup {

    ArrayList<ProcessGroup> processGroups;
    int id;
    int level;
    int localIdx;
    ProcessGroup parent;

    public ProcessGroup(int id, int level, int localIdx, ArrayList<ProcessGroup> processGroups) {
        this.id = id;
        this.level = level;
        this.localIdx = localIdx;
        this.processGroups = processGroups;
    }

    public void setParent(ProcessGroup p) {
        this.parent = p;
    }

    public int groupSize() {
        return this.processGroups.size();
    }
}
