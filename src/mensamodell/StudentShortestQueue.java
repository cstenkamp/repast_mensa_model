package mensamodell;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class StudentShortestQueue extends Student{

	public StudentShortestQueue(int num, SharedStuff sharedstuff, int fp, Context<Object> context, Grid<Object> g, int x, int y) {
		super(num, sharedstuff, fp, context, g, x, y);
	}

	// Suche dir die Ausgabe mit der kuerzesten Schlange
	@Override
	public Ausgabe next_ausgabe() {
		Ausgabe nextBar = null;
		int sizeMin = 999999;
		for (Object a : sharedstuff.ausgaben) {
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
