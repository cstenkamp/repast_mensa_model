package mensamodell;

import javax.vecmath.Vector2d;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.context.Context;

public class StudentGoalOriented extends Student {

	public StudentGoalOriented(ContinuousSpace s, Context c) {
		super(s, c);
		this.vision = 200; // Sichtweite
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
			if (movement != null && !(movement[0] == 0 && movement[1] == 0)) {
				// Du bist auf dem Weg.
				velocity.setX(movement[0]);
				velocity.setY(movement[1]);	
				System.out.println("Move " + this);
			} else if (movement == null) {
				// gehe zur Kasse
				movement = to_kasse();
				velocity.setX(movement[0]);
				velocity.setY(movement[1]);	
				// entferne den Studenten
				//context.remove(this);
				System.out.println("Kasse " + this);
			} else {
				// Wähle dein Essen. Du stehst vor einer Theke.
				System.out.println("Essen " + this);
			}
		}
	} 

}
