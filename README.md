# Hierarchical-Vector-Clock

To simulate a computation, use the HVCTest.java file. There are JUnit test cases which simulate a computation. To create your own, pass the iniatlization function the number of processes and a hierarchical mapping (an ArrayList of Integer arrays). The number of rows in the mapping specify the number of levels in the hierarchy. Each number in a row specifies how many subgroups belong to the group at that level. For example, [2, 2, 2, 2] means the subgroups are paired up into 4 distinct groups.
