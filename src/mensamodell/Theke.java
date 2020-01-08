package mensamodell;

import mensamodell.consts.*;

//student laeuft rum, sieht theke, called enqueueInTheke, ab dann geht er in jedem zeitschritt ggf nen schlangenplatz vor
//wenn er die theke sieht sieht er aber auch die länge der schlange und reiht sich nur ggf ein

public class Theke {
	int x;
	int y;
	public int kind;
	boolean visited;
	
	
	public Theke(int x, int y, int kind) {
		this.x = x;
		this.y = y;
		this.kind = kind;
		this.visited = false;
	}
	
	public void setVisit() {
		this.visited = true;
	}
	
	public boolean isLeft() {
		return x > consts.SIZE_X/2;
	}
	

	
	
	
	
} // END of Class
