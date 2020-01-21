package mensamodell;

import java.util.ArrayList;
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

	public StudentChaotic(ContinuousSpace s, Context c, int num, SharedStuff sharedstuff) {
		super(s, c, num, sharedstuff);
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
		
		List<Theke> nonvisited_theken = new ArrayList<Theke>();
		for (Theke t : theken) 
			if (!visitedBars.contains(t)) 
				nonvisited_theken.add(t);
		
		if (nonvisited_theken.isEmpty()) {
			return null;
			// gehe zur theke
		} 
		// suche zufaellig eine Theke aus der Liste
		int index = nonvisited_theken.size();
		NdPoint location = space.getLocation(nonvisited_theken.get(RandomHelper.nextIntFromTo(0, index-1)));
		double[] temp = space.getDisplacement(space.getLocation(this), location);
		return new Vector2d(temp[0], temp[1]);
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
				// Wähle dein Essen. Du stehst vor einer Theke.
				//System.out.println("Essenswahl!");
			}
		}
	} 
	

}
