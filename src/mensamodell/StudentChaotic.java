package mensamodell;

import java.util.List;

import javax.vecmath.Vector2d;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.util.collections.FilteredIterator;
import repast.simphony.context.Context;

public class StudentChaotic extends Student {

	public StudentChaotic(ContinuousSpace s, Context c, List<Kasse> kassen, List<Theke> theken) {
		super(s, c, kassen, theken);
		this.vision = 30; // Sichtweite
	}
	
	public Vector2d move_chaotically() {
		/*
		 * Return Values:
		 * distXY == null --> gehe zur Kasse
		 * distXY == (0,0)--> W�hle dein Essen. Du stehst vor einer Theke.
		 * distXY == (X,Y)--> Du bist auf dem Weg.
		 */
		// Falls der student vor einer Theke steht
		if (at_bar()) {
			return new Vector2d(0,0);
		}
		
		//waehle zufaellig eine Theke NOCH NICHT GANZ ZUFAELLIG
		double[] distXY = new double[] {0,0};
		NdPoint lastPos = space.getLocation(this);
		ContinuousWithin barInVision = new ContinuousWithin(space, this, vision);
		
		for (Object o : barInVision.query()){			
			if (o instanceof Theke && !visitedBars.contains(o)){	
				distXY = space.getDisplacement(lastPos, space.getLocation(o));	
			}
		}
		return new Vector2d(distXY[0], distXY[1]);
	}
	
	
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		
		Vector2d avoidance = avoid_others();
		if (avoidance != null) {
			velocity.setX(-avoidance.x);
			velocity.setY(-avoidance.y);
		} else {
			Vector2d movement = move_chaotically();
			if (movement != null && !(movement.x == 0 && movement.y == 0)) {
				// Du bist auf dem Weg.
				velocity.setX(movement.x);
				velocity.setY(movement.y);	
			} else if (movement == null) {
				// gehe zur Kasse
				movement = to_kasse();
				if (movement != null) {
					velocity.setX(movement.x);
					velocity.setY(movement.y);
				}
			} else {
				// W�hle dein Essen. Du stehst vor einer Theke.
				//System.out.println("Essenswahl!");
			}
		}
	} 
	

}
