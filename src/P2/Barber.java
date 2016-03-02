package P2;
/**
 * This class implements the barber's part of the
 * Barbershop thread synchronization example.
 */
public class Barber extends Thread{
	/**
	 * Creates a new barber.
	 * @param queue		The customer queue.
	 * @param gui		The GUI.
	 * @param pos		The position of this barber's chair
	 */
	private CustomerQueue customerQueue;
	private Gui gui;
	private int barberId;
	private boolean running;
	
	public Barber(CustomerQueue customerQueue, Gui gui, int barberId) {
		super();
		this.customerQueue = customerQueue;
		this.gui = gui;
		this.barberId = barberId;
	}

	/**
	 * Starts the barber running as a separate thread.
	 */
	public void startThread() {
		this.running = true;
		this.start();
	}

	/**
	 * Stops the barber thread.
	 */
	public void stopThread() {
		this.running = false;
	}


	@Override
	public void run(){
		while (running){
			// barber is sleeping
			this.gui.barberIsSleeping(barberId);
			try {
				this.sleep(Globals.barberSleep);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
			this.gui.barberIsAwake(barberId);

			// as soon as the barber wakes up, he tries to get a new customer from the waitingroom
			this.customerQueue.getNextCustomer(barberId);

			try {
				this.sleep(Globals.barberWork);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
			// finished the work and going to sleep.
			this.gui.emptyBarberChair(barberId);
		}
	}



}

