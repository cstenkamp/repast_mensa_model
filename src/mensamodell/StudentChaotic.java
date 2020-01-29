package mensamodell;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import org.apache.commons.math3.exception.NullArgumentException;

import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;

public class StudentChaotic extends Student {


	public StudentChaotic(ContinuousSpace s, Context c, int num, SharedStuff sharedstuff, int fp) {
		super(s, c, num, sharedstuff, fp);

	}

	//returns null wenn er gerade was zu essen gefunden hat, nicht mehr hungrig ist, oder schon alle Theken besucht hat
	public Vector2d move() {
		/*
		 * Return Values:
		 * null  --> gehe zur Kasse
		 * (X,Y) --> Du bist auf dem Weg.
		 */
		
		// Falls der student vor einer Ausgabe steht
		if (at_bar()) {
			this.tempDestination = null;
			if (this.hungry) {
				if (chooseMeal()) {
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
		// suche zufaellig eine Ausgabe aus der Liste und speichere sie als tempDestination
		List<Ausgabe> nonvisited_ausgaben = new ArrayList<Ausgabe>();
		for (Ausgabe t : sharedstuff.ausgaben) {
			if (!visitedAusgaben.contains(t)) {
				nonvisited_ausgaben.add(t);
			}
		}
		
		if (nonvisited_ausgaben.isEmpty()) {
			return null;
			// gehe zur kasse, der Student will heute nichts zu essen
		}
		
		int index = nonvisited_ausgaben.size();
		Ausgabe randomBar = nonvisited_ausgaben.get(RandomHelper.nextIntFromTo(0, index-1));
		this.tempDestination = randomBar;
		return randomBar;
	}


}
