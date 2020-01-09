package mensamodell;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;

import mensamodell.consts.*;
import repast.simphony.context.Context;

//TODO next:
//-Studenten gehen nicht auf die Theke drauf
//-Studenten gehen theke nach theke ab
//-Studenten haben listen welche theken sie schon besucht haben
//-Vor einer Theke gehen studenten in ordentliche Reihen
//-Chaotische Studenten gehen zu Theke falls in Sichtweite


/*
 * Studenten Klasse. Der Student laeuft durch die Mensa und erkennt innerhalb seines Sichtradius verschiedene Theken anhand der Schlange und
 * seines movement_pref entscheidet er wohin er laeuft.
 */

public class Student {

	// Class variables
	private ContinuousSpace space;	// Der kontinuierliche Raum wird in dieser Variablen gespeichert.
	int food_preference; 			// 0=veggie, 1=vegan, 2=meat, 3=no_preference
	double vision;					// Sichtradius
	protected Vector2d velocity;	// Geschwindigkeits- und Ausrichtungsvektor
	float walking_speed = 0.002f;
	int aversionradius = 1;
	Context<Object> context;
	List<Theke> visitedBars; 		// Liste der Besuchten Theken 
	boolean sated;					// Besser waere es das Objekt vom context zu entfernen 
									// context.remove(Object)

	// choose randomly
	public Student(ContinuousSpace s, Context c) {
		this.space = s;
		this.food_preference = RandomHelper.nextIntFromTo(0, 3);
		this.velocity = new Vector2d(0,0);
		this.sated = false;
		this.visitedBars = new ArrayList<>();
		this.context = c;
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
	public double[] to_kasse() {
		double[] distXY = null;
		ContinuousWithin kasseInRange = new ContinuousWithin(space, this, 1000);
		for (Object k : kasseInRange.query()) {
			if (k instanceof Kasse) {
				distXY = space.getDisplacement(space.getLocation(this), space.getLocation(k));
				// falls student in kassen reichweite --> entfernen
				if (((Kasse) k).pay(this)) context.remove(this);
				return distXY;
			}
		}
		return null;
	}
	
	
	// Sucht den kuerzesten Weg
	public double[] to_next_ausgabe() {

		ContinuousWithin barInRange;
		double[] distXY = null;						// Abstandsvektor fuer NdPoints
		
		// pruefe ob du bereits nah genug bist um Essen zu nehmen
		barInRange = new ContinuousWithin(space, this, 4);
		for (Object b : barInRange.query()) {
			if (b instanceof Theke && !visitedBars.contains(b)) {
				visitedBars.add((Theke) b); 
				// Du stehst vor einer Theke
				distXY = space.getDisplacement(space.getLocation(this), space.getLocation(this));
				// System.out.println(distXY[0] + " " + distXY[1]);
				return distXY; //  == {0,0}
			}
		}
		
		// Suche deinen Weg zur naechsten Theke
		NdPoint lastPos = space.getLocation(this);										// speichere die aktuelle Position
		ContinuousWithin barInVision = new ContinuousWithin(space, this, vision);		// erzeugt eine Query mit allen Objekten im Sichtradius
		double minBarDist = vision;					// kuerzester Abstand zu einer Bar
		NdPoint closestBarPoint = new NdPoint();	// Punkt mit naechster Bar
		Theke v = null; 							// zum speichern der besuchten Theke
		
		for (Object o : barInVision.query()){			// Durchlaufe die Query des Sichtradius
			if (o instanceof Theke && !visitedBars.contains(o)){	// falls das Theke und noch nicht besucht
				Theke tempBar = (Theke) o;
				NdPoint tempBarLoc = space.getLocation(tempBar);
				double dist = space.getDistance(lastPos, tempBarLoc);			// Distanz zur Theke, falls minimum -> speichern
				if (dist < minBarDist){
					minBarDist = dist;
					closestBarPoint = tempBarLoc;
					v = tempBar;
					distXY = space.getDisplacement(lastPos, closestBarPoint);	// speichere Abstand in x- und y-Ausrichtung
				}
			}
		}
		if (v != null) {
			return distXY;
		} else {
			// Falls alle Theken besucht oder Essen gefunden.
			return null;
		}
	}

	public Vector2d avoid_others() {
		NdPoint lastPos = space.getLocation(this);
		ContinuousWithin query = new ContinuousWithin(space, this, vision);
		Vector2d distXY = new Vector2d(0,0);
		double minStudDist = 99999;
		NdPoint closestStudPoint = new NdPoint();
		Student neigh;

		for (Object o : query.query()){
			if (o instanceof Student){
				neigh = (Student)o;
				NdPoint p = space.getLocation(neigh);
				double dist = space.getDistance(lastPos, p);
				if (dist < minStudDist){
					minStudDist = dist;
					closestStudPoint = p;
					distXY = new Vector2d(space.getDisplacement(lastPos, p)[0], space.getDisplacement(lastPos, p)[1]);
				}
			}
		}
		if (distXY.length() < aversionradius)
			return distXY;

		return null;
	}

	// Standard Student weicht nur aus. 
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		Vector2d avoidance = avoid_others();
		if (avoidance != null) {
			velocity.setX(-avoidance.x);
			velocity.setY(-avoidance.y);
		}
	}


	/**
	 * Eigentliche Bewegung zwischen den Zeitschritten.
	 */
	@ScheduledMethod(start=0.5, interval=1)
	public void do_move(){

		velocity.normalize();
		velocity.scale(walking_speed);

		NdPoint pos = space.getLocation(this);
		Vector2d potentialcoords = new Vector2d(pos.getX()+velocity.x, pos.getY()+velocity.y);

		if (potentialcoords.x <= 0 || potentialcoords.x >= consts.SIZE_X || potentialcoords.y <= 0 || potentialcoords.y >= consts.SIZE_Y){
			//throw new java.lang.RuntimeException("Student ausserhalb der Mensa.");
			return;
		}

		space.moveByDisplacement(this, velocity.x, velocity.y);
	}


} // END of Class.
