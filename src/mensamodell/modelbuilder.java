package mensamodell;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.*;
import repast.simphony.random.RandomHelper;

import mensamodell.consts.*;



public class modelbuilder implements ContextBuilder<Object>{

	public final int SIZE_X = 200;
	public final int SIZE_Y = 200;

	@Override
	public Context<Object> build(Context<Object> context) {
		
		// get parameters from the GUI
		Parameters param = RunEnvironment.getInstance().getParameters();
		// save the parameters in variables
		int initialNumStud = (Integer)param.getValue("initialNumStud");
		
		// create ContinuousSpace, size: 100x70
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context, new SimpleCartesianAdder<Object>(), new StrictBorders(), SIZE_X, SIZE_Y);
		
		Theke aktionstheke = new Theke(SIZE_X/2, 0, consts.AKTIONSTHEKE);
		context.add(aktionstheke);
		space.moveTo(aktionstheke, aktionstheke.x, aktionstheke.y);
		
		// add students to context
		for (int i = 0; i < initialNumStud; i++) {
			basestudent stud = new basestudent(space);	// add new students
			context.add(stud);	// add the new prey to the root context
			space.moveTo(stud, 50, 50);
		}
		
		return context;
	} // END of Context.

} // END of modelbuilder.
