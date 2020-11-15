package HVC;

public class ProcessGroup {

    boolean firstLevel;
    Process[] processes;
    ProcessGroup[] processGroups;
    int id;
    int level;
    ProcessGroup parent;

    public ProcessGroup(int id, int level, Process[] processes) {
        this.id = id;
        this.level = level;
        this.firstLevel = true;
        this.processes = processes;
    }

    public ProcessGroup(int id, int level, ProcessGroup[] processGroups) {
        this.id = id;
        this.level = level;
        this.processGroups = processGroups;
        this.firstLevel = false;
    }

    public void setParent(ProcessGroup p) {
        this.parent = p;
    }
}
