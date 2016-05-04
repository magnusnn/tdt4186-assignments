/**
 * Created by Magnus on 01/04/16.
 */
public class CPU {

    // instaner
    private Queue cpuQueue;
    private Statistics statistics;
    private long maxCpuTime;
    private Gui gui;
    private Process activeProcess;
    private EventQueue eQueue;


    public CPU(Queue cpuQueue, Statistics statistics, long maxCpuTime, Gui gui, EventQueue eQueue) {
        this.cpuQueue = cpuQueue;
        this.statistics = statistics;
        this.maxCpuTime = maxCpuTime;
        this.gui = gui;
        this.eQueue = eQueue;

    }

    //legge til prosess i køen
    public void addToQueue(Process p, long clock) {
        this.cpuQueue.insert(p);
        p.arrivedAtCpuQueue(clock);

        if (this.activeProcess == null && this.cpuQueue.getQueueLength() == 1) {
            this.eQueue.insertEvent(new Event(Constants.SWITCH_PROCESS, clock));
        }

        this.statistics.cpuQueueInserts++;

    }


    //bruker Round Robin algorithm
    public void processNextProcess(long clock) {

        // sjekk om aktiv prosess
        if (this.activeProcess != null) {
            //sjekker om tid som gjennstår er større enn max cpu tid, legger den til i IO og bytter prosess
            if (this.activeProcess.getCpuTimeNeeded() > this.maxCpuTime && this.activeProcess.getTimeToNextIO() <= this.maxCpuTime) {
                this.activeProcess.updateCpuTimeNeeded(this.activeProcess.getTimeToNextIO());
                this.eQueue.insertEvent(new Event(Constants.IO_REQUEST, clock + this.activeProcess.getTimeToNextIO()));
                this.eQueue.insertEvent(new Event(Constants.SWITCH_PROCESS, clock + this.activeProcess.getTimeToNextIO() + 1));
            }

            //gjennomfør prosessen dersom den kan gjennomføres innen max cpu tid.
            else if (this.activeProcess.getCpuTimeNeeded() <= maxCpuTime) {
                this.eQueue.insertEvent(new Event(Constants.END_PROCESS, clock + this.activeProcess.getCpuTimeNeeded()));
                this.eQueue.insertEvent(new Event(Constants.SWITCH_PROCESS, clock + this.activeProcess.getCpuTimeNeeded() + 1));

                activeProcess.updateCpuTimeNeeded(maxCpuTime);

                this.statistics.timeSpentCPU += this.activeProcess.getCpuTimeNeeded();
            }

            //hvis tiden ikke strekker til, switch til neste prosess
            else if (this.activeProcess.getCpuTimeNeeded() > this.maxCpuTime) {
                activeProcess.updateCpuTimeNeeded(maxCpuTime);

                this.eQueue.insertEvent(new Event(Constants.SWITCH_PROCESS, clock + maxCpuTime));
                this.statistics.nofProcessSwitches++;
                this.statistics.timeSpentCPU += this.maxCpuTime;
            }


        }

    }

    // metoder for å bytte prosess
    public void switchProcess(long clock) {

        if (this.activeProcess != null) {
            this.cpuQueue.insert(this.activeProcess);
        }


        if (!cpuQueue.isEmpty()) {
            if (cpuQueue.getNext() instanceof Process) {
                this.activeProcess = (Process) cpuQueue.getNext();
                this.cpuQueue.removeNext();
                this.activeProcess.leftCpuQueue(clock);
                this.gui.setCpuActive(activeProcess);
            }
        }

    }


    //metode for å avslutte prosessen
    public Process endProcess() {
        Process oldProcess = null;
        if (activeProcess != null) {
            oldProcess = activeProcess;
            activeProcess = null;
        }

        this.gui.setCpuActive(null);
        this.statistics.nofCompletedProcesses++;
        this.statistics.totalTimeInSystem += oldProcess.calcTotalTimeInSystem();
        return oldProcess;
    }


    public Process getProcessforIO() {
        Process ioProcess = this.activeProcess;
        this.activeProcess = null;
        this.gui.setCpuActive(null);
        return ioProcess;
    }

    public void timePassed(long timePassed) {
        this.statistics.cpuQueueLengthTime+= this.cpuQueue.getQueueLength()*timePassed;

        if (this.cpuQueue.getQueueLength() > this.statistics.cpuQueueLargestLength) {
            this.statistics.cpuQueueLargestLength = this.cpuQueue.getQueueLength();
        }
    }
}
