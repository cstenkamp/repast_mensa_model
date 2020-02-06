package mensamodell;

import java.util.List;

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
	public Context<Food> foodContext;
	public double[] foodParam;
	
	public List<Student> studierende;
	public List<Student> studierendeChaotic;
	public List<Student> studierendeGoal;		// DATA
	public List<Student> studierendeQueue;
	
	public List<Student> remove_these;

	public ContinuousSpace<Object> space;
	public Grid<Object> grid;

	
	private void auslager_constr(ContextBuilder<Object> builder, Context<Object> context, List<Kasse> kassen, List<Ausgabe> ausgaben, Context<Food> foodContext) {
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
		remove_these = new ArrayList<Student>();
	}
	
	
	public SharedStuff(ContextBuilder<Object> builder, Context<Object> context, List<Kasse> kassen, List<Ausgabe> ausgaben, Context<Food> foodContext, ContinuousSpace<Object> space, MensaGrid mgrid, double[] fParam) {
		auslager_constr(builder, context, kassen, ausgaben, foodContext);
		this.space = space;
		this.mgrid = mgrid;
		this.foodParam = fParam;
	}
	
	public SharedStuff(ContextBuilder<Object> builder, Context<Object> context, List<Kasse> kassen, List<Ausgabe> ausgaben, Context<Food> foodContext, Grid<Object> grid, MensaGrid mgrid, double[] fParam) {
		auslager_constr(builder, context, kassen, ausgaben, foodContext);
		this.mgrid = mgrid;
		this.foodParam = fParam;
	}
	
}
