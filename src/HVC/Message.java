package HVC;

import java.util.*;

public class Message {
    ArrayList<ArrayList<Integer>> clock;
    Object value;

    public Message(ArrayList<ArrayList<Integer>> clock, Object value) {
        this.clock = clock;
        this.value = value;
    }

}
