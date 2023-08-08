package nachos.threads;

import java.util.*;

import nachos.machine.*;
import java.util.LinkedList;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 *
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */
	public Alarm() {
		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current thread
	 * to yield, forcing a context switch if there is another thread that should be
	 * run.
	 */

	LinkedList<Long> lst1 = new LinkedList<Long>();// Create ArrayLists to store wakeTimes
	LinkedList<KThread> lst2 = new LinkedList<KThread>();// Create ArrayList to store Threads @same index of wakeTimes

	public void timerInterrupt() {

		boolean intStatus = Machine.interrupt().disable();// 

		for (int i = 0; i < lst1.size(); i++) {// iterate through list of waketimes
			if (lst1 !=null && lst2 != null) {
				if (lst1.get(i) < Machine.timer().getTime()) {// if the waketime is past due, ready the related thread*/

					// KThread nxtThread = lst2.get(i);
					lst2.get(i).ready();
					lst1.remove(i);
					lst2.remove(i);

					i++;

				} // end if
			}
			

		} // end for

		Machine.interrupt().restore(intStatus);//
		// KThread.currentThread().yield();

	}// end timer interrupt

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up in
	 * the timer interrupt handler. The thread must be woken up (placed in the
	 * scheduler ready set) during the first timer interrupt where
	 *
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 *
	 * @param x the minimum number of clock ticks to wait.
	 *
	 * @see nachos.machine.Timer#getTime()
	 */

	public void waitUntil(long x) {

		Long wakeTime = Machine.timer().getTime() + x;
		// 

		lst1.add(wakeTime);// ASSERTION FAILURE
		// adding waketime and threads to respective lists

		lst2.add(KThread.currentThread());

		boolean intStatus = Machine.interrupt().disable();

		KThread.sleep();
		Machine.interrupt().restore(intStatus);
		///

	}

}
