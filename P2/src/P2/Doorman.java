package P2;

/**
 * This class implements the doorman's part of the
 * Barbershop thread synchronization example.
 */
public class Doorman extends Thread {
	/**
	 * Creates a new doorman.
	 * @param queue		The customer queue.
	 * @param gui		A reference to the GUI interface.
	 */
	private CustomerQueue customerQueue;
	private Gui gui;
	private boolean active;
	
	public Doorman(CustomerQueue queue, Gui gui) { 
		super();
		this.customerQueue = queue;
		this.gui = gui;
	}

	/**
	 * Starts the doorman running as a separate thread.
	 */
	public void startThread() {
		this.active = true;
		this.start();
	}

	/**
	 * Stops the doorman thread.
	 */
	public void stopThread() {
		this.active = false;
	}

	@Override
	public void run() {
		while (this.active) {
			try {
				// doorman is sleeping
				this.gui.println("Doorman is sleeping");
				this.sleep(Globals.doormanSleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// doorman pulles in a customer and add it to the waitingroom if allowed.
			this.customerQueue.addCustomer();
		}
		
//		super.run();
	}
}
