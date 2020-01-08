package mensamodell;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;

public class StudentGoalOriented extends Student {

	public StudentGoalOriented(ContinuousSpace s) {
		super(s);
	}
	
	
	/**
	 * Methode wird jede Runde ausgefuehrt. 
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		to_next_ausgabe();		
	} 

}
