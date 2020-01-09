package mensamodell;

import javax.vecmath.Vector2d;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.context.Context;

public class StudentGoalOriented extends Student {

	public StudentGoalOriented(ContinuousSpace s, Context c) {
		super(s, c);
		this.vision = 300; // Sichtweite
	}
	
	// Sucht den kuerzesten Weg
		public double[] to_next_ausgabe() {
			
			/*
			 * Return Values:
			 * distXY == null --> gehe zur Kasse
			 * distXY == (0,0)--> Wähle dein Essen. Du stehst vor einer Theke.
			 * distXY == (X,Y)--> Du bist auf dem Weg.
			 */
			
			// Falls der student vor einer Theke steht
			if (at_bar() != null) return at_bar();
						
			// Suche deinen Weg zur naechsten Theke
			double[] distXY = null;			
			NdPoint lastPos = space.getLocation(this);										// speichere die aktuelle Position
			ContinuousWithin barInVision = new ContinuousWithin(space, this, vision);		// erzeugt eine Query mit allen Objekten im Sichtradius
			double minBarDist = vision;					// kuerzester Abstand zu einer Bar
			NdPoint closestBarPoint = new NdPoint();	// Punkt mit naechster Bar
			Theke v = null; 							// zum speichern der besuchten Theke
			
			for (Object o : barInVision.query()){			// Durchlaufe die Query des Sichtradius
				if (o instanceof Theke && !visitedBars.contains(o)){	// falls das Theke und noch nicht besucht
					Theke tempBar = (Theke) o;
					NdPoint tempBarLoc = space.getLocation(tempBar);
					double dist = space.getDistance(lastPos, tempBarLoc);			// Distanz zur Theke, falls minimum -> speichern
					if (dist < minBarDist){
						minBarDist = dist;
						closestBarPoint = tempBarLoc;
						v = tempBar;
						distXY = space.getDisplacement(lastPos, closestBarPoint);	// speichere Abstand in x- und y-Ausrichtung
					}
				}
			}
			if (v != null) {
				return distXY;
			} else {
				// Falls alle Theken besucht oder Essen gefunden.
				return null;
			}
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
			} else if (movement == null) {
				// gehe zur Kasse
				movement = to_kasse();
				velocity.setX(movement[0]);
				velocity.setY(movement[1]);	
			} else {
				// Wähle dein Essen. Du stehst vor einer Theke.
				System.out.println("Essenswahl!");
			}
		}
	} 

}
