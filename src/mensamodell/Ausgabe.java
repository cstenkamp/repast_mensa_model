package mensamodell;

import javax.vecmath.Vector2d;

import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;

//student laeuft rum, sieht theke, called enqueueInTheke, ab dann geht er in jedem zeitschritt ggf nen schlangenplatz vor
//wenn er die theke sieht sieht er aber auch die länge der schlange und reiht sich nur ggf ein

public class Ausgabe {
	int x;
	int y;
	public int kind;
	public Vector2d size;
	private ContinuousSpace space;
	private double barRange = 5;
	private int essen;
	public Vector2d ap1 = null;
	public Vector2d ap2 = null;
	public int wait_time = 0;
	
	
	public Ausgabe(int x, int y, int kind, ContinuousSpace s, int e) {
		this.x = x;
		this.y = y;
		this.kind = kind;
		this.space = s;
		this.essen = e;
		
		switch (kind) {
			case consts.SALATBAR:
				wait_time = 20000;
				break;
			case consts.AKTIONSTHEKE:
				wait_time = 12000;
				break;
			case consts.POMMES:
				wait_time = 12000;
				break;
			default:
				wait_time = 10000;
		}
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
	
	public int getEssen() {
		return this.essen;
	}
	
	
	

	
	
	
	
} // END of Class
