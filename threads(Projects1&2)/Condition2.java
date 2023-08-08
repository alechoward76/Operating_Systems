package nachos.threads;

import nachos.machine.*;
import java.util.LinkedList;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see nachos.threads.Condition
 */
public class Condition2 {
	
	//Private Vars\\
	private Lock conditionLock;
	private LinkedList<KThread> threadQueue = new LinkedList<KThread>();//create linked list for EZ queue
	/**
	 * Allocate a new condition variable.
	 *
	 * @param conditionLock the lock associated with this condition variable. The
	 *                      current thread must hold this lock whenever it uses
	 *                      <tt>sleep()</tt>, <tt>wake()</tt>, or
	 *                      <tt>wakeAll()</tt>.
	 */
	public Condition2(Lock conditionLock) {
		this.conditionLock = conditionLock;
	}

	/**
	 * Atomically release the associated lock and go to sleep on this condition
	 * variable until another thread wakes it using <tt>wake()</tt>. The current
	 * thread must hold the associated lock. The thread will automatically reacquire
	 * the lock before <tt>sleep()</tt> returns.
	 */
	public void sleep() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		conditionLock.release();

		boolean intStatus = Machine.interrupt().disable();

		KThread currThread = KThread.currentThread();

		threadQueue.add(currThread);//add current thread to queue

		KThread.sleep();//sleep it
		Machine.interrupt().restore(intStatus);

		conditionLock.acquire();
	}

	/**
	 * Wake up at most one thread sleeping on this condition variable. The current
	 * thread must hold the associated lock.
	 */
	public void wake() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		boolean intStatus = Machine.interrupt().disable();
		KThread nxtThread = threadQueue.removeFirst();//remove front of queue and assign to var
		nxtThread.ready();//ready that thread

		Machine.interrupt().restore(intStatus);

	}

	/**
	 * Wake up all threads sleeping on this condition variable. The current thread
	 * must hold the associated lock.
	 */
	public void wakeAll() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		while (!threadQueue.isEmpty()) {//for the entire threadQueue, wake each thread
			wake();

		}
	}
	
	
	
}
