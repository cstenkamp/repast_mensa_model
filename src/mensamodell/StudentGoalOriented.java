package mensamodell;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.util.collections.FilteredIterator;
import repast.simphony.context.Context;

public class StudentGoalOriented extends Student {

	public StudentGoalOriented(ContinuousSpace s, Context c, int num, List<Kasse> kassen, List<Theke> theken) {
		super(s, c, num, kassen, theken);
		this.vision = 300; // Sichtweite
	}
	
	// Sucht den kuerzesten Weg
		public Vector2d to_next_ausgabe() {
			
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
			
			if (nonvisited_theken.isEmpty())
				return null;
			
			Object[] clostesttheke = get_closest(nonvisited_theken);
			Vector2d distance = (Vector2d) clostesttheke[1];
			Theke k = (Theke) clostesttheke[0];
				return distance;
			
			
//			// Suche deinen Weg zur naechsten Theke
//			double[] distXY = null;			
//			NdPoint lastPos = space.getLocation(this);										// speichere die aktuelle Position
//			ContinuousWithin barInVision = new ContinuousWithin(space, this, vision);		// erzeugt eine Query mit allen Objekten im Sichtradius
//			double minBarDist = vision;					// kuerzester Abstand zu einer Bar
//			NdPoint closestBarPoint = new NdPoint();	// Punkt mit naechster Bar
//			Theke v = null; 							// zum speichern der besuchten Theke
//			
//			
//			for (Object o : barInVision.query()){			// Durchlaufe die Query des Sichtradius
//				if (o instanceof Theke && !visitedBars.contains(o)){	// falls das Theke und noch nicht besucht
//					Theke tempBar = (Theke) o;
//					NdPoint tempBarLoc = space.getLocation(tempBar);
//					double dist = space.getDistance(lastPos, tempBarLoc);			// Distanz zur Theke, falls minimum -> speichern
//					if (dist < minBarDist){
//						minBarDist = dist;
//						closestBarPoint = tempBarLoc;
//						v = tempBar;
//						distXY = space.getDisplacement(lastPos, closestBarPoint);	// speichere Abstand in x- und y-Ausrichtung
//					}
//				}
//			}
//			if (v != null) {
//				return new Vector2d(distXY[0], distXY[1]);
//			} else {
//				// Falls alle Theken besucht oder Essen gefunden.
//				return null;
//			}
			
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
			Vector2d movement = to_next_ausgabe();
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
				this.waitticks = 5000;
			}
		}
	} 

}
