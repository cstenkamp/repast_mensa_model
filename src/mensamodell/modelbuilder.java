package mensamodell;

import java.util.ArrayList;
import java.util.List;

import food_objs.Food;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.SimpleCartesianAdder;
import repast.simphony.space.continuous.StrictBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StickyBorders;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.schedule.ISchedulableAction;


public class modelbuilder extends DefaultContext implements ContextBuilder<Object>{


	static boolean USE_GRID = true;
	
	SharedStuff sharedstuff;
	@Override
	public Context<Object> build(Context<Object> context) {
		
		// ======================== get parameters from the GUI ========================
		
		Parameters param = RunEnvironment.getInstance().getParameters();
		// save the parameters in variables
		int initialNumStud = (Integer) param.getValue("initialNumStud");
		double veggieProp = (Double) param.getValue("veggieProp");
		double veganProp = (Double) param.getValue("veganProp");
		double meatProp = (Double) param.getValue("meatProp");
		double noPrefProp = 1.0 - veggieProp - veganProp - meatProp; 
		if (noPrefProp < 0) { //dann ist die Summe der 3 anderen >1, kann in batch run parameter sweeps passieren
    	System.out.println("Bad Parameter combination.");
    	RunEnvironment.getInstance().endRun();
		}
		
	
		double chaoticProp = (Double) param.getValue("chaoticProp");
		double goalProp = (Double) param.getValue("goalProp");
		double pathProp = 1.0 - chaoticProp - goalProp; 
		if (pathProp < 0) { //dann ist die Summe der 3 anderen >1, kann in batch run parameter sweeps passieren
    	System.out.println("Bad Parameter combination.");
    	RunEnvironment.getInstance().endRun();
		}
		
		Double[] proportionsEat = new Double[] {veggieProp, veganProp, meatProp, noPrefProp};
		Double[] proportionsWalk = new Double[] {chaoticProp, goalProp, pathProp};
		
		// BATCH RUN PARAMETERS:
		// VEGGIE
		double vg_vg = (Double) param.getValue("vg_vg");
		double vg_ve = (Double) param.getValue("vg_ve");
		double vg_sa = (Double) param.getValue("vg_sa");
		double vg_po = (Double) param.getValue("vg_po");
		// VEGAN
		double ve_ve = (Double) param.getValue("ve_ve");
		double ve_po = (Double) param.getValue("ve_po");
		double ve_sa = (Double) param.getValue("ve_sa");
		// MEAT
		double m_ve = (Double) param.getValue("m_ve");
		double m_vg = (Double) param.getValue("m_vg");
		double m_m = (Double) param.getValue("m_m");
		double m_sa = (Double) param.getValue("m_sa");
		double m_po = (Double) param.getValue("m_po");
		
		double[] foodParam = new double[] {vg_vg, vg_ve, vg_sa, vg_po, ve_ve, ve_po, ve_sa, m_ve, m_vg, m_m, m_sa, m_po};
		
		

		consts.SIZE_X = (int) param.getValue("size_x");
		consts.SIZE_Y = (int) param.getValue("size_y");
		
		
		// Aktionstheke ist zufaellig vegan, veggie, salad oder meat 
		int aktionsessen = RandomHelper.nextIntFromTo(0, 2);
		switch (aktionsessen) {
			case consts.ESSEN_VEGGIE: consts.print("Aktionstheke ist Vegetarisch."); break;
			case consts.ESSEN_VEGAN: consts.print("Aktionstheke ist Vegan."); break;
			case consts.ESSEN_MEAT: consts.print("Aktionstheke ist Fleisch."); break;
			default: consts.print("Irgendwas ist mit der Aktionstheke falsch.");
		}
		int eintopfessen = RandomHelper.nextIntFromTo(0, 2);
		switch (eintopfessen) {
			case consts.ESSEN_VEGGIE: consts.print("Eintopf ist Vegetarisch."); break;
			case consts.ESSEN_VEGAN: consts.print("Eintopf ist Vegan."); break;
			case consts.ESSEN_MEAT: consts.print("Eintopf ist Fleisch."); break;
			default: consts.print("Irgendwas ist mit der Eintopf falsch.");
		}
		int schnellesessen = RandomHelper.nextIntFromTo(0, 2);
		switch (schnellesessen) {
			case consts.ESSEN_VEGGIE: consts.print("Schneller Teller ist Vegetarisch."); break;
			case consts.ESSEN_VEGAN: consts.print("Schneller Teller ist Vegan."); break;
			case consts.ESSEN_MEAT: consts.print("Schneller Teller ist Fleisch."); break;
			default: consts.print("Irgendwas ist mit der Schnellen Teller falsch.");
		}

		ContinuousSpace<Object> space = null;
		MensaGrid mgrid = null;
		Grid<Object> grid = null;
		Ausgabe aktion = null;
		Ausgabe salat = null;
		Ausgabe pommes = null;
		if (USE_GRID) {
				consts.EINGANG_DELAY = 10;
			
				GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);	
				grid = gridFactory.createGrid("MensaGrid", context, new GridBuilderParameters<Object>(new StickyBorders(), new SimpleGridAdder<Object>(), false, consts.SIZE_X, consts.SIZE_Y));
				
				//der konstruktor added die jeweils zum Kontext
				new Kasse(consts.SIZE_X/6, consts.SIZE_Y-1, context, grid);
				new Kasse(consts.SIZE_X*5/6, consts.SIZE_Y-1, context, grid);
				aktion = new Ausgabe(consts.SIZE_X/2, 0, consts.AKTIONSTHEKE, aktionsessen, context, grid);
				salat = new Ausgabe(consts.SIZE_X*1/10, 0, consts.SALATBAR, consts.ESSEN_SALAD, context, grid);
				new Ausgabe(consts.SIZE_X*2/10, 0, consts.VEGANTHEKE, consts.ESSEN_VEGAN, context, grid);
				new Ausgabe(consts.SIZE_X*3/10, 0, consts.VEGGIETHEKE, consts.ESSEN_VEGGIE, context, grid);
				new Ausgabe(consts.SIZE_X*4/10, 0, consts.FLEISCHTHEKE, consts.ESSEN_MEAT, context, grid);
				new Ausgabe(consts.SIZE_X*6/10, 0, consts.FLEISCHTHEKE, consts.ESSEN_MEAT, context, grid);
				new Ausgabe(consts.SIZE_X*7/10, 0, consts.SCHNELLERTELLER, schnellesessen, context, grid);
				new Ausgabe(consts.SIZE_X*8/10, 0, consts.EINTOPF, eintopfessen, context, grid);
				pommes = new Ausgabe(consts.SIZE_X*9/10, 0, consts.POMMES, consts.ESSEN_POMMES, context, grid);
			
		} else {
				consts.EINGANG_DELAY = 1000;
				
				ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
				space = spaceFactory.createContinuousSpace("space", context, new SimpleCartesianAdder<Object>(), new StrictBorders(), consts.SIZE_X, consts.SIZE_Y);
		
				// Theken 
				addThekeAusgabe(consts.SIZE_X/2, 10, 0, 6, consts.AKTIONSTHEKE, space, aktionsessen, context);
				for (double i : new double[]{-consts.SIZE_X/6.0, consts.SIZE_X/5.0})
						addThekeAusgabe((int)(consts.SIZE_X/2.0+i)-1, 5, 0, 2, consts.FLEISCHTHEKE, space, consts.ESSEN_MEAT, context); //bei 34,5 & 66,5 | size 50,20
				for (double i : new double[]{-consts.SIZE_X/5.0, consts.SIZE_X/5.0}) {
					addThekeAusgabe((int)(consts.SIZE_X/2.0+i), consts.SIZE_Y/2, 0, 5, consts.SALATBAR, space, consts.ESSEN_SALAD, context);
					new Ausgabe((int)(consts.SIZE_X/2.0+i), consts.SIZE_Y/2-5, consts.FLEISCHTHEKE, consts.ESSEN_MEAT, context, space);
				}
				addThekeAusgabe(12, consts.SIZE_Y*1/4, 4, 0, consts.VEGGIETHEKE, space, consts.ESSEN_VEGGIE, context);
				addThekeAusgabe(12, consts.SIZE_Y*2/4, 4, 0, consts.VEGANTHEKE, space, consts.ESSEN_VEGAN, context);
				addThekeAusgabe(consts.SIZE_X-12, consts.SIZE_Y*1/4, -4, 0, consts.POMMES, space, consts.ESSEN_POMMES, context);
				// Eintopf ist zufaellig vegan, veggie oder meat
				addThekeAusgabe(consts.SIZE_X-12, consts.SIZE_Y*2/4, -4, 0, consts.EINTOPF, space, RandomHelper.nextIntFromTo(0, 2), context);
		
				// Kassen
				new Kasse(consts.SIZE_X*1/4, consts.SIZE_Y-5, context, space);
				new Kasse(consts.SIZE_X*3/4, consts.SIZE_Y-5, context, space);
			
				mgrid = new MensaGrid(consts.SIZE_X, consts.SIZE_Y);
				for (Object obj: context.getObjects(Object.class))
					mgrid.setObj(obj);
		}
		
		//get all positions of theken and eingaenge here and save them, such that students only need to loop over them instead of all elems
		List<Kasse> kassen = new ArrayList<Kasse>();
		for (Object ks: context.getObjects(Kasse.class)) {
			kassen.add((Kasse) ks);
		}
		List<Ausgabe> ausgaben = new ArrayList<Ausgabe>();
		for (Object th: context.getObjects(Ausgabe.class)) {
			if (!kassen.contains(th))
				ausgaben.add((Ausgabe) th);
		}
		
		// ##########################################################
		// Speicher die Essen fuer jede Pref in der jeweiligen Liste
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
		

		Context<Food> foodContext = new DefaultContext<>("foodContext");
		context.addSubContext(foodContext);
		
		// #########################################################

		MensaEingang eingang;
		if (USE_GRID)  {
			sharedstuff = new SharedStuff(this, context, kassen, ausgaben, foodContext, grid, null, foodParam);
			eingang = new MensaEingang((int)consts.SIZE_X/2, (int)consts.SIZE_Y-1, initialNumStud, proportionsEat, proportionsWalk, context, sharedstuff, grid, aktion);
			sharedstuff.pommesbar = pommes;
			sharedstuff.salatbar = salat;
			sharedstuff.initialNumStud = initialNumStud;
		} else {
			sharedstuff = new SharedStuff(this, context, kassen, ausgaben, foodContext, space, mgrid, foodParam);
			eingang = new MensaEingang((float)(consts.SIZE_X*2.5/5), (float)(consts.SIZE_Y-5), initialNumStud, proportionsEat, proportionsWalk, context, space, sharedstuff); 
			//TODO darauf achten dass man immer 100 studenten drin hat bspw
		}	
 
		context.add(eingang);
		sharedstuff.schedule.schedule(ScheduleParameters.createRepeating(1, consts.EINGANG_DELAY), eingang, "step");


    
		return context;
	} // END of Context.

	
	public void addThekeAusgabe(int theke_x, int theke_y, int ausgabe_x_diff, int ausgabe_y_diff, int kind, ContinuousSpace s, int e, Context context) {
		Ausgabe ausgabe = new Ausgabe(theke_x+ausgabe_x_diff, theke_y+ausgabe_y_diff, kind, e, context, s);
		Theke theke = new Theke(theke_x, theke_y, kind, s);
		context.add(theke);
		s.moveTo(theke, theke.x, theke.y);
	}
	
	
  public void remove_studs() {
    for (Student s : sharedstuff.remove_these) {
    	for (ISchedulableAction a : s.scheduledSteps) {
    		sharedstuff.schedule.removeAction(a);
    	}
    	s.scheduledSteps = new ArrayList<ISchedulableAction>();
    }
    sharedstuff.remove_these = new ArrayList<Student>();
    
    if (sharedstuff.students_that_left.size() ==  sharedstuff.initialNumStud) {
    	System.out.println("run ended after "+sharedstuff.schedule.getTickCount()+" ticks.");
    	RunEnvironment.getInstance().endRun();
    }
    	
  }


} // END of modelbuilder.
