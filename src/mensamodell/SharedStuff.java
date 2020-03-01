package mensamodell;

import java.util.List;

import data_objs.MeanTimeCalulator_Vegan;
import food_objs.Food;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid; 


public class SharedStuff {

	public Context<Object> context;
	public List<Kasse> kassen;
	public List<Ausgabe> ausgaben;
	public MensaGrid mgrid;
	public ISchedule schedule = null;
	public ContextBuilder<Object> builder = null;
	public Context<Object> foodContext;
	public double[] foodParam;

	public List<Student> studierende;
	public List<Student> studierendeChaotic;
	public List<Student> studierendeGoal;		// DATA
	public List<Student> studierendeQueue;

	public List<Student> studierendeVegan;
	public List<Student> studierendeMeatEater;		// DATA
	public List<Student> studierendeVeggie;
	public List<Student> studierendeNoPref;
	
	public List<Student> remove_these;
	public List<Student> students_that_left = null;
	public int initialNumStud = 0;
	
	
	public ContinuousSpace<Object> space;
	public Grid<Object> grid;
	public Ausgabe pommesbar = null;
	public Ausgabe salatbar = null;
	

	
	private void auslager_constr(ContextBuilder<Object> builder, Context<Object> context, List<Kasse> kassen, List<Ausgabe> ausgaben, Context<Object> foodContext) {
		this.context = context;
		this.foodContext = foodContext;
		this.kassen = kassen;
		this.ausgaben = ausgaben;
		this.builder = builder;
		schedule = RunEnvironment.getInstance().getCurrentSchedule(); //schedule.schedule(ScheduleParameters.createOneTime(1.0), new IAction() { public void execute() {} });

		studierende = new ArrayList<Student>();			// DATA
		studierendeChaotic = new ArrayList<Student>();
		studierendeGoal = new ArrayList<Student>();
		studierendeQueue = new ArrayList<Student>();
		studierendeVegan = new ArrayList<Student>();			// DATA
		studierendeVeggie = new ArrayList<Student>();
		studierendeMeatEater = new ArrayList<Student>();
		studierendeNoPref = new ArrayList<Student>();

		remove_these = new ArrayList<Student>();
		students_that_left = new ArrayList<Student>();
	}
	
	
	public SharedStuff(ContextBuilder<Object> builder, Context<Object> context, List<Kasse> kassen, List<Ausgabe> ausgaben, Context<Object> foodContext, ContinuousSpace<Object> space, MensaGrid mgrid, double[] fParam) {
		auslager_constr(builder, context, kassen, ausgaben, foodContext);
		this.space = space;
		this.mgrid = mgrid;
		this.foodParam = fParam;
	}
	
	public SharedStuff(ContextBuilder<Object> builder, Context<Object> context, List<Kasse> kassen, List<Ausgabe> ausgaben, Context<Object> foodContext, Grid<Object> grid, MensaGrid mgrid, double[] fParam) {
		auslager_constr(builder, context, kassen, ausgaben, foodContext);
		this.mgrid = mgrid;
		this.foodParam = fParam;
	}
	
}
