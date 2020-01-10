package mensamodell;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.*;

import javax.media.j3d.Shape3D;



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

		// Theken
		Theke aktionstheke = new Theke(consts.SIZE_X/2, 10, consts.AKTIONSTHEKE, space);
		context.add(aktionstheke);
		space.moveTo(aktionstheke, aktionstheke.x, aktionstheke.y);

		for (double i : new double[]{-consts.SIZE_X/6.0, consts.SIZE_X/6.0}) {
				Theke fleisch = new Theke((int)(consts.SIZE_X/2.0+i), 5, consts.FLEISCHTHEKE, space);
				context.add(fleisch);
				space.moveTo(fleisch, fleisch.x, fleisch.y);
		}
		for (double i : new double[]{-consts.SIZE_X/5.0, consts.SIZE_X/5.0}) {
			Theke salatbar = new Theke((int)(consts.SIZE_X/2.0+i), consts.SIZE_Y/2, consts.SALATBAR, space);
			context.add(salatbar);
			space.moveTo(salatbar, salatbar.x, salatbar.y);
		}

		Theke veggie = new Theke(5, consts.SIZE_Y*1/4, consts.VEGGIETHEKE, space);
		context.add(veggie);
		space.moveTo(veggie, veggie.x, veggie.y);

		// Kassen
		Kasse kasseL = new Kasse(consts.SIZE_X*1/4, consts.SIZE_Y-5, space);
		context.add(kasseL);
		space.moveTo(kasseL, kasseL.x, kasseL.y);
		Kasse kasseR = new Kasse(consts.SIZE_X*3/4, consts.SIZE_Y-5, space);
		context.add(kasseR);
		space.moveTo(kasseR, kasseR.x, kasseR.y);

		MensaEingang eingang = new MensaEingang(initialNumStud, context, space); //TODO darauf achten dass man immer 100 studenten drin hat bspw
		context.add(eingang);
		space.moveTo(eingang, consts.SIZE_X*2.5/5,consts.SIZE_Y-5);
		
		return context;
	} // END of Context.




} // END of modelbuilder.
