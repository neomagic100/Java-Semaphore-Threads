/*
 * Step 5 
 * SantaScenario class (main)
 * 
 * Author: Michael Bernhardt (edited from original code)
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// Step 5
public class SantaScenario {
	// Semaphores and their initial max permits
	public Semaphore elfSemaphore;
	public Semaphore reindeerSemaphore;
	public Semaphore elvesDoneSemaphore;
	public final int NUM_ELVES_TO_MOVE = 3;
	public final int EIGHT_REINDEER_BACK = 8;
	
	public Santa santa;
	public List<Elf> elves;
	public List<Reindeer> reindeers;
	public boolean isDecember;
	public boolean isDecember25;

	public static void main(String args[]) {
		SantaScenario scenario = new SantaScenario();
		scenario.isDecember = scenario.isDecember25 = false;
		scenario.elfSemaphore = new Semaphore(scenario.NUM_ELVES_TO_MOVE);
		scenario.reindeerSemaphore = new Semaphore(scenario.EIGHT_REINDEER_BACK);
		scenario.elvesDoneSemaphore = new Semaphore(Integer.MAX_VALUE);
		
		// create the participants
		// Santa
		scenario.santa = new Santa(scenario);
		Thread th = new Thread(scenario.santa);
		th.start();
		// The elves: in this case: 10
		scenario.elves = new ArrayList<>();
		for (int i = 0; i != 10; i++) {
			Elf elf = new Elf(i + 1, scenario);
			scenario.elves.add(elf);
			th = new Thread(elf);
			th.start();
		}

		// The reindeer: in this case: 9
		 scenario.reindeers = new ArrayList<>(); 
		 for (int i = 0; i != 9; i++) {
			 Reindeer reindeer = new Reindeer(i + 1, scenario);
			 scenario.reindeers.add(reindeer); th = new Thread(reindeer); 
			 th.start(); 
		 }
		 
		// now, start the passing of time
		for (int day = 1; day < 500; day++) {
			// wait a day
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// turn on December
			if (day > (365 - 31)) {
				scenario.isDecember = true;
			}
			
			// turn on December 25
			if (day == 359) {
				scenario.isDecember25 = true;
			}
			
			// End after Christmas - no need to keep looping
			if (day > 359) {
				break;
			}

			// print out the state:
			System.out.println("***********  Day " + day + " *************************");
			
			// Step 5: Note - Santa does not always report woken by elves, when he is awake. Sometimes he wakes up and then goes back to sleep
			//			      before report
			scenario.santa.report();

			for (Elf elf : scenario.elves) {
				elf.report();
			}
			
			for (Reindeer reindeer : scenario.reindeers) { reindeer.report(); }
		
		}
		
		System.out.println("\n===================================\nChristmas is over, Elves take 6 days off. Santa take 364 days off.");
	}

}
