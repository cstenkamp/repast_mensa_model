package mensamodell;

import mensamodell.consts.*;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;

import javax.vecmath.Vector2d;

//student laeuft rum, sieht theke, called enqueueInTheke, ab dann geht er in jedem zeitschritt ggf nen schlangenplatz vor
//wenn er die theke sieht sieht er aber auch die länge der schlange und reiht sich nur ggf ein

public class Theke {
	int x;
	int y;
	public int kind;
	public Vector2d size;
	private ContinuousSpace space;
	private double barRange = 5;
	private int essen;
	
	
	public Theke(int x, int y, int kind, ContinuousSpace s, int e) {
		this.x = x;
		this.y = y;
		this.kind = kind;
		this.space = s;
		this.essen = e;
		
		if (this.kind == consts.AKTIONSTHEKE) 
			size = new Vector2d(40,60);
		else if (this.kind == consts.FLEISCHTHEKE) 
			size = new Vector2d(50, 20);
		else if (this.kind == consts.SALATBAR) 
			size = new Vector2d(50,60);
		else
			size = null;
	}
	
	public boolean isLeft() {
		return x > consts.SIZE_X/2;
	}
	
	//public Student lastInQueue() {
		
	//	return Student s;
	//}
	
	public boolean isEmpty() {
		ContinuousWithin StudentInBarRange = new ContinuousWithin(space, this, barRange);
		for (Object s : StudentInBarRange.query()) {
			if (s instanceof Student) return true;
		}
		return false;
	}
	
	
	

	
	
	
	
} // END of Class
