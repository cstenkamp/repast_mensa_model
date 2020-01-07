package mensamodell;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.*;
import repast.simphony.random.RandomHelper;

public class modelbuilder implements ContextBuilder<Object>{

	@Override
	public Context<Object> build(Context<Object> context) {
		
		// get parameters from the GUI
		Parameters param = RunEnvironment.getInstance().getParameters();
		// save the parameters in variables
		int initialNumStud = (Integer)param.getValue("initialNumStud");
		
		// create ContinuousSpace, size: 100x80
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), 100, 80, 1);
		
		/*
		 * ContinuousSpaceFactory spaceFact = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		 * ContinuousSpace<Object> space = spaceFact.createContinuousSpace("space", context, new SimpleCartesianAdder<Object>(), new WrapAroundBorders(), 10, 10);
		 */
		
		// add students to context
		for (int i = 0; i < initialNumStud; i++) {
			basestudent stud = new basestudent(space);	// add new students
			context.add(stud);							// add the new prey to the root context
		}
		
		return context;
	} // END of Context.

} // END of modelbuilder.
