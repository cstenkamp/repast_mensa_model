package mensamodell;

import repast.simphony.context.Context;
import java.util.Arrays;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.*;
import java.util.List;
import java.util.Collections;

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
	Integer[] foodPrefArray;

	public MensaEingang(int numStud, Object[] prop, Context<Object> context, ContinuousSpace<Object> space, SharedStuff sharedstuff) {
		this.numStudents = numStud;
		this.space = space;
		this.context = context;
		this.addedStudents = 0;
		this.sharedstuff = sharedstuff;
		this.proportions = prop;

		// berechne die Anzahl der einzelnen Food Preferences
		numVeggie = (int) Math.floor(this.numStudents * (double) proportions[0]);
		numVegan = (int) Math.floor(this.numStudents * (double) proportions[1]);
		numMeat = (int) Math.floor(this.numStudents * (double) proportions[2]);
		numNoPref = (int) Math.floor(this.numStudents * (double) proportions[3]);

		// falls die anzahl der initialNumStud noch nicht passt fuelle die werte zufaellig nach
		while (numStudents != (numVeggie+numVegan+numMeat+numNoPref)) {
				int random = RandomHelper.nextIntFromTo(0, 3);
				if (random == 0) numVeggie++;
				if (random == 1) numVegan++;
				if (random == 2) numMeat++;
				if (random == 3) numNoPref++;
		};

		foodPrefArray = new Integer[this.numStudents];
		for (int i = 0; i < numStudents; i++) {
			if (i < numVeggie)
				foodPrefArray[i] = 0;
			else if (i < numVeggie+numVegan)
				foodPrefArray[i] = 1;
			else if (i < numVeggie+numVegan+numMeat)
				foodPrefArray[i] = 2;
			else if (i < numVeggie+numVegan+numMeat+numNoPref)
				foodPrefArray[i] = 3;
		}
		System.out.println(Arrays.toString(foodPrefArray));
		List<Integer> intList = Arrays.asList(foodPrefArray);
		Collections.shuffle(intList);
		intList.toArray(foodPrefArray);
		System.out.println(Arrays.toString(foodPrefArray));
	}

	@ScheduledMethod(start = 0, interval = 1000)
	public void step() {
		passedsteps++;
		// check

		// fuege zufaellig eine food prev ein
		// FIX: mit der alten version hat er wenn random == 0 aber !stillVeggie halt keinen studenten hinzugefügt!
		// FIX am anfang liste generieren mit jeder preference hintereinander, shufflen, nächstes elemtn hziehen
		if (addedStudents < numStudents) {
			fp = foodPrefArray[addedStudents];
			addedStudents++;
			double x, y;
			Student stud;
			int rand = RandomHelper.nextIntFromTo(0, 2); //TODO
			if (rand == 0) stud = new StudentGoalOriented(space, context, addedStudents, sharedstuff, fp);
			else if (rand == 1) stud = new StudentChaotic(space, context, addedStudents, sharedstuff, fp);
			else stud = new StudentPathfinder(space, context, addedStudents, sharedstuff, fp);

			x = RandomHelper.nextIntFromTo(consts.SIZE_X*2/5, consts.SIZE_X*3/5);
			y = consts.SIZE_Y-5;
			context.add(stud);	// add the new students to the root context
			space.moveTo(stud, x, y); // add students to space

			
			System.out.println("Student #"+addedStudents+" x:"+x+" y:"+y+" "+(stud instanceof StudentGoalOriented ? "GoalOriented" : stud instanceof StudentChaotic ? "Chaotic" : "Pathfinder"));
		}

//		if (passedsteps > 50) {
//			passedsteps = 0;
//			sharedstuff.grid.print();
//		}

	}

}
