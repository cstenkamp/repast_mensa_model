package mensamodell;

import mensamodell.consts.*;

//student läuft rum, sieht theke, called enqueueInTheke, ab dann geht er in jedem zeitschritt ggf nen schlangenplatz vor
//wenn er die theke sieht sieht er aber auch die länge der schlange und reiht sich nur ggf ein

public class Theke {
	int x;
	int y;
	int kind;
	
	public Theke(int x, int y, int kind) {
		this.x = x;
		this.y = y;
		this.kind = kind;
	}
	
}
