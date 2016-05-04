/**
 * Created by Magnus on 01/04/16.
 */
public class IO {

    private Queue ioQueue;
    private Statistics statistics;
    private EventQueue eQueue;
    private Gui gui;
    private Process activeProcess;


    public IO(Queue ioQueue, Statistics statistics, Gui gui, EventQueue eQueue) {
        // instansierer
        this.ioQueue = ioQueue;
        this.statistics = statistics;
        this.eQueue = eQueue;
        this.gui = gui;

    }

    // legg til i kø
    public void addToQueue(Process p, long clock) {
        //legge til prosessen i iokøen
        this.ioQueue.insert(p);
//        this.activeProcess.arrivedAtIoQueue(clock);

        // sjekker at aktiv prosess er lik null og at det neste elementet i iokøen er en instans av prosess
        if ((this.activeProcess == null) && (this.ioQueue.getNext() instanceof Process)) {

            // setter aktiv prosess til neste i køen
            this.activeProcess = (Process) this.ioQueue.removeNext();

            // aktiverer is i gui
            this.gui.setIoActive(this.activeProcess);


            performIO(clock);

//            if(this.ioQueue.getNext() instanceof Process) {
//            }
        }

        //holde styr på neste insert
        this.statistics.ioQueueInserts++;
    }

    public void performIO(long clock) {
        //sjekk at det faktisk finnes en aktiv prosess
        if (this.activeProcess != null) {
            //
            this.activeProcess.leftIoQueue(clock);
            this.eQueue.insertEvent(new Event(Constants.END_IO, clock + this.activeProcess.calcIOTime()));

        }
    }

    public Process endIoOp() {
        Process finishedIoProcess = this.activeProcess;
        this.activeProcess = null;

        if (this.ioQueue.getQueueLength() > 0) {
            if (this.ioQueue.getNext() instanceof Process) {
                this.activeProcess = (Process)this.ioQueue.removeNext();
            }
        }
        this.gui.setIoActive(activeProcess);
        this.statistics.nofProcessIO++;
        return finishedIoProcess;
    }

    public void timePassed(long timePassed) {
        this.statistics.ioQueueLengthTime += this.ioQueue.getQueueLength()*timePassed;

        if(this.ioQueue.getQueueLength() > this.statistics.ioQueueLargestLength) {
            this.statistics.ioQueueLargestLength = this.ioQueue.getQueueLength();
        }
    }

    public Process getActiveProcess() {
        return this.activeProcess;
    }


}
