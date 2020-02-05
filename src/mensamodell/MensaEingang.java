package mensamodell;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class MensaEingang {
	int numStudents;
	Context<Object> context;
	ContinuousSpace<Object> space;
	SharedStuff sharedstuff;
	int addedStudents = 0;
	int numVeggie;
	int numVegan;
	int numMeat;
	int numNoPref;
	int numChaotic;
	int numGoal;
	int numPath; //=numShort
	Integer[] foodPrefArray;
	Integer[] walkStyleArray;
	
	float x;
	float y;
	Grid<Object> grid;
	Ausgabe aktionstheke; //wenn da zu viele stehen nicht anstellen //TODO!!

	
	//Konstruktor für 2D-Env
	public MensaEingang(float x, float y, int numStud, Double[] propEat, Double[] propWalk, Context<Object> context, ContinuousSpace<Object> space, SharedStuff sharedstuff) {
		this.x = x;
		this.y = y;
		this.numStudents = numStud;
		this.space = space;
		this.context = context;
		this.sharedstuff = sharedstuff;
		this.foodPrefArray = createFoodPref(propEat);
		this.walkStyleArray = createWalkStyle(propWalk);
		context.add(this);
		space.moveTo(this, x, y);
	}
		
	//Konstruktor für Grid
	public MensaEingang(int x, int y, int numStud, Double[] propEat, Double[] propWalk, Context<Object> context, SharedStuff sharedstuff, Grid<Object> g, Ausgabe a) {
		this.x = x;
		this.y = y - 1;
		this.grid = g;
		this.context = context;
		this.aktionstheke = a;
		this.numStudents = numStud;
		this.sharedstuff = sharedstuff;
		this.foodPrefArray = createFoodPref(propEat);
		this.walkStyleArray = createWalkStyle(propWalk); 
		context.add(this);
		grid.moveTo(this, x, y);
	}

		
private Integer[] createFoodPref(Double[] prop) {		
		// berechne die Anzahl der einzelnen Food Preferences
		numVeggie = (int) Math.floor(this.numStudents * (double) prop[0]);
		numVegan = (int) Math.floor(this.numStudents * (double) prop[1]);
		numMeat = (int) Math.floor(this.numStudents * (double) prop[2]);
		numNoPref = (int) Math.floor(this.numStudents * (double) prop[3]);
		// falls die anzahl der initialNumStud noch nicht passt fuelle die werte zufaellig nach
		while (numStudents != (numVeggie+numVegan+numMeat+numNoPref)) {
				int random = RandomHelper.nextIntFromTo(0, 3);
				if (random == 0) numVeggie++;
				if (random == 1) numVegan++;
				if (random == 2) numMeat++;
				if (random == 3) numNoPref++;
		};
		Integer[] tmp_arr = new Integer[this.numStudents];
		for (int i = 0; i < numStudents; i++) {
			if (i < numVeggie)
				tmp_arr[i] = 0;
			else if (i < numVeggie+numVegan)
				tmp_arr[i] = 1;
			else if (i < numVeggie+numVegan+numMeat)
				tmp_arr[i] = 2;
			else if (i < numVeggie+numVegan+numMeat+numNoPref)
				tmp_arr[i] = 3;
		}
		System.out.println(Arrays.toString(tmp_arr));
		List<Integer> intList = Arrays.asList(tmp_arr);
		Collections.shuffle(intList);
		intList.toArray(tmp_arr);
		System.out.println(Arrays.toString(tmp_arr));
		return tmp_arr;
	}
		

private Integer[] createWalkStyle(Double[] prop) {
		// berechne die Walking-Styles der einzelnen Studenten
		numChaotic = (int) Math.floor(this.numStudents * (double) prop[0]);
		numGoal = (int) Math.floor(this.numStudents * (double) prop[1]);
		numPath = (int) Math.floor(this.numStudents * (double) prop[2]);
		// falls die anzahl der initialNumStud noch nicht passt fuelle die werte zufaellig nach
		while (numStudents != (numChaotic+numGoal+numPath)) {
				int random = RandomHelper.nextIntFromTo(0, 2);
				if (random == 0) numChaotic++;
				if (random == 1) numGoal++;
				if (random == 2) numPath++;
		};
		Integer[] tmp_arr = new Integer[this.numStudents];
		for (int i = 0; i < numStudents; i++) {
			if (i < numChaotic)
				tmp_arr[i] = 0;
			else if (i < numChaotic+numGoal)
				tmp_arr[i] = 1;
			else if (i < numChaotic+numGoal+numPath)
				tmp_arr[i] = 2;
		}
		System.out.println(Arrays.toString(tmp_arr));
		List<Integer> intListWalk = Arrays.asList(tmp_arr);
		Collections.shuffle(intListWalk);
		intListWalk.toArray(tmp_arr);
		System.out.println(Arrays.toString(tmp_arr));		
		return tmp_arr;
	}




	//ScheduledMethod(start = 0, interval = consts.EINGANG_DELAY)
	public void step() {		
		if (addedStudents < numStudents) {
			
			if (aktionstheke != null && aktionstheke.getStudentsInQueue() > (y-9)) 
				return; //falls es ein grid gibt und hier zu viel schlange ist kann keiner kommen //TODO Hm kann das anders?
			
			int fp = foodPrefArray[addedStudents];
			Student stud = null;
			switch(walkStyleArray[addedStudents]) {
			case 0: 
				if (space != null) 
					stud = new StudentChaotic(addedStudents, sharedstuff, fp, context, space);
				else
					stud = new StudentChaotic(addedStudents, sharedstuff, fp, context, grid, (int)x, (int)y);
				break;
			case 1:
				if (space != null) 
					stud = new StudentGoalOriented(addedStudents, sharedstuff, fp, context, space);
				else
					stud = new StudentGoalOriented(addedStudents, sharedstuff, fp, context, grid, (int)x, (int)y);
				break;
			case 2:
				if (space != null) 
					stud = new StudentPathfinder(addedStudents, sharedstuff, fp, context, space);
				else
					stud = new StudentShortestQueue(addedStudents, sharedstuff, fp, context, grid, (int)x, (int)y);
				break;
			}

			addedStudents++;
			System.out.println("Student #"+addedStudents+" @"+(int)sharedstuff.schedule.getTickCount()+" x:"+x+" y:"+y+" "+(stud instanceof StudentGoalOriented ? "GoalOriented " : stud instanceof StudentChaotic ? "Chaotic " : "Pathfinder ")+
					(fp == consts.MEAT ? "Fleischesser" : fp == consts.VEGGIE ? "Vegetarier" : fp == consts.VEGANER ? "Veganer" : "Ohne Präferenz"));
		}

		if (sharedstuff.mgrid != null && sharedstuff.schedule.getTickCount()/consts.EINGANG_DELAY % 100 == 0) {
			sharedstuff.mgrid.print();
		}

	}

}
