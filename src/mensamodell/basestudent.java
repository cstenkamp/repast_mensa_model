package mensamodell;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import javax.vecmath.Vector2d;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;



/*
 * Studenten Klasse. Der Student laeuft durch die Mensa und erkennt innerhalb seines Sichtradius verschiedene Theken anhand der Schlange und 
 * seines movement_pref entscheidet er wohin er laeuft.
 */

public class basestudent {
	
	//thekentreu hat suchradius unendlich: geht immer zur richtigen theke.
	
	public class Velocity {
		int x;
		int y;
	}
	
	private ContinuousSpace space;	// Der kontinuierliche Raum wird in dieser Variablen gespeichert.
	int food_preference; // 0=veggie, 1=vegan, 2=meat, 3=no_preference 
	int movement; // 0=chaotic, 1=goal oriented, 2=constant
	double vision;			// Sichtradius
	Velocity velocity;
	
	// choose randomly 	
	public basestudent(ContinuousSpace s) {
		this.space = s;
		this.food_preference = RandomHelper.nextIntFromTo(0, 3);
		this.movement = RandomHelper.nextIntFromTo(0, 2);
		this.velocity = new Velocity();
		if (this.movement == 0) {
			this.vision = 20; // chaotische besitzen einen kleineren Sichtradius
		}
		if (this.movement == 1) {
			this.vision = 100;
		} else {
			this.vision = 100;
		}
		
	}
	
	// choose only one preference
	public basestudent(ContinuousSpace s, int value, boolean food_pref) {
		
		this.space = s;
		// test
		// if foodpref: value in 0,1,2,3, else in 0,1,2
		
		if(food_pref) {
			this.food_preference = value;
			this.movement = RandomHelper.nextIntFromTo(0, 2);
		}else {
			this.food_preference = RandomHelper.nextIntFromTo(0, 3);
			this.movement = value;
		}
	}
	
	// initialise both preferences
	public basestudent(ContinuousSpace s, int food_pref, int move_pref) {
		this.space = s;
		this.food_preference = food_pref;
		this.movement = move_pref; 
	}
	
		
	/**
	 * Methode wird jede Runde ausgefuehrt.
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		
		public double[] destination() {
			
			double koord[] = new double[2];
			if (this.movement == 0) {
				// randomly choose a destination in a specific search radius
			} 
			if (this.movement == 1) {
				//	search for your previously picked meal in a big search radius		
			}
			if (this.movement == 2) {
				// look for the meals in a constant order
			}
			
			return koord;
		}
			
			
		public boolean select_meal() {
			return true;
		}
	} // END of ScheduledMethod.
	
	// eigentliche Bewegung zwischen den Zeitschritten
	@ScheduledMethod(start=1.5, interval=1)
	public void move(){
		space.moveByDisplacement(this, velocity.x, velocity.y);
	}
	
	
} // END of Class.