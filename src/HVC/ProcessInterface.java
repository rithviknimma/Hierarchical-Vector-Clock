package HVC;


public interface ProcessInterface {
    Message Send(Process p, Object value);
    void Receive(Message m);
    void InternalEvent();
}
