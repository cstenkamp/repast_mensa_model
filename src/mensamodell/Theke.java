package mensamodell;

import mensamodell.consts.*;
import javax.vecmath.Vector2d;

//student laeuft rum, sieht theke, called enqueueInTheke, ab dann geht er in jedem zeitschritt ggf nen schlangenplatz vor
//wenn er die theke sieht sieht er aber auch die länge der schlange und reiht sich nur ggf ein

public class Theke {
	int x;
	int y;
	public int kind;
	boolean visited;
	public Vector2d size;
	
	
	public Theke(int x, int y, int kind) {
		this.x = x;
		this.y = y;
		this.kind = kind;
		this.visited = false;
		
		if (this.kind == consts.AKTIONSTHEKE) 
			size = new Vector2d(40,100);
		else if (this.kind == consts.FLEISCHTHEKE) 
			size = new Vector2d(50, 20);
		else if (this.kind == consts.SALATBAR) 
			size = new Vector2d(50,60);
		else
			size = null;
		
	}
	
	public void setVisit() {
		this.visited = true;
	}
	
	public boolean isLeft() {
		return x > consts.SIZE_X/2;
	}
	

	
	
	
	
} // END of Class
