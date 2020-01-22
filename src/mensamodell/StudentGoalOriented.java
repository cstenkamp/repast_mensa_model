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
	
	private Theke tempDestination;
	private Kasse tempBar = null;
	private Object[] closestkasse;

	public StudentGoalOriented(ContinuousSpace s, Context c, int num, SharedStuff sharedstuff) {
		super(s, c, num, sharedstuff);
		this.vision = 300; // Sichtweite
	}
	
	// Sucht den kuerzesten Weg
		public Vector2d to_next_ausgabe() {
			
			/*
			 * Return Values:
			 * distXY == null --> gehe zur Kasse
			 * distXY == (X,Y)--> Du bist auf dem Weg.
			 */
			
			// Falls der student vor einer Theke steht
			if (at_bar()) {
				// waehle das Essen
				if (chooseMeal()) return null;
				return new Vector2d(0,0);
			}
						
			List<Theke> nonvisited_theken = new ArrayList<Theke>();
			for (Theke t : sharedstuff.theken) 
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
	
	/*
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
			if (movement != null) {
				// Du bist auf dem Weg.
				velocity.setX(movement.x);
				velocity.setY(movement.y);	
			} else if (movement == null) {
				//System.out.println(this.directlyToKassa);
				if (!this.directlyToKassa.equals(check) && this.directlyToKassa != null) {
					if (tempBar != null && tempBar.pay(this)) {
						System.out.println("Student #" + this.num + " hat die Mensa verlassen.");
						context.remove(this);
					}
					// nimm die bereits gewaehlte Kasse
					velocity.setX(this.directlyToKassa.x);
					velocity.setY(this.directlyToKassa.y);	
				}else {
					// waehle Kasse
					//System.out.println("choose Kassa");
					// gibt Location und Objekt zurueck
					this.closestkasse = to_kasse();
					this.tempBar = (Kasse) this.closestkasse[0];
					this.directlyToKassa = (Vector2d) this.closestkasse[1];
					if (this.directlyToKassa != null) {
						velocity.setX(this.directlyToKassa.x);
						velocity.setY(this.directlyToKassa.y);	
					}
				}
			} 
		}
	} 

}
