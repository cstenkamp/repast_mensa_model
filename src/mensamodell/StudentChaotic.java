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
	
	private Theke tempDestination;
	private Kasse tempBar = null;
	private Object[] closestkasse;

	public StudentChaotic(ContinuousSpace s, Context c, int num, SharedStuff sharedstuff) {
		super(s, c, num, sharedstuff);
		this.context = c;
		this.vision = 30; // Sichtweite
		this.tempDestination = null; // stellt sicher dass der student bis zur Theke laeuft
		
	}
	
	public Vector2d move_chaotically() {
		/*
		 * Return Values:
		 * distXY == null --> gehe zur Kasse
		 * distXY == (0,0)--> Waehle dein Essen. Du stehst vor einer Theke.
		 * distXY == (X,Y)--> Du bist auf dem Weg.
		 */
		// Falls der student vor einer Theke steht
		if (at_bar()) {
			if (chooseMeal()) return null;
			this.tempDestination = null;
			return new Vector2d(0,0);
		}
		// wenn der Student schon eine theke ausgesucht hat gehe weiter
		if (this.tempDestination != null) {
			double[] temp = space.getDisplacement(space.getLocation(this), space.getLocation(this.tempDestination));
			return new Vector2d(temp[0], temp[1]);
		// falls er noch keine theke erkoren hat suche eine neue	
		} else {
			List<Theke> nonvisited_theken = new ArrayList<Theke>();
			for (Theke t : sharedstuff.theken) { 
				if (!visitedBars.contains(t)) {
					nonvisited_theken.add(t);
					//System.out.println("da war ich nicht "+ t);
				}
			}
			if (nonvisited_theken.isEmpty()) {
				return null;
				// gehe zur kasse, der Student will heute nichts zu essen
			} 
			// suche zufaellig eine Theke aus der Liste und speichere sie als tempDestination
			int index = nonvisited_theken.size();
			Theke randomBar = nonvisited_theken.get(RandomHelper.nextIntFromTo(0, index-1));
			this.tempDestination = randomBar;
			NdPoint location = space.getLocation(randomBar);
			double[] temp = space.getDisplacement(space.getLocation(this), location);
			return new Vector2d(temp[0], temp[1]);
		}
		
	}
	
	
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		Vector2d avoidance = avoid_others();
		if (avoidance != null) {
			velocity.setX(-avoidance.x);
			velocity.setY(-avoidance.y);
		} else {
			Vector2d movement = move_chaotically();
			if (movement != null) {
				// Du bist auf dem Weg.
				velocity.setX(movement.x);
				velocity.setY(movement.y);	
			} else if (movement == null) {
//				System.out.println(this.directlyToKassa);
				if (!this.directlyToKassa.equals(check) && this.directlyToKassa != null) {
					if (tempBar != null && tempBar.pay(this)) {
						System.out.println("Student #" + this.num + " hat die Mensa verlassen.");
						context.remove(this);
					}
					// nimm die bereits gewaehlte Kasse
					velocity.setX(this.directlyToKassa.x);
					velocity.setY(this.directlyToKassa.y);	
				} else {
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
