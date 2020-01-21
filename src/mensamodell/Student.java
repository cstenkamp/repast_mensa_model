package mensamodell;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;

import repast.simphony.context.Context;

//TODO next:
//-Studenten gehen nicht auf die Theke drauf SO HALB GELOEST
//-Studenten gehen theke nach theke ab DONE
//-Studenten haben listen welche theken sie schon besucht haben DONE
//-Vor einer Theke gehen studenten in ordentliche Reihen
//-Chaotische Studenten gehen zu Theke falls in Sichtweite


/*
 * Studenten Klasse. Der Student laeuft durch die Mensa und erkennt innerhalb seines Sichtradius verschiedene Theken anhand der Schlange und
 * seines movement_pref entscheidet er wohin er laeuft.
 */

public class Student {

	// Class variables
	ContinuousSpace space;	// Der kontinuierliche Raum wird in dieser Variablen gespeichert.
	int food_preference; 			// 0=veggie, 1=vegan, 2=meat, 3=no_preference
	double vision;					// Sichtradius
	protected Vector2d velocity;	// Geschwindigkeits- und Ausrichtungsvektor
	float walking_speed = 0.002f;
	int aversionradius = 1;
	Context<Object> context;
	List<Theke> visitedBars; 		// Liste der Besuchten Theken 
	int waitticks;
	SharedStuff sharedstuff; //among others: list of all kassen & theken for faster access
	int num; //number of the student
	
	// choose randomly
	public Student(ContinuousSpace s, Context c, int num, SharedStuff sharedstuff) {
		this.space = s;
		this.food_preference = RandomHelper.nextIntFromTo(0, 3);
		this.velocity = new Vector2d(0,0);
		this.visitedBars = new ArrayList<>();
		this.context = c;
		this.waitticks = 0;
		this.sharedstuff = sharedstuff;
		this.num = num;
	}


	public Object[] get_closest(List lst) {
		Vector2d distXY = new Vector2d(999999,999999);
		Vector2d tmpdist = null;
		double[] tmp;
		Object res = null;
		for (Object obj : lst) {
			tmp = space.getDisplacement(space.getLocation(this), space.getLocation(obj));
			tmpdist = new Vector2d(tmp[0], tmp[1]);
			if (tmpdist.length() < distXY.length()) {
				distXY = tmpdist;
				res = obj;
			}
		}
		return new Object[]{res, distXY};
	}
	
	
	public Vector2d to_kasse() {
		//ContinuousWithin kasseInRange = new ContinuousWithin(space, this, 1000);
		Object[] closestkasse = get_closest(sharedstuff.kassen);
		Vector2d distance = (Vector2d) closestkasse[1];
		Kasse k = (Kasse) closestkasse[0];
		if (((Kasse) k).pay(this)) {
			context.remove(this);
			System.out.println("Student #"+this.num+" left the Mensa.");
			return null;
		}
		return distance;
	}
	
	// Hier wird geprüft ob wir vor einer Theke stehen.
	public boolean at_bar() {
		// pruefe ob du bereits nah genug bist um Essen zu nehmen
		Vector2d distXY = null;
		ContinuousWithin barInRange = new ContinuousWithin(space, this, 4);
		for (Object b : barInRange.query()) {
			if (b instanceof Theke && !visitedBars.contains(b)) {
				visitedBars.add((Theke) b); // DAS MUSS AUCH BLEIBEN 
				//in Du stehst vor einer Theke behalte deine aktuelle Position bei
				return true;
			}
		}	
		return false;
	}
	
	public Vector2d avoid_others() {
		NdPoint lastPos = space.getLocation(this);
		ContinuousWithin query = new ContinuousWithin(space, this, aversionradius);
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
		if (minStudDist < 99999) {
			if (distXY.length() == 0) {
				distXY.x = RandomHelper.nextDouble();
				distXY.y = RandomHelper.nextDouble();
				return distXY;
			} else if (distXY.length() < aversionradius) {
				return distXY;
			}
		}
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
		if (waitticks > 0) {
			waitticks --;
			return;
		}
		
		velocity.normalize();
		velocity.scale(walking_speed);

		if (Double.isNaN(velocity.x)) 
			velocity = new Vector2d(0, velocity.y);
		if (Double.isNaN(velocity.y)) 
			velocity = new Vector2d(velocity.x, 0);
		
		NdPoint pos = space.getLocation(this);
		Vector2d potentialcoords = new Vector2d(pos.getX()+velocity.x, pos.getY()+velocity.y);
		//System.out.println("Stuent #"+num+" did something"+pos.getX()+" "+pos.getY()+" velocity "+velocity.x+" "+velocity.y);

		if (potentialcoords.x <= 0 || potentialcoords.x >= consts.SIZE_X || potentialcoords.y <= 0 || potentialcoords.y >= consts.SIZE_Y){
			//throw new java.lang.RuntimeException("Student ausserhalb der Mensa.");
			return;	
		}

		space.moveByDisplacement(this, velocity.x, velocity.y);
	}


} // END of Class.
