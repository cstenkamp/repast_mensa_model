package mensamodell;

import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;

import java.util.ArrayList; 


public class SharedStuff {

	public Context<Object> context;
	public ContinuousSpace<Object> space;
	public List<Kasse> kassen;
	public List<Theke> theken;
	public MensaGrid grid;

	public SharedStuff(Context<Object> context, ContinuousSpace<Object> space, List<Kasse> kassen, List<Theke> theken,
			MensaGrid grid) {
		this.context = context;
		this.space = space;
		this.kassen = kassen;
		this.theken = theken;
		this.grid = grid;
		
	}
	
}
