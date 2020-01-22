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
	public Vector2d ap1 = null;
	public Vector2d ap2 = null;
	
	
	public Theke(int x, int y, int kind, ContinuousSpace s, int e) {
		this.x = x;
		this.y = y;
		this.kind = kind;
		this.space = s;
		this.essen = e;
		
		if (this.kind == consts.AKTIONSTHEKE) {
			size = new Vector2d(40,60);
			ap1 = new Vector2d(this.x-this.size.x/40, this.y+this.size.y/10);
		}
		else if (this.kind == consts.FLEISCHTHEKE) {
			size = new Vector2d(180, 20);
			ap1 = new Vector2d(this.x-3, this.y+this.size.y/20);
		}
		else if (this.kind == consts.SALATBAR) {
			size = new Vector2d(60,50);
			ap1 = new Vector2d(this.x-1, this.y+this.size.y/10);
			ap2 = new Vector2d(this.x-1, this.y+this.size.y/10-11);
		}
		else if (this.kind == consts.VEGGIETHEKE) {
			size = new Vector2d(40,80);
			//ap1 = new Vector2d(this.x-this.size.x/40, this.y+this.size.y/10);
		}
		else if (this.kind == consts.VEGANTHEKE) {
			size = new Vector2d(40,80);
			//ap1 = new Vector2d(this.x-this.size.x/40, this.y+this.size.y/10);
		}
		else if (this.kind == consts.EINTOPF) {
			size = new Vector2d(40,80);
			//ap1 = new Vector2d(this.x-this.size.x/40, this.y+this.size.y/10);
		}
		else if (this.kind == consts.POMMES) {
			size = new Vector2d(40,80);
			//ap1 = new Vector2d(this.x-this.size.x/40, this.y+this.size.y/10);
		}
		else {
			size = new Vector2d(10,10);
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
