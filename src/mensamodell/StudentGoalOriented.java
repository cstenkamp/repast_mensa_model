package mensamodell;

import javax.vecmath.Vector2d;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.context.Context;

public class StudentGoalOriented extends Student {

	public StudentGoalOriented(ContinuousSpace s, Context c) {
		super(s, c);
	}
	
	
	/**
	 * Methode wird jede Runde ausgefuehrt. 
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		Vector2d avoidance = avoid_others();
		if (avoidance != null) {
			velocity.setX(-avoidance.x);
			velocity.setY(-avoidance.y);
		} else {
			double[] movement = to_next_ausgabe();
			velocity.setX(movement[0]);
			velocity.setY(movement[1]);		
		}
	} 

}
