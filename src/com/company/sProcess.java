package com.company;

public class sProcess {
    public int cputime;
    public int ioblocking;
    public int cpudone;
    public int ionext;
    public int numblocked;
    public int number;
    public int preemted = 0;
    public int time = -1;

    public sProcess(int cputime, int ioblocking, int cpudone, int ionext, int numblocked, int number) {
        this.cputime = cputime;
        this.ioblocking = ioblocking;
        this.cpudone = cpudone;
        this.ionext = ionext;
        this.numblocked = numblocked;
        this.number = number;
    }
}
