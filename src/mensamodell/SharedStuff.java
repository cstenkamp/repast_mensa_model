package mensamodell;

import java.util.List;
import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.space.continuous.ContinuousSpace; 


public class SharedStuff {

	public Context<Object> context;
	public ContinuousSpace<Object> space;
	public List<Kasse> kassen;
	public List<Ausgabe> ausgaben;
	public MensaGrid grid;
	public ISchedule schedule = null;
	public ContextBuilder<Object> builder = null;
	
	public List<Student> studierende;
	public List<Student> remove_these;

	public SharedStuff(ContextBuilder<Object> builder, Context<Object> context, ContinuousSpace<Object> space, List<Kasse> kassen, List<Ausgabe> ausgaben,
			MensaGrid grid) {
		this.context = context;
		this.space = space;
		this.kassen = kassen;
		this.ausgaben = ausgaben;
		this.grid = grid;
		this.builder = builder;
		studierende = new ArrayList<Student>();
		remove_these = new ArrayList<Student>();
	}
	
}
