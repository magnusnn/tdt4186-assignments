package P2;

import java.util.ArrayList;

/**
 * This class implements a queue of customers as a circular buffer.
 */
public class CustomerQueue {
	/**
	 * Creates a new customer queue.
	 * @param queueLength	The maximum length of the queue.
	 * @param gui			A reference to the GUI interface.
	 */
	private int queueLength, start, end;
	private Gui gui;
	private Customer[] customerQueue;
	
    public CustomerQueue(int queueLength, Gui gui) {
    	super();
    	this.customerQueue = new Customer[queueLength];
    	this.start = 0;
    	this.end = 0;
    	this.queueLength = queueLength;
		this.gui = gui;

	}

	// just to check if the queue is full
    private boolean queue_full(){
    	if (start == end && customerQueue[start] != null){return true;}
		else{return false;}
    }
	// just to check if the queue is empty
    private boolean queue_empty(){
    	if(start == end && customerQueue[start] == null){return true;}
    	else{return false;}
    }

	// for the doorman to add a customer and its synchronized so that the barber
	// isn't trying to get a customer at the same time and mess up the queue
    public synchronized void addCustomer(){
    	while (queue_full()) {
			this.gui.println("Doorman is waiting for free chairs...");
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    	this.gui.println("A spot has opened in the waiting room");
    	this.customerQueue[end] = new Customer();
		this.gui.fillLoungeChair(end, customerQueue[end]);
    	end ++;
		if (end == queueLength) {
			end = 0;
		}
    	if (end - start == 1) {
			// notify in case of any waiting threads
			notifyAll();
		}
    	
    }

	// collects the next customer from the waiting room so the barber #x can start working
    public synchronized Customer getNextCustomer(int barberId){
		while (queue_empty()){
			try {
				this.gui.println("Barber #" + barberId + " is waiting for customer...");
				//waiting for notify
				this.wait();
			}
			catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		this.gui.println("Barber #" + barberId + " started on a new customer");

		if (queue_full()){
//			notify all that the queue is full
			notifyAll();

		}

		// the barber starts working on a customer
		Customer customer = customerQueue[start];
		this.gui.emptyLoungeChair(start);
		this.gui.fillBarberChair(barberId, customer);
		this.customerQueue[start] = null;
		this.start++;
		if (start == queueLength){start = 0;}
		return customer;
	}
    
}
