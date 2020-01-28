package mensamodell;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import org.apache.commons.math3.exception.NullArgumentException;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;

public class StudentGoalOriented extends Student {


	public StudentGoalOriented(ContinuousSpace s, Context c, int num, SharedStuff sharedstuff, int fp) {
		super(s, c, num, sharedstuff, fp);
	}
	
	public Object next_aim() {

		// Falls der student vor einer Ausgabe steht
		if (at_bar()) {

			if (this.hungry) {
				if (chooseMeal()) {
					this.hungry = false; 
					return null;
				}
			}
		}
		if (!this.hungry)
			return null;

		List<Ausgabe> nonvisited_ausgaben = new ArrayList<Ausgabe>();
		for (Ausgabe t : sharedstuff.ausgaben) {
			if (!visitedAusgaben.contains(t)) nonvisited_ausgaben.add(t);
		}
		if (nonvisited_ausgaben.isEmpty()) return null;

		Ausgabe closesttheke = (Ausgabe) get_closest(nonvisited_ausgaben);
		
		return closesttheke;
	}
	

	// Sucht den kuerzesten Weg
		public Vector2d move() {

			/*
			 * Return Values:
			 * distXY == null --> gehe zur Kasse
			 * distXY == (X,Y)--> Du bist auf dem Weg.
			 */
			try {
				Ausgabe closesttheke = (Ausgabe) next_aim();
				return walk_but_dont_bump(closesttheke);
			} catch (NullArgumentException e) {
				return null;
			}
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
