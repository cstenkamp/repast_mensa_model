package food_objs;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;

public class VeganObj extends Food{
	public VeganObj(Context c, ContinuousSpace s) {
		c.add(this);
		s.moveTo(this,0,0);
	}
}
