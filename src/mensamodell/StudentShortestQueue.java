package mensamodell;

import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;

public class StudentShortestQueue extends Student{

	public StudentShortestQueue(int num, SharedStuff sharedstuff, int fp, Context<Object> context, Grid<Object> g, int x, int y) {
		super(num, sharedstuff, fp, context, g, x, y);
	}

  @Override
  public String toString() { 
      return "StudentShortestQueu(#"+num+", "+get_pref_string()+")";
  } 
  
	// Suche dir die Ausgabe mit der kuerzesten Schlange
	@Override
	public Ausgabe next_ausgabe() {
		Ausgabe nextBar = null;
		
		if (consideredBarsList.size() == visitedAusgaben.size()) { //dann hat er schon alle durchprobiert
			best_food_so_war_was = -1;
			return best_food_so_far.get(RandomHelper.nextIntFromTo(0, best_food_so_far.size()-1));
		}
		
		int sizeMin = 999999;
		for (Object a : consideredBarsList) {
			if (!visitedAusgaben.contains(a)) {
				int size = ((Ausgabe) a).getStudentsInQueue();
				if (size <= sizeMin) {
					sizeMin = size;
					nextBar = (Ausgabe) a;
				}
			}
		}
		return nextBar;	
	}
}
