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

	@Override
	public Context<Object> build(Context<Object> context) {

		// get parameters from the GUI
		Parameters param = RunEnvironment.getInstance().getParameters();
		// save the parameters in variables
		int initialNumStud = (Integer)param.getValue("initialNumStud");

		// create ContinuousSpace, size: 100x70
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);

		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context, new SimpleCartesianAdder<Object>(), new StrictBorders(), consts.SIZE_X, consts.SIZE_Y);

		// add Theke to context
		Theke aktionstheke = new Theke(consts.SIZE_X/2, 10, consts.AKTIONSTHEKE);
		context.add(aktionstheke);
		space.moveTo(aktionstheke, aktionstheke.x, aktionstheke.y);

		// add students to context
		for (int i = 0; i < initialNumStud; i++) {
			Student stud = new Student(space);	// add new students
			context.add(stud);	// add the new students to the root context
			space.moveTo(stud, (double)consts.SIZE_X/2, (double)consts.SIZE_Y-5); // add students to space
		}

		return context;
	} // END of Context.

} // END of modelbuilder.
