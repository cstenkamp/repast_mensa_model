package mensamodell;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.SimpleCartesianAdder;
import repast.simphony.space.continuous.StrictBorders; 


public class modelbuilder implements ContextBuilder<Object>{

	@Override
	public Context<Object> build(Context<Object> context) {

		// get parameters from the GUI
		Parameters param = RunEnvironment.getInstance().getParameters();
		// save the parameters in variables
		int initialNumStud = (Integer) param.getValue("initialNumStud");
		double veggieProp = (Double) param.getValue("veggieProp");
		double veganProp = (Double) param.getValue("veganProp");
		double meatProp = (Double) param.getValue("meatProp");
		double noPrefProp = (Double) param.getValue("noPrefProp");
		Object[] proportions = new Object[] {veggieProp, veganProp, meatProp, noPrefProp};

		// create ContinuousSpace, size: 100x60
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context, new SimpleCartesianAdder<Object>(), new StrictBorders(), consts.SIZE_X, consts.SIZE_Y);

		
		// Theken ##############################################
		// AKT ist zufaellig vegan, veggie, salad oder meat 
		addThekeAusgabe(consts.SIZE_X/2, 10, 0, 6, consts.AKTIONSTHEKE, space, RandomHelper.nextIntFromTo(0, 3), context);
		
		for (double i : new double[]{-consts.SIZE_X/6.0, consts.SIZE_X/5.0}) {
				addThekeAusgabe((int)(consts.SIZE_X/2.0+i)-1, 5, 0, 2, consts.FLEISCHTHEKE, space, consts.ESSEN_MEAT, context); //bei 34,5 & 66,5 | size 50,20
				//TODO die Theken müssen unterschiedlich lange bearbeitungszeit haben -> salatbar läänger
		}
		for (double i : new double[]{-consts.SIZE_X/5.0, consts.SIZE_X/5.0}) {
			addThekeAusgabe((int)(consts.SIZE_X/2.0+i), consts.SIZE_Y/2, 0, 5, consts.SALATBAR, space, consts.ESSEN_SALAD, context);				
			addAusgabe((int)(consts.SIZE_X/2.0+i), consts.SIZE_Y/2-5, consts.FLEISCHTHEKE, space, consts.ESSEN_MEAT, context);

		}
		addThekeAusgabe(12, consts.SIZE_Y*1/4, 4, 0, consts.VEGGIETHEKE, space, consts.ESSEN_VEGGIE, context);
		addThekeAusgabe(12, consts.SIZE_Y*2/4, 4, 0, consts.VEGANTHEKE, space, consts.ESSEN_VEGAN, context);
		
		// Eintopf ist zufaellig vegan, veggie oder meat
		addThekeAusgabe(consts.SIZE_X-12, consts.SIZE_Y*2/4, -4, 0, consts.EINTOPF, space, RandomHelper.nextIntFromTo(0, 2), context);
		addThekeAusgabe(consts.SIZE_X-12, consts.SIZE_Y*1/4, -4, 0, consts.POMMES, space, consts.ESSEN_POMMES, context);

		// Kassen #################################################
		Kasse kasseL = new Kasse(consts.SIZE_X*1/4, consts.SIZE_Y-5, space);
		context.add(kasseL);
		space.moveTo(kasseL, kasseL.x, kasseL.y);
		Kasse kasseR = new Kasse(consts.SIZE_X*3/4, consts.SIZE_Y-5, space);
		context.add(kasseR);
		space.moveTo(kasseR, kasseR.x, kasseR.y);

		//get all positions of theken and eingaenge here and save them, such that students only need to loop over them instead of all elems
		List<Kasse> kassen = new ArrayList<Kasse>();
		for (Object ks: context.getObjects(Kasse.class)) {
			kassen.add((Kasse) ks);
		}
		List<Ausgabe> ausgaben = new ArrayList<Ausgabe>();
		for (Object th: context.getObjects(Ausgabe.class)) {
			ausgaben.add((Ausgabe) th);
		}
		// ##########################################################
		// Speicher die Essen für jede Pref in der jeweiligen Liste
		consts.vegetarian.add(consts.ESSEN_VEGGIE);
		consts.vegetarian.add(consts.ESSEN_VEGAN);
		consts.vegetarian.add(consts.ESSEN_SALAD);
		consts.vegetarian.add(consts.ESSEN_POMMES);
		consts.vegan.add(consts.ESSEN_VEGAN);
		consts.vegan.add(consts.ESSEN_SALAD);
		consts.vegan.add(consts.ESSEN_POMMES);
		consts.meatlover.add(consts.ESSEN_VEGGIE);
		consts.meatlover.add(consts.ESSEN_VEGAN);
		consts.meatlover.add(consts.ESSEN_SALAD);
		consts.meatlover.add(consts.ESSEN_POMMES);
		consts.meatlover.add(consts.ESSEN_MEAT);
		consts.noPref.add(consts.ESSEN_VEGGIE);
		consts.noPref.add(consts.ESSEN_VEGAN);
		consts.noPref.add(consts.ESSEN_SALAD);
		consts.noPref.add(consts.ESSEN_POMMES);
		consts.noPref.add(consts.ESSEN_MEAT);
		// #########################################################
		
		MensaGrid grid = new MensaGrid(consts.SIZE_X, consts.SIZE_Y);
				
		for (Object obj: context.getObjects(Object.class))
			grid.setObj(obj);
//		grid.print();

		SharedStuff sharedstuff = new SharedStuff(context, space, kassen, ausgaben, grid);
		MensaEingang eingang = new MensaEingang(initialNumStud, proportions, context, space, sharedstuff); //TODO darauf achten dass man immer 100 studenten drin hat bspw
		context.add(eingang);
		space.moveTo(eingang, consts.SIZE_X*2.5/5,consts.SIZE_Y-5);
		
		
		return context;
	} // END of Context.

	
	public void addThekeAusgabe(int theke_x, int theke_y, int ausgabe_x_diff, int ausgabe_y_diff, int kind, ContinuousSpace s, int e, Context context) {
		Ausgabe ausgabe = new Ausgabe(theke_x+ausgabe_x_diff, theke_y+ausgabe_y_diff, kind, s, e);
		Theke theke = new Theke(theke_x, theke_y, kind, s);
		context.add(theke);
		s.moveTo(theke, theke.x, theke.y);
		context.add(ausgabe);
		s.moveTo(ausgabe, ausgabe.x, ausgabe.y);
	}
	
	public void addAusgabe(int x, int y, int kind, ContinuousSpace s, int e, Context context) {
		Ausgabe ausgabe = new Ausgabe(x, y, kind, s, e);
		context.add(ausgabe);
		s.moveTo(ausgabe, ausgabe.x, ausgabe.y);
	}



} // END of modelbuilder.
