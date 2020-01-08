package mensamodell;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;

public class StudentChaotic extends Student {

	public StudentChaotic(ContinuousSpace s) {
		super(s);
	}
	
	
	/**
	 * Methode wird jede Runde ausgefuehrt. 
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		velocity.x = 0.2*RandomHelper.nextDoubleFromTo(-10, 10)*walking_speed + 0.8*velocity.x; 
		velocity.y = 0.2*RandomHelper.nextDoubleFromTo(-10, 10)*walking_speed + 0.8*velocity.y; 
	} 

}
