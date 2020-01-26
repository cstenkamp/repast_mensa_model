package mensamodell;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.*;
import java.util.List;

public class MensaEingang {
	int numStudents;
	Context<Object> context;
	ContinuousSpace<Object> space;
	int addedStudents;
	SharedStuff sharedstuff;
	int passedsteps =  0;
	Object[] proportions;
	int fp;
	int numVeggie;
	int numVegan;
	int numMeat;
	int numNoPref;
	boolean stillVeggie = false;
	boolean stillVegan = false;
	boolean stillMeat = false;
	boolean stillNoPref = false;
	
	public MensaEingang(int numStud, Object[] prop, Context<Object> context, ContinuousSpace<Object> space, SharedStuff sharedstuff) {
		this.numStudents = numStud;
		this.space = space;
		this.context = context;
		this.addedStudents = 0;
		this.sharedstuff = sharedstuff;
		this.proportions = prop;
		
		// berechne die Anzahl der einzelnen Food Preferences
		numVeggie = (int) (this.numStudents * (double) proportions[0]);
		numVegan = (int) (this.numStudents * (double) proportions[1]);
		numMeat = (int) (this.numStudents * (double) proportions[2]);
		numNoPref = (int) (this.numStudents * (double) proportions[3]);
		
		// falls die anzahl der initialNumStud noch nicht passt fuelle die werte zufaellig nach
		if (numStudents > (numVeggie+numVegan+numMeat+numNoPref)) {
			do {
				int random = RandomHelper.nextIntFromTo(0, 3);
				if (random == 0) numVeggie++;
				if (random == 1) numVegan++;
				if (random == 2) numMeat++;
				if (random == 3) numNoPref++;
			} while (numStudents != (numVeggie+numVegan+numMeat+numNoPref));
		}
		if (numStudents < (numVeggie+numVegan+numMeat+numNoPref)) {
			do {
				int random = RandomHelper.nextIntFromTo(0, 3);
				if (random == 0) numVeggie--;
				if (random == 1) numVegan--;
				if (random == 2) numMeat--;
				if (random == 3) numNoPref--;
			} while (numStudents != (numVeggie+numVegan+numMeat+numNoPref));
		}
		// flag
		if (numVeggie > 0) stillVeggie = true; 
		if (numVegan > 0) stillVegan = true; 
		if (numMeat > 0) stillMeat = true; 
		if (numNoPref > 0) stillNoPref = true; 
	}

	@ScheduledMethod(start = 0, interval = 1000)
	public void step() {
		passedsteps++;
		// check
		fp = -1;
		// falls einzele food prevs ausgelassen werden sollen setze flag auf false
		if (numVeggie <= 0) stillVeggie = false; 
		if (numVegan <= 0) stillVegan = false; 
		if (numMeat <= 0) stillMeat = false; 
		if (numNoPref <= 0) stillNoPref = false; 
		
		// fuege zufaellig eine food prev ein
		int random = RandomHelper.nextIntFromTo(0,3);
		if (random == 0 && stillVeggie) {fp = 0; numVeggie--;}
		if (random == 1 && stillVegan) {fp = 1; numVegan--;}
		if (random == 2 && stillMeat) {fp = 2; numMeat--;}
		if (random == 3 && stillNoPref) {fp = 3; numNoPref--;}
		
		if (fp != -1 && addedStudents <= numStudents) {
			double x, y;
			Student stud;
			int rand = RandomHelper.nextIntFromTo(0, 2);
			if (rand == 0) stud = new StudentGoalOriented(space, context, addedStudents, sharedstuff, fp);
			else if (rand == 1) stud = new StudentChaotic(space, context, addedStudents, sharedstuff, fp);
			else stud = new StudentPathfinder(space, context, addedStudents, sharedstuff, fp);
				
			x = RandomHelper.nextIntFromTo(consts.SIZE_X*2/5, consts.SIZE_X*3/5);
			y = consts.SIZE_Y-5;
			context.add(stud);	// add the new students to the root context
			space.moveTo(stud, x, y); // add students to space
			
			addedStudents++;
			System.out.println("Student #"+addedStudents+" x:"+x+" y:"+y+" "+(stud instanceof StudentGoalOriented ? "GoalOriented" : stud instanceof StudentChaotic ? "Chaotic" : "Pathfinder"));
		}
		
//		if (passedsteps > 50) {
//			passedsteps = 0;
//			sharedstuff.grid.print();
//		}
		
	}

}
