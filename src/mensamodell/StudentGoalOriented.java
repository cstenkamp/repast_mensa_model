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
	
	private Ausgabe tempDestination;
	private Kasse tempBar = null;
	private Object[] closestkasse;

	public StudentGoalOriented(ContinuousSpace s, Context c, int num, SharedStuff sharedstuff, int fp) {
		super(s, c, num, sharedstuff, fp);
	}
	
	// Sucht den kuerzesten Weg
		public Vector2d move() {
			
			/*
			 * Return Values:
			 * distXY == null --> gehe zur Kasse
			 * distXY == (X,Y)--> Du bist auf dem Weg.
			 */
			
			// Falls der student vor einer Ausgabe steht
			if (at_bar()) {
				// waehle das Essen
				if (chooseMeal()) return null;
//				return new Vector2d(0,0);
			}
						
			List<Ausgabe> nonvisited_ausgaben = new ArrayList<Ausgabe>();
			for (Ausgabe t : sharedstuff.ausgaben) 
				if (!visitedAusgaben.contains(t)) 
					nonvisited_ausgaben.add(t);
			
			if (nonvisited_ausgaben.isEmpty())
				return null;
			
			Object[] clostesttheke = get_closest(nonvisited_ausgaben);
			Vector2d distance = (Vector2d) clostesttheke[1];
			Ausgabe k = (Ausgabe) clostesttheke[0];
			return distance;
		}
			
			
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
