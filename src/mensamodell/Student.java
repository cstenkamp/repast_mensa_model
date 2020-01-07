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


	/**
	 * Methode wird jede Runde ausgefuehrt. Suche das/die naechste Ziel/Theke
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {

		// speichere die aktuelle Position
		NdPoint lastPos = space.getLocation(this);

		// erzeugt eine Query mit allen Objekten im Sichtradius
		ContinuousWithin query = new ContinuousWithin(space, this, vision);

		Theke neigh;				// dummy f�r Theken Objekt
		double[] distXY = null;		// Abstandsvektor fuer NdPoints
		double minBarDist = vision;	// k�rzester Abstand zu einer Bar
		NdPoint closestBarPoint = new NdPoint();	// Punkt mit n�chster Bar

		// Durchlaufe die Query des Sichtradius
		for (Object o : query.query()){

			// falls das Objekt Theke
			if (o instanceof Theke){
				neigh = (Theke) o;

				NdPoint t = space.getLocation(neigh);
				// Distanz zur Theke, falls minimum -> speichern
				double dist = space.getDistance(lastPos, t);
				if (dist < minBarDist){
					minBarDist = dist;
					closestBarPoint = t;
				}
				// speichere Abstand in x- und y-Ausrichtung
				distXY = space.getDisplacement(lastPos, t);
			}
		}

		// Set Velocity
		velocity.setX(distXY[0]);
		velocity.setY(distXY[1]);
		// normalisiert den Vektor auf 1
		velocity.normalize();

	} // END of ScheduledMethod.

	/**
	 * Eigentliche Bewegung zwischen den Zeitschritten.
	 */
	@ScheduledMethod(start=1.5, interval=1)
	public void move(){
		NdPoint potentialcoordinates = space.getLocation(this);
	    if ((potentialcoordinates.getX()+velocity.x <= 0) || (potentialcoordinates.getX()+velocity.x >= consts.SIZE_X)) velocity.x = 0;
	    if ((potentialcoordinates.getY()+velocity.y <= 0) || (potentialcoordinates.getY()+velocity.y  >= consts.SIZE_Y)) velocity.y = 0;
		space.moveByDisplacement(this, velocity.x, velocity.y);
	}


} // END of Class.
