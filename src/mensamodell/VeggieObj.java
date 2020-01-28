package mensamodell;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;

public class VeggieObj extends Food{
	public VeggieObj(Context c, ContinuousSpace s) {
		c.add(this);
		s.moveTo(this, 0,0);
	}

}
