package nachos.threads;


import nachos.machine.*;

//import java.util.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>, and multiple
 * threads can be waiting to <i>listen</i>. But there should never be a time
 * when both a speaker and a listener are waiting, because the two threads can
 * be paired off at this point.
 */
public class Communicator {
	// Private Variables\\
	private Lock myLock;
	
	private Condition spkIQ;
	private Condition lstnIQ;
	private Condition spkOut;
	private Condition lstnIn;

	private boolean spkIdle;
	private boolean lstnIdle;
	
	private boolean rtn;
	private Integer buffer;

	/**
	 * Allocate a new communicator.
	 */
	public Communicator() {

		// Initialize lock, conditions, and booleans\\
		myLock = new Lock();
		
		spkIQ = new Condition(myLock);
		lstnIQ = new Condition(myLock);
		spkOut = new Condition(myLock);
		lstnIn = new Condition(myLock);

		lstnIdle = false;
	
		spkIdle = false;
		rtn = false;

	}

	/**
	 * Wait for a thread to listen through this communicator, and then transfer
	 * <i>word</i> to the listener.
	 *
	 * <p>
	 * Does not return until this thread is paired up with a listening thread.
	 * Exactly one listener should receive <i>word</i>.
	 *
	 * @param word the integer to transfer.
	 */
	public void speak(int word) {

		myLock.acquire();// acquire lock

		while (spkIdle == true) { // While There is an idle speaker, putting it into the idle speaker queue
			spkIQ.sleep();
		}
		spkIdle = true;// Tells program that there is a speaker idle in the queue

		buffer = word; // setting buffer to given val

		while (!lstnIdle || !rtn) { // while there is no idle listener OR no return val, wake up listener and put
									// sending speaker to sleep
			lstnIn.wake();
			spkOut.sleep();
		}

		
		//wakes waiting speaker & listener
		spkIQ.wake();
		lstnIQ.wake();
		
		//Resets the booleans to default
		lstnIdle = false;
		spkIdle = false;
		rtn = false;

		

		myLock.release();// release lock

	}

	/**
	 * Wait for a thread to speak through this communicator, and then return the
	 * <i>word</i> that thread passed to <tt>speak()</tt>.
	 *
	 * @return the integer transferred.
	 */
	public int listen() {

		myLock.acquire();// acquire lock
		while (lstnIdle == true) { // while there is an idle listener, put it into a queue
			lstnIQ.sleep();

		}

		lstnIdle = true;// Alerts program to waiting listener in queue

		while (!spkIdle) {//while there is no idle speaker, hold onto the listener
			lstnIn.sleep();
		}

		spkOut.wake();// wake outgoing speaker
		rtn = true;// return is set to true

		myLock.release();// release lock

		return buffer;// return the integer value of the buffer
	}
}
