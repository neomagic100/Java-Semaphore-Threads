/*
 * Step 5 
 * Reindeer class
 * 
 * Author: Michael Bernhardt (edited from original code)
 */

import java.util.Random;

public class Reindeer implements Runnable {

	public enum ReindeerState {AT_BEACH, AT_WARMING_SHED, AT_THE_SLEIGH};
	private ReindeerState state;
	private SantaScenario scenario;
	private Random rand = new Random();
	private boolean isAcquired;  // bool value of whether reindeer is acquired in semaphore

	private int number;
	
	/**
	 * Constructor
	 * @param number		integer representing the reindeer
	 * @param scenario		SantaScenario where Reindeer is being run
	 */
	public Reindeer(int number, SantaScenario scenario) {
		this.number = number;
		this.scenario = scenario;
		this.state = ReindeerState.AT_BEACH;
		this.isAcquired = false;
	}
	
	/**
	 * Run the Reindeer thread
	 */
	@Override
	public void run() {
		
		while(true) {
			// wait a day
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// see what we need to do:
			switch(state) {
			case AT_BEACH: { // if it is December, the reindeer might think about returning from the beach
				if (scenario.isDecember) {
					if (rand.nextDouble() < 0.1) {
						state = ReindeerState.AT_WARMING_SHED;
					}
					
					// If it is Dec 25, go to shed (assuming non-leap year)
					if (scenario.isDecember25) {
						state = ReindeerState.AT_WARMING_SHED;
					}
				}
				break;			
			}
			
			case AT_WARMING_SHED:
				// If semaphore has no permits left, and the last reindeer comes, wake up Santa
				if (scenario.reindeerSemaphore.availablePermits() == 0 && this.isAcquired == false) {
					scenario.santa.setState(Santa.SantaState.WOKEN_UP_BY_REINDEER);
				}
				
				try {
					scenario.reindeerSemaphore.acquire();
					this.isAcquired = true;
				}
				catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				
				break;
				
			case AT_THE_SLEIGH: 
				// Nothing else to do
				break;
			}
		}
	}
	
	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Reindeer " + number + " : " + state);
	}
	
	/**
	 * Set the state of the reindeer
	 * @param state			ReindeerState
	 */
	public void setState(ReindeerState state) {
		this.state = state;
	}
	
	/**
	 * Determine if a reindeer is at the sleigh
	 * @return				bool
	 */
	public boolean isAtSleigh() {
		return this.state == ReindeerState.AT_THE_SLEIGH;
	}
	
}
