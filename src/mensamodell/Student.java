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
	int food_preference; 			// 0=veggie, 1=vegan, 2=meat, 3=no_preference
	int movement; 					// 0=chaotic, 1=goal oriented, 2=constant
	double vision;					// Sichtradius
	private Vector2d velocity;		// Geschwindigkeits- und Ausrichtungsvektor

	// choose randomly
	public Student(ContinuousSpace s) {
		this.space = s;
		this.food_preference = RandomHelper.nextIntFromTo(0, 3);
		this.movement = 1;//RandomHelper.nextIntFromTo(0, 2);
		this.velocity = new Vector2d(0,0);
		if (this.movement == 0) {
			this.vision = 20; // chaotische besitzen einen kleineren Sichtradius
		}
		if (this.movement == 1) {
			this.vision = 300;
		} else {
			this.vision = 300;
		}
	}

	// choose only one preference
	public Student(ContinuousSpace s, int value, boolean food_pref) {

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
	public Student(ContinuousSpace s, int food_pref, int move_pref) {
		this.space = s;
		this.food_preference = food_pref;
		this.movement = move_pref;
	}

	/**
	 *  private Methoden der Klasse.
	 *
	 */

	public boolean select_meal() {
		return true;
	}
	
	// chaotischer Lauftyp
	public void chaotic() {
		
	}
	
	// kurze Wege Lauftyp
	public void shorty() {
		// speichere die aktuelle Position
		NdPoint lastPos = space.getLocation(this);

		// erzeugt eine Query mit allen Objekten im Sichtradius
		ContinuousWithin barInVision = new ContinuousWithin(space, this, vision);

		Theke tempBar;				// dummy fuer Theken Objekt
		double[] distXY = null;		// Abstandsvektor fuer NdPoints
		double minBarDist = vision;	// kuerzester Abstand zu einer Bar
		NdPoint closestBarPoint = new NdPoint();	// Punkt mit naechster Bar
		Theke v = null; // zum speichern der besuchten Theke

		// Durchlaufe die Query des Sichtradius
		for (Object o : barInVision.query()){
		// falls das Objekt Essensausgabe aber keine Kasse und noch nicht besucht
			if (o instanceof Theke && ((Theke) o).kind != - 1 && !((Theke) o).visited){
				tempBar = (Theke) o;
				NdPoint tempBarLoc = space.getLocation(tempBar);
				// Distanz zur Theke, falls minimum -> speichern
				double dist = space.getDistance(lastPos, tempBarLoc);
				if (dist < minBarDist){
					minBarDist = dist;
					closestBarPoint = tempBarLoc;
					v = tempBar;
				}
				// speichere Abstand in x- und y-Ausrichtung
				distXY = space.getDisplacement(lastPos, closestBarPoint);
			} //else if (o instanceof Theke && ((Theke) o).kind = - 1) {
				// Gehe zur Kasse!
			//}
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
		velocity.setX(distXY[0]);
		velocity.setY(distXY[1]);
	}

	/**
	 * Methode wird jede Runde ausgefuehrt. Suche das/die naechste Ziel/Theke
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		
		shorty();
		
	} // END of ScheduledMethod.

	/**
	 * Eigentliche Bewegung zwischen den Zeitschritten.
	 */
	@ScheduledMethod(start=1.5, interval=1)
	public void move(){
	    NdPoint potentialcoordinates = space.getLocation(this);
	    if (potentialcoordinates.getX()+velocity.x >= 0 || potentialcoordinates.getX()+velocity.x <= consts.SIZE_X || 
	    	potentialcoordinates.getY()+velocity.y >= 0 || potentialcoordinates.getY()+velocity.y <= consts.SIZE_Y){
	    	space.moveByDisplacement(this, velocity.x, velocity.y);
	    } else { 
	    	throw new java.lang.RuntimeException("Student ausserhalb der Mensa.");
	    }
	}


} // END of Class.
