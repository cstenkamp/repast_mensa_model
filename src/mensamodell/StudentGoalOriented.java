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
		//returns null wenn er gerade was zu essen gefunden hat, nicht mehr hungrig ist, oder schon alle Theken besucht hat

		// Falls der student vor einer Ausgabe steht
		Ausgabe a= at_bar();
		if (a != null) {
			if (this.hungry) {
				if (chooseMeal(a)) {
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
			 * null --> gehe zur Kasse
			 * (X,Y) --> Du bist auf dem Weg.
			 */
			try {
				Ausgabe closesttheke = (Ausgabe) next_aim();
				return walk_but_dont_bump(closesttheke);
			} catch (NullArgumentException e) {
				return null;
			}
		}
		
	

}
