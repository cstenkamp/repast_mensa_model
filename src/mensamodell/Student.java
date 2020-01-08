package mensamodell;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import javax.vecmath.Vector2d;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;

import mensamodell.consts.*;



/*
 * Studenten Klasse. Der Student laeuft durch die Mensa und erkennt innerhalb seines Sichtradius verschiedene Theken anhand der Schlange und
 * seines movement_pref entscheidet er wohin er laeuft.
 */

public class Student {

	// Class variables
	private ContinuousSpace space;	// Der kontinuierliche Raum wird in dieser Variablen gespeichert.
	int food_preference; 						// 0=veggie, 1=vegan, 2=meat, 3=no_preference
	int movement; 									// 0=chaotic, 1=goal oriented, 2=pathoriented (constant)
	double vision;									// Sichtradius
	protected Vector2d velocity;		// Geschwindigkeits- und Ausrichtungsvektor
	float walking_speed = 0.002f;
	
	// choose randomly
	public Student(ContinuousSpace s) {
		this.space = s;
		this.food_preference = RandomHelper.nextIntFromTo(0, 3);
		this.movement = 1;//RandomHelper.nextIntFromTo(0, 2);
		this.velocity = new Vector2d(0,0);
		if (this.movement == 0) {
			this.vision = 20; // chaotische besitzen einen kleineren Sichtradius
		} else if (this.movement == 1) {
			this.vision = 300;
		} else {
			this.vision = 300;
		}
	}

//	// choose only one preference
//	public Student(ContinuousSpace s, int value, boolean food_pref) {
//		this.space = s;
//		// test
//		// if foodpref: value in 0,1,2,3, else in 0,1,2
//		if(food_pref) {
//			this.food_preference = value;
//			this.movement = RandomHelper.nextIntFromTo(0, 2);
//		}else {
//			this.food_preference = RandomHelper.nextIntFromTo(0, 3);
//			this.movement = value;
//		}
//	}
//
//	// initialise both preferences
//	public Student(ContinuousSpace s, int food_pref, int move_pref) {
//		this.space = s;
//		this.food_preference = food_pref;
//		this.movement = move_pref;
//	}

//	public boolean select_meal() {
//		return true;
//	}
//	
//	// chaotischer Lauftyp
//	public void chaotic() {
//		
//	}
	
	public void to_next_ausgabe() {
		
		NdPoint lastPos = space.getLocation(this);																	// speichere die aktuelle Position
		ContinuousWithin barInVision = new ContinuousWithin(space, this, vision);		// erzeugt eine Query mit allen Objekten im Sichtradius

		double[] distXY = null;										// Abstandsvektor fuer NdPoints
		double minBarDist = vision;								// kuerzester Abstand zu einer Bar
		NdPoint closestBarPoint = new NdPoint();	// Punkt mit naechster Bar
		Theke v = null; 													// zum speichern der besuchten Theke

		for (Object o : barInVision.query()){															// Durchlaufe die Query des Sichtradius
			if (o instanceof Theke &&  !((Theke)o).visited){								// falls das Objekt Essensausgabe aber keine Kasse und noch nicht besucht
				Theke tempBar = (Theke)o;
				NdPoint tempBarLoc = space.getLocation(tempBar);
				double dist = space.getDistance(lastPos, tempBarLoc);					// Distanz zur Theke, falls minimum -> speichern
				if (dist < minBarDist){
					minBarDist = dist;
					closestBarPoint = tempBarLoc;
					v = tempBar;
					distXY = space.getDisplacement(lastPos, closestBarPoint);		// speichere Abstand in x- und y-Ausrichtung
				}
			} 
		}
		if (v != null) {
	    //double vec_len = Math.sqrt(distXY[0]*distXY[0] + distXY[1]*distXY[1]);
			//velocity.setX(distXY[0]/vec_len*walking_speed);
			//velocity.setY(distXY[1]/vec_len*walking_speed);
			velocity.normalize();
			velocity.scale(walking_speed);
		} else {
			velocity.set(0, 0);
		}
		/*
		try {
			// setze visited der Theke auf true
			v.setVisit();
		} catch (Exception e) {
			System.out.println("Alle Essensausgaben besucht.");
		}
		*/
		
		
		// Set Velocity/Geschwindigkeit

	}


	/**
	 * Eigentliche Bewegung zwischen den Zeitschritten.
	 */
	@ScheduledMethod(start=0.5, interval=1)
	public void do_move(){
    NdPoint pos = space.getLocation(this);
		Vector2d potentialcoords = new Vector2d(pos.getX()+velocity.x, pos.getY()+velocity.y);
		
    if (potentialcoords.x <= 0 || potentialcoords.x >= consts.SIZE_X || potentialcoords.y <= 0 || potentialcoords.y >= consts.SIZE_Y){
    	//throw new java.lang.RuntimeException("Student ausserhalb der Mensa.");
    	return;
    }
    
    space.moveByDisplacement(this, velocity.x, velocity.y);
	}


} // END of Class.
