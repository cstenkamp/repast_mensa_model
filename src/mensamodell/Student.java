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

public class Student {

	// Class variables
	ContinuousSpace space;	// Der kontinuierliche Raum wird in dieser Variablen gespeichert.
	int food_preference; 			// 0=veggie, 1=vegan, 2=meat, 3=no_preference
	protected Vector2d velocity;	// Geschwindigkeits- und Ausrichtungsvektor
	float walking_speed = 0.002f;
	int aversionradius = 2;
	Context<Object> context;
	List<Ausgabe> visitedAusgaben; 		// Liste der Besuchten Ausgaben
	int waitticks;
	SharedStuff sharedstuff; //among others: list of all kassen & theken for faster access
	public int num; //number of the student
	public Vector2d directlyToKassa = new Vector2d(-1.0,-1.0); // Speichert die ausgewaehlte Kasse
	protected Kasse tempBar = null;
	protected Ausgabe tempDestination;
	protected Object[] closestkasse;
	private Vector2d keepWalkingdirection =  new Vector2d(0, 0); //wenn er gegen wände läuft läuft er in eine zufällige richtung. damit die nicht jigglet muss er sie speichern.
	static private int notStudents = 0; // DATA
	static int payStud = 0; // DATA
	static int countEssenVeggie = 0; // DATA
	static int countEssenVegan = 0; // DATA
	static int countEssenMeat = 0; // DATA
	static int countEssenSALAD = 0; // DATA
	static int countEssenPOMMES = 0; // DATA


	// choose randomly
	public Student(ContinuousSpace s, Context c, int num, SharedStuff sharedstuff, int fp) {
		this.space = s;
		this.food_preference = fp;
		this.velocity = new Vector2d(0,0);
		this.visitedAusgaben = new ArrayList<>();
		this.context = c;
		this.waitticks = 0;
		this.sharedstuff = sharedstuff;
		this.num = num;
		this.tempDestination = null; // stellt sicher dass der student bis zur Ausgabe laeuft
	}

	// waehle dein Essen
	public boolean chooseMeal() {
		// warte vor der Ausgabe
		this.waitticks = 10000;
		// speicher die letzte Ausgabe
		Ausgabe currentBar = this.visitedAusgaben.get(visitedAusgaben.size()-1);
		int essen = currentBar.getEssen();
		double randomNum = RandomHelper.nextDoubleFromTo(0, 1);
		// VEGGIE
		if (this.food_preference == 0 && consts.vegetarian.contains(essen)) {
			if (essen == 0 && randomNum <= 0.9) {countEssenVeggie++; return true;}
			if (essen == 1 && randomNum <= 0.5) {countEssenVegan++; return true;}
			if (essen == 3 && randomNum <= 0.2) {countEssenSALAD++; return true;}
			if (essen == 4 && randomNum <= 0.1) {countEssenPOMMES++; return true;}
		}
		// VEGAN
		else if (this.food_preference == 1 && consts.vegan.contains(essen)) {
			if (essen == 1 && randomNum <= 0.9) {countEssenVegan++; return true;}
			if (essen == 3 && randomNum <= 0.2) {countEssenSALAD++; return true;}
			if (essen == 4 && randomNum <= 0.1) {countEssenPOMMES++; return true;}
		}
		// MEAT
		else if (this.food_preference == 2 && consts.meatlover.contains(essen)) {
			if (essen == 0 && randomNum <= 0.2) {countEssenVeggie++; return true;}
			if (essen == 1 && randomNum <= 0.1) {countEssenVegan++; return true;}
			if (essen == 2 && randomNum <= 0.9) {countEssenMeat++; return true;}
			if (essen == 3 && randomNum <= 0.2) {countEssenSALAD++; return true;}
			if (essen == 4 && randomNum <= 0.1) {countEssenPOMMES++; return true;}
		}
		// No Preference
		else if (this.food_preference == 3 && consts.noPref.contains(essen)) {
			if (essen == 0) {countEssenVeggie++;}
			if (essen == 1) {countEssenVegan++;}
			if (essen == 2) {countEssenMeat++;}
			if (essen == 3) {countEssenSALAD++;}
			if (essen == 4) {countEssenPOMMES++;}
			return true;
		}
		return false;
	}


	// der student geht zur Kasse
	public Object[] to_kasse() {
		Object[] closestkasse = get_closest(sharedstuff.kassen);
//		Vector2d distance = (Vector2d) closestkasse[1];
//		Kasse k = (Kasse) closestkasse[0];
		return closestkasse;
	}
	// sucht die naechste kasse
	public Object[] get_closest(List lst) {
		Vector2d distXY = new Vector2d(999999,999999);
		Vector2d tmpdist = null;
		double[] tmp = null;
		Object res = null;
		for (Object obj : lst) {
			tmp = space.getDisplacement(space.getLocation(this), space.getLocation(obj));
			tmpdist = new Vector2d(tmp[0], tmp[1]);
			if (tmpdist.length() < distXY.length()) {
				distXY = tmpdist;
				res = obj;
			}
		}
		// prueft ob space.getLocation(this) NaN wirft
		if (Double.isNaN(tmp[0]) || Double.isNaN(tmp[1])) return new Object[]{res, new Vector2d(0,0)};

		return new Object[]{res, distXY};
	}

	// Hier wird geprueft ob wir vor einer Ausgabe stehen.
	public boolean at_bar() {
		// pruefe ob du bereits nah genug bist um Essen zu nehmen
		Vector2d distXY = null;
		ContinuousWithin barInRange = new ContinuousWithin(space, this, 4);
		for (Object b : barInRange.query()) {
			if (b instanceof Ausgabe && !visitedAusgaben.contains(b)) {
				visitedAusgaben.add((Ausgabe) b);
				//System.out.println("new bar");
				return true;
			}
		}
		return false;
	}

	// gehe anderen Studenten aus dem Weg
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

//	// Standard Student weicht nur aus.
//	@ScheduledMethod(start = 0, interval = 1)
//	public void step() {
//		Vector2d avoidance = avoid_others();
//		if (avoidance != null) {
//			velocity.setX(-avoidance.x);
//			velocity.setY(-avoidance.y);
//		}
//	}

	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		Vector2d avoidance = avoid_others();
		if (avoidance != null) {
			velocity.setX(-avoidance.x);
			velocity.setY(-avoidance.y);
		} else {
			Vector2d movement = move();
//			if (movement != null) System.out.println(movement.x + " " + movement.y + " " + this);
			if (movement != null) {
				// Du bist auf dem Weg.
				velocity.setX(movement.x);
				velocity.setY(movement.y);
			} else if (movement == null) {
				if (tempBar != null && tempBar.pay(this)) {
					System.out.println("Student #" + this.num + " hat die Mensa verlassen.");
					context.remove(this);
					payStud++;
				} else {
					// waehle Kasse
//					System.out.println("choose Kassa");
					// gibt Location und Objekt zurueck
					this.closestkasse = to_kasse();
					this.tempBar = (Kasse) this.closestkasse[0];
					this.directlyToKassa = (Vector2d) this.closestkasse[1];
					if (this.directlyToKassa != null) {
						velocity.setX(this.directlyToKassa.x);
						velocity.setY(this.directlyToKassa.y);
					}
				}
			}
		}
	}

	//to be overridden
		public Vector2d move() {
			return new Vector2d(0,0);
		}

	/*
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
		sharedstuff.grid.set((int)pos.getX(), (int)pos.getY(), 0);


		Vector2d potentialcoords = new Vector2d(pos.getX()+velocity.x, pos.getY()+velocity.y);
		//System.out.println("Student #"+num+" did something"+pos.getX()+" "+pos.getY()+" velocity "+velocity.x+" "+velocity.y);

		if (potentialcoords.x <= 0 || potentialcoords.x >= consts.SIZE_X || potentialcoords.y <= 0 || potentialcoords.y >= consts.SIZE_Y){
			throw new java.lang.RuntimeException("Student ausserhalb der Mensa.");
		}

		int potential_grid_pos = sharedstuff.grid.get((int)potentialcoords.x, (int)potentialcoords.y);
		if (potential_grid_pos == consts.GRID_STUDENT) {//studenten sind +2, also ist dann schon wer da
			//throw new java.lang.RuntimeException("Student laeuft auf anderen Studenten!");
		}

		boolean triedkeepwalking = false;
		while ((potential_grid_pos > consts.GRID_STUDENT) || ((velocity.x == 0) && (velocity.y == 0))) { //theken, kassen, accesspoints
			if (triedkeepwalking) {
				keepWalkingdirection = new Vector2d(0, 0);
			}
			if (velocity.x < 00.1*walking_speed && velocity.y < 00.1*walking_speed) {
				if (((keepWalkingdirection.x == 0) && (keepWalkingdirection.y == 0))) { //wenn er also nicht vorher schon gegen 'ne Wand gelaufen wäre
					do {
						velocity.x = RandomHelper.nextIntFromTo(-1, 1);
						velocity.y = RandomHelper.nextIntFromTo(-1, 1);
						velocity.normalize();
						velocity.scale(walking_speed);
					} while ((Double.isNaN(velocity.x)) || (Double.isNaN(velocity.y)) || ((velocity.x == 0) && (velocity.y == 0)) );
					keepWalkingdirection = velocity;
				} else {
					velocity = keepWalkingdirection;
					triedkeepwalking = true;
				}
			} else {
				int leftofthat  = sharedstuff.grid.get((int)potentialcoords.x-1, (int)potentialcoords.y);
				int rightofthat = sharedstuff.grid.get((int)potentialcoords.x+1, (int)potentialcoords.y);
				int topofthat    = sharedstuff.grid.get((int)potentialcoords.x, (int)potentialcoords.y-1);
				int bottomofthat = sharedstuff.grid.get((int)potentialcoords.x, (int)potentialcoords.y+1); //TODO fehler fangen
				if ((leftofthat > consts.GRID_STUDENT) || (rightofthat > consts.GRID_STUDENT)) {
					velocity.y = 0;
				}
				if ((topofthat > consts.GRID_STUDENT) || (bottomofthat > consts.GRID_STUDENT)) {
					velocity.x = 0;
				}
			}
			velocity.normalize();
			if ((Double.isNaN(velocity.x)) || (Double.isNaN(velocity.y)))
				velocity = new Vector2d(0, 0);
			velocity.scale(walking_speed);
			potentialcoords = new Vector2d(pos.getX()+velocity.x, pos.getY()+velocity.y);
			potential_grid_pos = sharedstuff.grid.get((int)potentialcoords.x, (int)potentialcoords.y);
		}

		sharedstuff.grid.set((int)potentialcoords.x, (int)potentialcoords.y, 3);
		space.moveByDisplacement(this, velocity.x, velocity.y);
	}

//// COLLECT DATA:
	//TODO das sollte keine object-methode sein. Wenn in Student dann eher static, aber ggf auch ganz wonaders
	public int getCurNumStud() {
		Iterable allObj = space.getObjects();
		if (notStudents == 0) {
			for (Object o : allObj) {
				if (!(o instanceof Student)) notStudents++;
			}
		}
		return space.size()-notStudents;
	}

	public int getCurNumStudChaos() {
		Iterable allObj = space.getObjects();
		int stud = 0;
		for (Object o : allObj) {
			if ((o instanceof StudentChaotic)) stud++;
		}
		return stud;
	}

	public int getCurNumStudGoal() {
		Iterable allObj = space.getObjects();
		int stud = 0;
		for (Object o : allObj) {
			if ((o instanceof StudentGoalOriented)) stud++;
		}
		return stud;
	}
	// TODO payStud speichert die Zahl solange bis das Projekt wieder geschlossen wird
	public int getPayStud() {
//		System.out.println(payStud);
		return payStud;
	}

	public int getCountEssenVeggie() {
		return countEssenVeggie;
	}

	public int getCountEssenVegan() {
		return countEssenVegan;
	}

	public int getCountEssenMeat() {
		return countEssenMeat;
	}

	public int getCountEssenSALAD() {
		return countEssenSALAD;
	}

	public int getCountEssenPOMMES() {
		return countEssenPOMMES;
	}

//	public int[] getMealCount() {
//		return new int[] {countEssenVeggie, countEssenVegan,countEssenMeat, countEssenSALAD, countEssenPOMMES};
//	}

} // END of Class.
