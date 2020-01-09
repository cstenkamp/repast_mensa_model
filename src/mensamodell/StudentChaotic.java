package mensamodell;

import javax.vecmath.Vector2d;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.context.Context;

public class StudentChaotic extends Student {

	public StudentChaotic(ContinuousSpace s, Context c) {
		super(s, c);
		this.vision = 100; // Sichweite
	}
	
	public double[] move_chaotically() {
		//wenn in der naehe einer theke, gehe dort hin...
		double[] distXY = {0.2*RandomHelper.nextDoubleFromTo(-20, 20)*walking_speed + 0.8*velocity.x, 0.2*RandomHelper.nextDoubleFromTo(-20, 20)*walking_speed + 0.8*velocity.y};
		return distXY;
	}
	
	
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		
		Vector2d avoidance = avoid_others();
		if (avoidance != null) {
			velocity.setX(-avoidance.x);
			velocity.setY(-avoidance.y);
		} else {
			double[] movement = move_chaotically();
			velocity.setX(movement[0]);
			velocity.setY(movement[1]);		
		}
	} 
	

}
