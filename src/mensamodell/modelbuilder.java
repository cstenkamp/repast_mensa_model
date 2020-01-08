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
		Theke aktionstheke = new Theke(consts.SIZE_X/2, 5, consts.AKTIONSTHEKE);
		context.add(aktionstheke);
		space.moveTo(aktionstheke, aktionstheke.x, aktionstheke.y);
		
		// NEUE DUMMY THEKEN ZUM TESTEN:
		Theke kasseL = new Theke(consts.SIZE_X*1/4, consts.SIZE_Y-5, consts.KASSE);
		context.add(kasseL);
		space.moveTo(kasseL, kasseL.x, kasseL.y);
		Theke kasseR = new Theke(consts.SIZE_X*3/4, consts.SIZE_Y-5, consts.KASSE);
		context.add(kasseR);
		space.moveTo(kasseR, kasseR.x, kasseR.y);
		
		Theke fleischL = new Theke(consts.SIZE_X*1/4, 5, consts.FLEISCH);
		context.add(fleischL);
		space.moveTo(fleischL, fleischL.x, fleischL.y);
		Theke fleischR = new Theke(consts.SIZE_X*3/4, 5, consts.FLEISCH);
		context.add(fleischR);
		space.moveTo(fleischR, fleischR.x, fleischR.y);
		Theke salatbarL = new Theke(consts.SIZE_X*1/4, consts.SIZE_Y/2, consts.SALATBAR);
		context.add(salatbarL);
		space.moveTo(salatbarL, salatbarL.x, salatbarL.y);
		Theke salatbar = new Theke(consts.SIZE_X*3/4, consts.SIZE_Y/2, consts.SALATBAR);
		context.add(salatbar);
		space.moveTo(salatbar, salatbar.x, salatbar.y);
		Theke veggie = new Theke(5, consts.SIZE_Y*1/4, consts.VEGGIE);
		context.add(veggie);
		space.moveTo(veggie, veggie.x, veggie.y);
		// ENDE DUMMY THEKEN 
		

		// add students to context
		for (int i = 0; i < initialNumStud; i++) {
			Student stud = new Student(space);	// add new students
			context.add(stud);	// add the new students to the root context
			space.moveTo(stud, (double)consts.SIZE_X/2, (double)consts.SIZE_Y-5); // add students to space
		}

		return context;
	} // END of Context.

} // END of modelbuilder.
