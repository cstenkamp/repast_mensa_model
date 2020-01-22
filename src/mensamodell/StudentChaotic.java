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


	public StudentChaotic(ContinuousSpace s, Context c, int num, SharedStuff sharedstuff, int fp) {
		super(s, c, num, sharedstuff, fp);

	}

	public Vector2d move() {
		/*
		 * Return Values:
		 * distXY == null --> gehe zur Kasse
		 * distXY == (X,Y)--> Du bist auf dem Weg.
		 */
		// Falls der student vor einer Ausgabe steht
		if (at_bar()) {
			this.tempDestination = null;
			if (chooseMeal()) return null;
//			return new Vector2d(0,0);
		}
		// wenn der Student schon eine theke ausgesucht hat gehe weiter
		if (this.tempDestination != null) {
			double[] temp = space.getDisplacement(space.getLocation(this), space.getLocation(this.tempDestination));
			return new Vector2d(temp[0], temp[1]);
		// falls er noch keine theke erkoren hat suche eine neue
		} else {
			List<Ausgabe> nonvisited_ausgaben = new ArrayList<Ausgabe>();
			for (Ausgabe t : sharedstuff.ausgaben) {
				if (!visitedAusgaben.contains(t)) {
					nonvisited_ausgaben.add(t);
					//System.out.println("da war ich nicht "+ t);
				}
			}
			if (nonvisited_ausgaben.isEmpty()) {
				return null;
				// gehe zur kasse, der Student will heute nichts zu essen
			}
			// suche zufaellig eine Ausgabe aus der Liste und speichere sie als tempDestination
			int index = nonvisited_ausgaben.size();
			Ausgabe randomBar = nonvisited_ausgaben.get(RandomHelper.nextIntFromTo(0, index-1));
			this.tempDestination = randomBar;
			NdPoint location = space.getLocation(randomBar);
//			System.out.println(space.getLocation(this).getX() + " " + space.getLocation(this).getY());
			double[] temp = space.getDisplacement(space.getLocation(this), location); // TODO CAUSES NaN: space.getLocation(this) Nur am Eingang
			return new Vector2d(temp[0], temp[1]);
		}

	}




}
