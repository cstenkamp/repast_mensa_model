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
			if (at_bar() && chooseMeal()) return null;
						
			List<Ausgabe> nonvisited_ausgaben = new ArrayList<Ausgabe>();
			for (Ausgabe t : sharedstuff.ausgaben) {
				if (!visitedAusgaben.contains(t)) nonvisited_ausgaben.add(t);
			}
			if (nonvisited_ausgaben.isEmpty()) return null;
			
			Object[] clostesttheke = get_closest(nonvisited_ausgaben);
			Vector2d distance = (Vector2d) clostesttheke[1];
			Ausgabe k = (Ausgabe) clostesttheke[0];
			return distance;
		}				
}
