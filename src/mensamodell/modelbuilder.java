package mensamodell;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.*;

import java.util.List; 
import java.util.ArrayList; 


public class modelbuilder implements ContextBuilder<Object>{

	@Override
	public Context<Object> build(Context<Object> context) {

		// get parameters from the GUI
		Parameters param = RunEnvironment.getInstance().getParameters();
		// save the parameters in variables
		int initialNumStud = (Integer)param.getValue("initialNumStud");

		// create ContinuousSpace, size: 100x60
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context, new SimpleCartesianAdder<Object>(), new StrictBorders(), consts.SIZE_X, consts.SIZE_Y);

		
		// Theken ##############################################
		// AKT ist zufällig vegan, veggie, salad oder meat 
		Theke aktionstheke = new Theke(consts.SIZE_X/2, 10, consts.AKTIONSTHEKE, space, RandomHelper.nextIntFromTo(0, 3));
		context.add(aktionstheke);
		space.moveTo(aktionstheke, aktionstheke.x, aktionstheke.y);

		for (double i : new double[]{-consts.SIZE_X/6.0, consts.SIZE_X/6.0}) {
				Theke fleisch = new Theke((int)(consts.SIZE_X/2.0+i), 5, consts.FLEISCHTHEKE, space, consts.ESSEN_MEAT); //bei 34,5 & 66,5 | size 50,20
				context.add(fleisch);
				space.moveTo(fleisch, fleisch.x, fleisch.y);
		}
		for (double i : new double[]{-consts.SIZE_X/5.0, consts.SIZE_X/5.0}) {
			Theke salatbar = new Theke((int)(consts.SIZE_X/2.0+i), consts.SIZE_Y/2, consts.SALATBAR, space, consts.ESSEN_SALAD);
			context.add(salatbar);
			space.moveTo(salatbar, salatbar.x, salatbar.y);
		}

		Theke veggie = new Theke(5, consts.SIZE_Y*1/4, consts.VEGGIETHEKE, space, consts.ESSEN_VEGGIE);
		context.add(veggie);
		space.moveTo(veggie, veggie.x, veggie.y);
		
		Theke vegan = new Theke(5, consts.SIZE_Y*2/4, consts.VEGANTHEKE, space, consts.ESSEN_VEGAN);
		context.add(vegan);
		space.moveTo(vegan, vegan.x, vegan.y);
		
		// Eintopf ist zufällig vegan, veggie oder meat
		Theke eintopf = new Theke(consts.SIZE_X-5, consts.SIZE_Y*2/4, consts.EINTOPF, space, RandomHelper.nextIntFromTo(0, 2));
		context.add(eintopf);
		space.moveTo(eintopf, eintopf.x, eintopf.y);
		
		Theke pommes = new Theke(consts.SIZE_X-5, consts.SIZE_Y*1/4, consts.POMMES, space, consts.ESSEN_POMMES);
		context.add(pommes);
		space.moveTo(pommes, pommes.x, pommes.y);

		// Kassen #################################################
		Kasse kasseL = new Kasse(consts.SIZE_X*1/4, consts.SIZE_Y-5, space);
		context.add(kasseL);
		space.moveTo(kasseL, kasseL.x, kasseL.y);
		Kasse kasseR = new Kasse(consts.SIZE_X*3/4, consts.SIZE_Y-5, space);
		context.add(kasseR);
		space.moveTo(kasseR, kasseR.x, kasseR.y);

		//get all positions of theken and eingänge here and save them, such that students only need to loop over them instead of all elems
		List<Kasse> kassen = new ArrayList<Kasse>();
		for (Object ks: context.getObjects(Kasse.class)) {
			kassen.add((Kasse) ks);
		}
		List<Theke> theken = new ArrayList<Theke>();
		for (Object th: context.getObjects(Theke.class)) {
			theken.add((Theke) th);
		}

		MensaGrid grid = new MensaGrid(consts.SIZE_X, consts.SIZE_Y);
				
		for (Object obj: context.getObjects(Object.class))
			grid.setObj(obj);
		grid.print();

		SharedStuff sharedstuff = new SharedStuff(context, space, kassen, theken, grid);
		MensaEingang eingang = new MensaEingang(initialNumStud, context, space, sharedstuff); //TODO darauf achten dass man immer 100 studenten drin hat bspw
		context.add(eingang);
		space.moveTo(eingang, consts.SIZE_X*2.5/5,consts.SIZE_Y-5);
		
		
		return context;
	} // END of Context.




} // END of modelbuilder.
