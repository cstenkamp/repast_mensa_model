package mensamodell;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class StudentShortestQueue extends Student{

	public StudentShortestQueue(int num, SharedStuff sharedstuff, int fp, Context<Object> context, ContinuousSpace<Object> s) {
		super(num, sharedstuff, fp, context, s);
	}
	
	// Suche dir die Ausgabe mit der kuerzesten Schlange
//	@Override
//	public Ausgabe move() {
//		Ausgabe nextBar = null;
//		int sizeMin = 999999;
//		for (Object a : Share.ausgabenList) {
//			if (!this.visitedAusgaben.contains(a)) {
//				int size = ((Ausgabe) a).getQueueSize();
//				if (size <= sizeMin) {
//					sizeMin = size;
//					nextBar = (Ausgabe) a;
//				}
//			}
//		}
//		return nextBar;	
//	}
}
