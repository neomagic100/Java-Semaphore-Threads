/*
 * Step 5 
 * Elf class
 * 
 * Author: Michael Bernhardt (edited from original code)
 */

import java.util.Random;


public class Elf implements Runnable {

	enum ElfState {
		WORKING, TROUBLE, AT_SANTAS_DOOR
	};

	private ElfState state;
	private int number;
	private Random rand = new Random();
	private SantaScenario scenario;
	private boolean isAcquired;		// bool value of whether elf is acquired in semaphore

	/**
	 * Constructor
	 * @param number		integer representing the elf
	 * @param scenario		SantaScenario where Elf is being run
	 */
	public Elf(int number, SantaScenario scenario) {
		this.number = number;
		this.scenario = scenario;
		this.state = ElfState.WORKING;
		this.isAcquired = false;
	}
	

	/**
	 * Set an Elf state
	 * @param state
	 */
	public void setState(ElfState state) {
		this.state = state;
	}
	
	/**
	 * Run the Elf thread
	 */
	@Override
	public void run() {
		while (true) {
			// wait a day
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// If all reindeer at sleigh, stop waking Santa
			if (allDeerAtSleigh() && !isAcquired) {
				try {
					scenario.elvesDoneSemaphore.acquire();
					this.isAcquired = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// State switch
			switch (state) {
			case WORKING: {
				// at each day, there is a 1% chance that an elf runs into
				// trouble.
				if (rand.nextDouble() < 0.01) {
					state = ElfState.TROUBLE;
				}
				break;
			}
			
			case TROUBLE:
				// from Step 4 - If elf in trouble, try to acquire in semaphores
				if (!this.isAcquired) {
					try {
						scenario.elfSemaphore.acquire();
						this.isAcquired = true;
					}
					catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
				
				// If no more permits in semaphore, 3 elves in trouble, move to door
				if (scenario.elfSemaphore.availablePermits() == 0) 
					moveElves(Elf.ElfState.TROUBLE, Elf.ElfState.AT_SANTAS_DOOR, true);
				
				break;
				
			case AT_SANTAS_DOOR:
				// Wake Santa
				if (scenario.santa.getState() != Santa.SantaState.WOKEN_UP_BY_ELVES) {
					scenario.santa.setState(Santa.SantaState.WOKEN_UP_BY_ELVES);
				}
				
				break;
			}
		}
	}


	/**
	 * Move elves from one state to another
	 * @param src 						ElfState moving from
	 * @param dest						ElfState moving to
	 * @param needToBeInSemaphore		bool of whether the elves being moved need to be in a semaphore
	 */
	private void moveElves(ElfState src, ElfState dest, boolean needToBeInSemaphore) {
		if (needToBeInSemaphore) {
			for (Elf elf : scenario.elves) {
				// If the elf needs to be in a semaphore, make sure it is acquired
				if (elf.getState() == src && elf.isAcquired)
					elf.setState(dest);
			}
		}
		else {
			for (Elf elf : scenario.elves) {
				if (elf.getState() == src)
					elf.setState(dest);
			}
		}
	}

	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Elf " + number + " : " + state);
	}
	
	/**
	 * Set whether an elf thread is acquired in a semaphore
	 * @param isAcquired   			bool
	 */
	public void setAcquired(boolean isAcquired) {
		this.isAcquired = isAcquired;
	}
	
	/**
	 * Determine if an elf thread is acquired in a semaphore
	 * @return						bool
	 */
	public boolean getAcquired() {
		return isAcquired;
	}

	/**
	 * Determine the state of an elf
	 * @return						ElfState
	 */
	public ElfState getState() {
		return state;
	}
	
	/**
	 * Determine if all the reindeer are at the sleigh
	 * @return						bool
	 */
	private boolean allDeerAtSleigh() {
		for (Reindeer deer: scenario.reindeers) {
			if (!deer.isAtSleigh()) return false;
		}
		
		return true;
	}
}
