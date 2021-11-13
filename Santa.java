/*
 * Step 5 
 * Reindeer class
 * 
 * Author: Michael Bernhardt (edited from original code)
 */

public class Santa implements Runnable {

	enum SantaState {
		SLEEPING, READY_FOR_CHRISTMAS, WOKEN_UP_BY_ELVES, WOKEN_UP_BY_REINDEER
	};

	private SantaState state;
	private SantaScenario scenario;
	
	/**
	 * Constructor
	 * @param scenario  SantaScenario where Santa thread is being run
	 */
	public Santa(SantaScenario scenario) {
		this.scenario = scenario;
		this.state = SantaState.SLEEPING;
	}
	
	/**
	 * Run the thread
	 */
	@Override
	public void run() {
		while (true) {
			// wait a day...
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			switch (state) {
			case SLEEPING: // if sleeping, continue to sleep
				break;
				
			case WOKEN_UP_BY_ELVES:
				// from Step 4 - Fix 3 elves in semaphore
				fixElvesAtDoor();

				// Elves fixed, go back to sleep
				scenario.santa.setState(Santa.SantaState.SLEEPING);
				
				// Release elf threads in semaphore
				scenario.elfSemaphore.release(3);
				break;

			case WOKEN_UP_BY_REINDEER:
				assembleReindeer();
				
				// Ready
				state = SantaState.READY_FOR_CHRISTMAS;
				break;
				
			case READY_FOR_CHRISTMAS: // nothing more to be done
				break;
			}
		}
	}

	/**
	 * When the elves wake Santa at his door, send them back to work
	 */
	private void fixElvesAtDoor() {
		for (Elf elf : scenario.elves) {
			if (elf.getState() == Elf.ElfState.AT_SANTAS_DOOR && elf.getAcquired()) {
				elf.setState(Elf.ElfState.WORKING);
				elf.setAcquired(false);
			}
		}		
	}

	/**
	 * Report about my state
	 */
	public void report() {
		System.out.println("Santa : " + state);
	}
	
	/**
	 * Assemble reindeer to sleigh
	 */
	private void assembleReindeer() {
		// At this point, all reindeer should be at the warming shed
		
		// Release reindeer in semaphore
		scenario.reindeerSemaphore.release(scenario.EIGHT_REINDEER_BACK);
		
		// Set their states to at the sleigh
		for (Reindeer deer : scenario.reindeers) {
			deer.setState(Reindeer.ReindeerState.AT_THE_SLEIGH);
		}
	}

	// Santa getters and setters
	public SantaState getState() {
		return state;
	}

	public void setState(SantaState state) {
		this.state = state;
	}

}
