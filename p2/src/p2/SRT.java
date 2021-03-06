package p2;

import java.util.*;

/**
 * Shortest remaining time (SRT) is a preemptive scheduling algorithm that has a list
 * of processes that are ready and running. If a job arrives and has a shorter
 * time to complete than the remaining time on the current job, it immediately preempts
 * the CPU.
 */
public class SRT {
	/*** Instance variables ***/
    private final static int MAX_QUANTA_RUN_TIME = 100;
    private int completed;					// number of completed processes
    private Queue<Process> queue;
    private ArrayList<Process> processes;
    private ArrayList<Process> ready;
    // statistics for averages
    private float turnaroundTime;
    private float waitingTime;
    private float responseTime;
    /***********************/
    
    /**
     * Takes a list of processes, sorts by expected runtime, and adds them to process queue
     */
    public SRT(ArrayList<Process> processes) {
        this.processes = processes;
        // sort by expected runtime
        Collections.sort(this.processes, new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {
                if (p1.getArrivalTime() < p2.getArrivalTime()) {
                    return -1;
                }
                else if (p1.getArrivalTime() > p2.getArrivalTime()) {
                    return 0;
                }
                else {
                    if (p1.getExpectedRunTime() > p2.getExpectedRunTime()) {
                        return 0;
                    }
                    return -1;
                }
            }
        });
        
        // add all the processes to process queue
        queue = new LinkedList<Process>();
        for (Process p : this.processes) {
            queue.add(p);
        }
    }
   
    
    /**
     * Returns a process from the ready queue with the lowest burst time
     */
    public Process getProcessRQ() {
        Process current = null;
        int index = 0;
        for (int i = 0; i < ready.size(); i++) {
            if (current == null) {
                current = ready.get(i);
                index = i;
            } else if (current.getBurstTime() > ready.get(i).getBurstTime()) {
                current = ready.get(i);
                index = i;
            }
        }
        ready.remove(index);
        return current;
    }
    public String printProcessesInfo(Process currProcess) {
        return "\nProcess " + currProcess.getProcessNumber() + " \nArrival Time of this process is: "
                + currProcess.getArrivalTime() + " \nExpected Run Time is: " + currProcess.getExpectedRunTime()
                + " \nPriority " + currProcess.getPriority() + "\n";
    }

    /**
     * Run the SRT algorithm and output statistics
     */
    public void run() {
        Process current = null;
        ready = new ArrayList<Process>();
        String processHistory = "";
        // expected values
        for (int i = 0; i < processes.size(); i++) {
            System.out.println("Process #" + processes.get(i).getProcessNumber());
            System.out.println("Priority: " + processes.get(i).getPriority() + "\n");
            System.out.println("Arrival Time: " + processes.get(i).getArrivalTime());
            System.out.println("Expected Runtime: " + processes.get(i).getExpectedRunTime());
        }
        // processing system
        for (int j = 0; j <= 100; j++) {
            System.out.println("Run: " + j);
            // checks for empty queue and then adds processes to queue based on burst times
            if (!queue.isEmpty()) {
            	// if there's currently no running processes
                if (current == null & queue.peek().getArrivalTime() <= j) {
                    current = queue.poll();
                    current.setStartTime(j);
                }
                // if there is a currently running process, but the one in the queue has a
                // shorter burst time -- then replace it as the current
                else if ((queue.peek().getArrivalTime() <= j)) {
                    if (current.getBurstTime() > queue.peek().getBurstTime()) {
                        ready.add(current);
                        current = queue.poll();
                        current.setStartTime(j);
                    }
                    else if (current.getBurstTime() <= queue.peek().getBurstTime()) {
                        ready.add(queue.poll());
                    }
                }
            }
            // if there is no currently running process
            if (current == null) {
                processHistory = processHistory + "NULL ";
            }
            else {
                processHistory = processHistory + current.getProcessNumber() + " ";
            }
            // if there is a current process, decrement process burst time
            if (current != null) {
                System.out.println("Process #" + current.getProcessNumber());
                current.decBurstTime();
                
                // gather process information after it is finished (when burst time = 0)
                if (current.getBurstTime() == 0) {
                    this.turnaroundTime += (j + 1) - current.getArrivalTime();
                    this.waitingTime += ((j + 1) - current.getArrivalTime()) - (current.getExpectedRunTime());               
                    this.responseTime += current.getStartTime() - current.getArrivalTime();
                    this.completed++;
                    // if ready queue has 0, there are no current processes
                    if (ready.size() == 0) {
                        current = null;
                        
                    }
                    else {
                    	// get process from ready queue and set start time
                        current = getProcessRQ();
                        if(current.getBurstTime() == current.getExpectedRunTime()) {
                            current.setStartTime(j);
                        }
                    }
                }
            }
        }	// end of run()

        System.out.println(processHistory);
        System.out.println("\nAverage turnaround time: " + this.turnaroundTime / this.completed);
        System.out.println("Average wait time: " + this.waitingTime / this.completed);
        System.out.println("Average response time: " + this.responseTime / this.completed);
    }

} // end of class SRT