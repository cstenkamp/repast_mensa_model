package mensamodell;


import javax.vecmath.Vector2d;

import repast.simphony.space.continuous.ContinuousSpace;

// die Klasse "Theke" ist nur relevant für das 2D-Space Modell (!=Grid), welches nicht präsentiert wurde.
// Eine Theke hat lediglich eine räumliche Ausdehnung und soll ausschließlich als Lauf-Blockade für die Studenten dienen.

public class Theke {
	int x;
	int y;
	public int kind;
	public Vector2d size;
	private ContinuousSpace space;
	private double barRange = 5;
	private int essen;
	
	
	public Theke(int x, int y, int kind, ContinuousSpace s) {
		this.x = x;
		this.y = y;
		this.kind = kind;
		this.space = s;
		
		if (this.kind == consts.AKTIONSTHEKE) {
			size = new Vector2d(40,60);
		}
		else if (this.kind == consts.FLEISCHTHEKE) {
			size = new Vector2d(180, 20);
		}
		else if (this.kind == consts.SALATBAR) {
			size = new Vector2d(60,50);
		}
		else if (this.kind == consts.VEGGIETHEKE) {
			size = new Vector2d(40,80);
		}
		else if (this.kind == consts.VEGANTHEKE) {
			size = new Vector2d(40,80);
		}
		else if (this.kind == consts.EINTOPF) {
			size = new Vector2d(40,80);
		}
		else if (this.kind == consts.POMMES) {
			size = new Vector2d(40,80);
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

	
	
} // END of Class
