package mensamodell;

import repast.simphony.context.Context;
import repast.simphony.space.grid.Grid;

public class StudentShortestQueue extends Student{

	public StudentShortestQueue(int num, SharedStuff sharedstuff, int fp, Context<Object> context, Grid<Object> g, int x, int y) {
		super(num, sharedstuff, fp, context, g, x, y);
		sharedstuff.studierendeQueue.add(this);
	}

  @Override
  public String toString() { 
      return "StudentShortestQueu(#"+num+", "+get_pref_string()+")";
  } 
  
	// Suche dir die Ausgabe mit der kuerzesten Schlange
	@Override
	public Ausgabe next_ausgabe() {
		Ausgabe nextBar = null;
		int sizeMin = 999999;
		for (Object a : this.consideredBarsList) {
			if (!this.visitedAusgaben.contains(a)) {
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
