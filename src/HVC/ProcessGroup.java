package HVC;

public class ProcessGroup {

    ProcessGroup[] processGroups;
    int id;
    int level;
    ProcessGroup parent;

    public ProcessGroup(int id, int level, ProcessGroup[] processGroups) {
        this.id = id;
        this.level = level;
        this.processGroups = processGroups;
    }

    public void setParent(ProcessGroup p) {
        this.parent = p;
    }

    public int groupSize() {
        return this.processGroups.length;
    }
}
