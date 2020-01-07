package mensamodell;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.continuous.*;
import repast.simphony.random.RandomHelper;

public class modelbuilder implements ContextBuilder<Object>{

	@Override
	public Context build(Context<Object> context) {
		ContinuousSpaceFactory spaceFact = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		
		ContinuousSpace<Object> space = spaceFact.createContinuousSpace("space", context, new SimpleCartesianAdder<Object>(), new StrictBorders(), 10, 10);

		for (int i = 0; i < 10; i++) {
			basestudent stud = new basestudent();
			context.add(stud);

			double x = RandomHelper.nextIntFromTo(0, 10); 
			double y = RandomHelper.nextIntFromTo(0, 10); 
			space.moveTo(stud, x, y);	
		}
		
		return context;
	}	

}
