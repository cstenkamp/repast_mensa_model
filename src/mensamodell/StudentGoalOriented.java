package mensamodell;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import org.apache.commons.math3.exception.NullArgumentException;

import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class StudentGoalOriented extends Student {


	public StudentGoalOriented(int num, SharedStuff sharedstuff, int fp, Context<Object> context, ContinuousSpace<Object> s) {
		super(num, sharedstuff, fp, context, s);
	}
	
	public StudentGoalOriented(int num, SharedStuff sharedstuff, int fp, Context<Object> context, Grid<Object> g, int x, int y) {
		super(num, sharedstuff, fp, context, g, x, y);
		sharedstuff.studierendeGoal.add(this);
	}
	
    @Override
    public String toString() {
      return "StudentGoalOriented(#"+num+", "+get_pref_string()+")";
  } 
	
	@Override
	public Ausgabe next_ausgabe() {
		List<Ausgabe> empty_enough_bars = new ArrayList<Ausgabe>();
		for (Ausgabe a : consideredBarsList) 
			if (a.getStudentsInQueue() < consts.SIZE_Y-5) //sonst würdest du dich außerhalb des grids stellen
				empty_enough_bars.add(a);
		
		if (empty_enough_bars.isEmpty())
			throw new IndexOutOfBoundsException(this+": findet keine leer genug-en Ausgaben!");
		
		Ausgabe nextBar = empty_enough_bars.get(RandomHelper.nextIntFromTo(0, empty_enough_bars.size()-1));
		return nextBar;
	}
	
	@Override
	/// Goal-Oriented studierende sind mit jedem Essen einverstanden
	public boolean chooseMeal(Ausgabe currentBar) {
		if (sharedstuff.space != null)
			this.waitTicks = currentBar.getWaitTicks();
		return true;
	}
	
	
	
	// ====================================== Space operations ======================================
	
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
		
		if (best_food_so_war_was == -1) { //dann ist er alle abgeklappert und hat sich die ausgesucht für die er sich dann final entscheidet
			return this.tempDestination;
		}
		
		
		List<Ausgabe> nonvisited_ausgaben = new ArrayList<Ausgabe>();
		for (Ausgabe t : sharedstuff.ausgaben) {
			if (!visitedAusgaben.contains(t)) nonvisited_ausgaben.add(t);
		}
	
		if (nonvisited_ausgaben.isEmpty()) {
			if (consts.MAY_LEAVE_WITHOUT_FOOD) {
				return null;
			} else {
				best_food_so_war_was = -1;
				int index = RandomHelper.nextIntFromTo(0, best_food_so_far.size()-1);
				if (best_food_so_far.size() > 0)
					this.tempDestination = best_food_so_far.get(index);
				//TODO was ist denn else? :D warum hat ers nicht gesetzt?
				return this.tempDestination;
			}			
		}

		Ausgabe closesttheke = (Ausgabe) get_closest(nonvisited_ausgaben);
		return closesttheke;
	}
	

	// Sucht den kuerzesten Weg
		public Vector2d move_spatial() {
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
