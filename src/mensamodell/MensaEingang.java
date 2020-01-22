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
	
	public MensaEingang(int numStudents, Context<Object> context, ContinuousSpace<Object> space, SharedStuff sharedstuff) {
		this.numStudents = numStudents;
		this.space = space;
		this.context = context;
		this.addedStudents = 0;
		this.sharedstuff = sharedstuff;
	}

	@ScheduledMethod(start = 0, interval = 1000)
	public void step() {
		passedsteps++;

		if (addedStudents < numStudents) {
			double x, y;
			Student stud;
			if (RandomHelper.nextIntFromTo(0, 1) == 0)
				stud = new StudentGoalOriented(space, context, addedStudents, sharedstuff);
			else
				stud = new StudentChaotic(space, context, addedStudents, sharedstuff);
			
			x = RandomHelper.nextIntFromTo(consts.SIZE_X*2/5, consts.SIZE_X*3/5);
			y = consts.SIZE_Y-5;
			context.add(stud);	// add the new students to the root context
			space.moveTo(stud, x, y); // add students to space
			
			System.out.println("Student #"+addedStudents+" x:"+x+" y:"+y+" "+(stud instanceof StudentGoalOriented ? "GoalOriented": "Chaotic"));
			addedStudents++;
		}
		
		if (passedsteps > 50) {
			passedsteps = 0;
			sharedstuff.grid.print();
		}
		
	}

}
