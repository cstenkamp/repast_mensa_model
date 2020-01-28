package mensamodell;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;

public class MensaEingang {
	int numStudents;
	Context<Object> context;
	ContinuousSpace<Object> space;
	int addedStudents;
	SharedStuff sharedstuff;
	int passedsteps =  0;
	Object[] proportions;
	Object[] proportionsWalk;
	int fp;
	int numVeggie;
	int numVegan;
	int numMeat;
	int numNoPref;
	int numChaotic;
	int numGoal;
	int numPath;
	Integer[] foodPrefArray;
	Integer[] studNumArray;

	public MensaEingang(int numStud, Object[] prop, Object[] propWalk, Context<Object> context, ContinuousSpace<Object> space, SharedStuff sharedstuff) {
		this.numStudents = numStud;
		this.space = space;
		this.context = context;
		this.addedStudents = 0;
		this.sharedstuff = sharedstuff;
		this.proportions = prop;
		this.proportionsWalk = propWalk;

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
		
		// berechne die Anzahl der einzelnen Studenten
		numChaotic = (int) Math.floor(this.numStudents * (double) proportionsWalk[0]);
		numGoal = (int) Math.floor(this.numStudents * (double) proportionsWalk[1]);
		numPath = (int) Math.floor(this.numStudents * (double) proportionsWalk[2]);

		// falls die anzahl der initialNumStud noch nicht passt fuelle die werte zufaellig nach
		while (numStudents != (numChaotic+numGoal+numPath)) {
				int random = RandomHelper.nextIntFromTo(0, 2);
				if (random == 0) numChaotic++;
				if (random == 1) numGoal++;
				if (random == 2) numPath++;
		};

		studNumArray = new Integer[this.numStudents];
		for (int i = 0; i < numStudents; i++) {
			if (i < numChaotic)
				studNumArray[i] = 0;
			else if (i < numChaotic+numGoal)
				studNumArray[i] = 1;
			else if (i < numChaotic+numGoal+numPath)
				studNumArray[i] = 2;
		}
		System.out.println(Arrays.toString(studNumArray));
		List<Integer> intListWalk = Arrays.asList(studNumArray);
		Collections.shuffle(intListWalk);
		intListWalk.toArray(studNumArray);
		System.out.println(Arrays.toString(studNumArray));		
	}

	@ScheduledMethod(start = 0, interval = 1000)
	public void step() {
		passedsteps++;
		// check

		// fuege zufaellig eine food prev ein
		// FIX: mit der alten version hat er wenn random == 0 aber !stillVeggie halt keinen studenten hinzugefügt!
		// FIX am anfang liste generieren mit jeder preference hintereinander, shufflen, nächstes elemtn hziehen
		if (fp != -1 && addedStudents < numStudents) {
			fp = foodPrefArray[addedStudents];

			double x, y;
			Student stud;
			int walkingStyle = studNumArray[addedStudents];
			if (walkingStyle == 0) stud = new StudentGoalOriented(space, context, addedStudents, sharedstuff, fp);
			else if (walkingStyle == 1) stud = new StudentChaotic(space, context, addedStudents, sharedstuff, fp);
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
