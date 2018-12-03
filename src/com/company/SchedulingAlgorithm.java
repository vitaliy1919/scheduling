package com.company;// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Vector;

public class SchedulingAlgorithm {

    public static Results Run(int runtime, int quantum, Vector<sProcess> processVector, Results result) {
        int i = 0;
        int comptime = 0;
        int currentProcess = 0;
        int previousProcess = 0;
        int size = processVector.size();
        int completed = 0;
        String resultsFile = "Summary-Processes";

        result.schedulingType = "Interactive (Preemptive)";
        result.schedulingName = "Round Robin";
        try {
            //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
            //OutputStream out = new FileOutputStream(resultsFile);
            LinkedList<sProcess> processes = new LinkedList<sProcess>(processVector);
            PriorityQueue<sProcess> blockedProcesses = new PriorityQueue<>((a, b)->Integer.compare(a.ionext, b.ionext));
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            while (comptime < runtime && (!processes.isEmpty() || !blockedProcesses.isEmpty())) {
                if (processes.isEmpty()) {
                    sProcess shortestBlocked = blockedProcesses.poll();
                    comptime = shortestBlocked.ionext;
                    shortestBlocked.ionext = 0;
                    processes.addLast(shortestBlocked);
                }
                int curRunningTime = 0;
                sProcess curProcess = processes.removeFirst();
                if (curProcess.time == -1)
                    curProcess.time = comptime;
                out.println("Process: " + curProcess.number + " registered... (" + curProcess.cputime + " " + curProcess.ioblocking + " " + curProcess.cpudone + ")");

                while ( comptime < runtime &&
                        curRunningTime < quantum
                                && curProcess.cpudone != curProcess.cputime
                                && curProcess.ioblocking != curProcess.ionext) {
                    curProcess.cpudone++;
                    curProcess.ionext++;
                    curRunningTime++;
                    comptime++;
                    if (!blockedProcesses.isEmpty()) {
                        if (blockedProcesses.peek().ionext == comptime) {
                            sProcess shortestBlocked = blockedProcesses.poll();
                            shortestBlocked.ionext = 0;
                            processes.addLast(shortestBlocked);
                        }
                    }
                }

                if (curProcess.cpudone == curProcess.cputime) {
                    // process finished
                    curProcess.time = comptime - curProcess.time;
                    out.println("Process: " + curProcess.number + " completed... (" + curProcess.cputime + " " + curProcess.ioblocking + " " + curProcess.cpudone + ")");
                    continue;
                }

                if (curProcess.ioblocking == curProcess.ionext) {
                    // io blocked
                    blockedProcesses.add(curProcess);
                    curProcess.ionext = comptime + curProcess.ioblocking / 2;
                    curProcess.numblocked++;
                    out.println("Process: " + curProcess.number + "  I/O blocked... (" + curProcess.cputime + " " + curProcess.ioblocking + " " + curProcess.cpudone + ")");
                    continue;
                }
                curProcess.preemted++;
                processes.addLast(curProcess);

            }
            Iterator<sProcess> unfinishedProcesses = processes.iterator();
            while (unfinishedProcesses.hasNext()) {
                sProcess process = unfinishedProcesses.next();
                process.time = comptime - process.time;
            }
//      sProcess process = (sProcess) processVector.elementAt(currentProcess);
//      out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + ")");
//      while (comptime < runtime) {
//        if (process.cpudone == process.cputime) {
//          completed++;
//          out.println("Process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + ")");
//          if (completed == size) {
//            result.compuTime = comptime;
//            out.close();
//            return result;
//          }
//          for (i = size - 1; i >= 0; i--) {
//            process = (sProcess) processVector.elementAt(i);
//            if (process.cpudone < process.cputime) {
//              currentProcess = i;
//            }
//          }
//          process = (sProcess) processVector.elementAt(currentProcess);
//          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + ")");
//        }
//        if (process.ioblocking == process.ionext) {
//          out.println("Process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + ")");
//          process.numblocked++;
//          process.ionext = 0;
//          previousProcess = currentProcess;
//          for (i = size - 1; i >= 0; i--) {
//            process = (sProcess) processVector.elementAt(i);
//            if (process.cpudone < process.cputime && previousProcess != i) {
//              currentProcess = i;
//            }
//          }
//          process = (sProcess) processVector.elementAt(currentProcess);
//          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + ")");
//        }
//        process.cpudone++;
//        if (process.ioblocking > 0) {
//          process.ionext++;
//        }
//        comptime++;
//      }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        result.compuTime = comptime;
        return result;
    }
}
