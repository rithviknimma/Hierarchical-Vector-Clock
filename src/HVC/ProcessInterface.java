package HVC;


public interface ProcessInterface {
    Message Send(Process p);
    void Receive(Message m);
    void InternalEvent();
    Process GetProcess();
}
