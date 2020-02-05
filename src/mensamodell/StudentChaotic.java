package mensamodell;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import org.apache.commons.math3.exception.NullArgumentException;

import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;

public class StudentChaotic extends Student {


	public StudentChaotic(int num, SharedStuff sharedstuff, int fp, Context<Object> context, ContinuousSpace<Object> s) {
		super(num, sharedstuff, fp, context, s);

	}

	//returns null wenn er gerade was zu essen gefunden hat, nicht mehr hungrig ist, oder schon alle Theken besucht hat
	public Vector2d move() {
		/*
		 * Return Values:
		 * null  --> gehe zur Kasse
		 * (X,Y) --> Du bist auf dem Weg.
		 */

		// Falls der student vor einer Ausgabe steht
		Ausgabe a= at_bar();
		if (a != null) {
			this.tempDestination = null;
			if (this.hungry) {
				if (chooseMeal(a)) {
					this.hungry = false; 
					return null;
				}
			}
		}
		
		if (!this.hungry)
			return null;
		
		// wenn der Student schon eine theke ausgesucht hat gehe weiter zu dieser theke
		if (this.tempDestination != null) {
			return walk_but_dont_bump(this.tempDestination);
		// falls er noch keine theke erkoren hat suche eine neue
		} else {
			try {
				Ausgabe closesttheke = (Ausgabe) next_aim();
				return walk_but_dont_bump(closesttheke);
			} catch (NullArgumentException e) {
				return null;
			}
		}


	}

	public Object next_aim() {
		
		if (best_food_so_war_was == -1) { //dann ist er alle abgeklappert und hat sich die ausgesucht für die er sich dann final entscheidet
			return this.tempDestination;
		}
		
		// suche zufaellig eine Ausgabe aus der Liste und speichere sie als tempDestination
		List<Ausgabe> nonvisited_ausgaben = new ArrayList<Ausgabe>();
		for (Ausgabe t : sharedstuff.ausgaben) {
			if (!visitedAusgaben.contains(t)) {
				nonvisited_ausgaben.add(t);
			}
		}
		
		if (nonvisited_ausgaben.isEmpty()) {
			if (consts.MAY_LEAVE_WITHOUT_FOOD) {
				return null;
			} else {
				best_food_so_war_was = -1;
				int index = RandomHelper.nextIntFromTo(0, best_food_so_far.size()-1);
				this.tempDestination = best_food_so_far.get(index);
				return this.tempDestination;
			}			
		}
		
		int index = nonvisited_ausgaben.size();
		Ausgabe randomBar = nonvisited_ausgaben.get(RandomHelper.nextIntFromTo(0, index-1));
		this.tempDestination = randomBar;
		return randomBar;
	}


}
