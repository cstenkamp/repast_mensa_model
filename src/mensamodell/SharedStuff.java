package mensamodell;

import java.util.List;
import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace; 


public class SharedStuff {

	public Context<Object> context;
	public ContinuousSpace<Object> space;
	public List<Kasse> kassen;
	public List<Ausgabe> ausgaben;
	public MensaGrid grid;
	
	public List<Student> studierende;

	public SharedStuff(Context<Object> context, ContinuousSpace<Object> space, List<Kasse> kassen, List<Ausgabe> ausgaben,
			MensaGrid grid) {
		this.context = context;
		this.space = space;
		this.kassen = kassen;
		this.ausgaben = ausgaben;
		this.grid = grid;
		studierende = new ArrayList<Student>();
	}
	
}
