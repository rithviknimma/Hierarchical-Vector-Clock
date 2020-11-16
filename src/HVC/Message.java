package HVC;

import java.util.*;

public class Message {
    ArrayList<ArrayList<Integer>> clock;
    int sender;

    public Message(ArrayList<ArrayList<Integer>> clock, int sender) {
        this.clock = clock;
        this.sender = sender;
    }

}
