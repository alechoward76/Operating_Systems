package nachos.threads;

import nachos.ag.BoatGrader;

public class Boat {

	static Lock O;

	static Condition adultsWaitingO;
	static Condition childrenWaitingO;

	static Condition childrenWaitingM;
	static Condition CWaiting;

	static String island;

	static Integer numCWaiting;
	static Integer numPass;

	static Integer numAO;
	static Integer numCO;

	static Semaphore done;

	static BoatGrader bg;

	public static void selfTest() {
		BoatGrader b = new BoatGrader();

		System.out.println("\n ***Testing Boats with only 2 children***");
		begin(0, 2, b);

		System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
		begin(1, 2, b);

		System.out.println("\n ***Testing Boats with 3 children, 3 adults***");
		begin(3, 3, b);

	}

	public static void begin(int adults, int children, BoatGrader b) {
		// Store the externally generated autograder in a class
		// variable to be accessible by children.
		bg = b;

		// Instantiate global variables here

		O = new Lock();

		adultsWaitingO = new Condition(O);
		childrenWaitingO = new Condition(O);

		childrenWaitingM = new Condition(O);
		CWaiting = new Condition(O);

		island = "Oahu";
		// keep track of children on each island
		numAO = adults;
		numCO = children;
		done = new Semaphore(0);
		numCWaiting = 0;
		numPass = 0;

		// Create threads here. See section 3.4 of the Nachos for Java
		// Walkthrough linked from the projects page.

		/*
		 * Runnable r = new Runnable() { public void run() { SampleItinerary(); } };
		 * KThread t = new KThread(r); t.setName("Sample Boat Thread"); t.fork();
		 */

		for (int j = 0; j < children; j++) {
			Runnable r = new Runnable() {
				public void run() {
					ChildItinerary();
				}
			};
			KThread t = new KThread(r);
			t.setName("Child Boat Thread " + j);
			t.fork();
		}

		//////////////////////////////////////////////
		for (int i = 0; i < adults; i++) {
			Runnable r = new Runnable() {
				public void run() {
					AdultItinerary();
				}
			};
			KThread t = new KThread(r);
			t.setName("Adult Boat Thread " + i);
			t.fork();

		}
		/////////////////////////////////////

		done.P();

	}

	static void AdultItinerary() {
		/*
		 * This is where you should put your solutions. Make calls to the BoatGrader to
		 * show that it is synchronized. For example: bg.AdultRowToMolokai(); indicates
		 * that an adult has rowed the boat across to Molokai
		 */

		O.acquire();
		while (numCO > 1 || island != "Oahu") {
			adultsWaitingO.sleep();
		}
		island = "Molokai";
		numAO--;

		bg.AdultRowToMolokai();

		childrenWaitingM.wake();

		O.release();

	}// end AdultItinerary

	static void ChildItinerary() {

		while (numCO > 1 || numAO >= 1) {
			O.acquire();

			if (numCO == 1) {
				adultsWaitingO.wake();
			} // end if

			while (numCWaiting > 1 || island != "Oahu") {
				childrenWaitingO.sleep();
			} // end while
			if (numCWaiting == 0) {
				numCWaiting++;
				childrenWaitingO.wake();
				CWaiting.sleep();
				bg.ChildRideToMolokai();
				numCO--;

				CWaiting.wake();
			} else {
				numCWaiting++;
				CWaiting.wake();
				bg.ChildRowToMolokai();
				numCO--;
				CWaiting.sleep();
			}

			island = "Molokai";

			numCWaiting--;
			numPass++;

			if (numPass == 1) {
				childrenWaitingM.sleep();
			}
			numPass = 0;// if decrement while already 0; error

			bg.ChildRowToOahu();
			island = "Oahu";
			numCO++;

			O.release();
		} // end while

		numCO--;
		bg.ChildRowToMolokai();

		done.V();

	}// end ChildItinerary

}
