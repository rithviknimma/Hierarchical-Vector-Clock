package HVC;

import java.util.*;

public class ProcessGroup {

    ArrayList<ProcessGroup> processGroups;
    int id;
    int level;
    int localIdx;
    ProcessGroup parent;

    public ProcessGroup(int id, int level, ArrayList<ProcessGroup> processGroups) {
        this.id = id;
        this.level = level;
        this.processGroups = new ArrayList<>();
        if (processGroups != null) {
            for (ProcessGroup pg : processGroups) {
                this.processGroups.add(pg);
            }
        }
    }

    public void setParent(ProcessGroup p) {
        this.parent = p;
    }

    public void setLocalIdx(int i) {
        this.localIdx = i;
    }

    public int groupSize() {
        return this.processGroups.size();
    }
}
