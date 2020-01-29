package mensamodell;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import org.apache.commons.math3.exception.NullArgumentException;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;

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
	protected Object closestkasse;
	protected Boolean hungry;
	
//wenn er gegen wände läuft läuft er in eine zufällige richtung. damit die nicht jigglet muss er sie speichern.
	private Vector2d keepWalkingdirection =  new Vector2d(0, 0); 
	private Vector2d keepZwischenziel = new Vector2d(0, 0);
	private Vector2d keepZwischenziel_mypos = new Vector2d(0, 0);
	private int keepZwischenziel_stoodfor = 0;

	
	
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
		this.hungry = true;
	}

	
	// waehle dein Essen
	public boolean chooseMeal_2(Ausgabe currentBar) {
		// warte vor der Ausgabe
		this.waitticks = currentBar.wait_time;

		int essen = currentBar.getEssen();
		double randomNum = RandomHelper.nextDoubleFromTo(0, 1);
		// VEGGIE
		if (this.food_preference == 0 && consts.vegetarian.contains(essen)) {
			if (essen == 0 && randomNum <= 0.9) {new VeggieObj(context,space); return true;}
			if (essen == 1 && randomNum <= 0.5) {new VeganObj(context,space); return true;}
			if (essen == 3 && randomNum <= 0.2) {new SaladObj(context,space); return true;}
			if (essen == 4 && randomNum <= 0.1) {new PommesObj(context,space); return true;}
		}
		// VEGAN
		else if (this.food_preference == 1 && consts.vegan.contains(essen)) {
			if (essen == 1 && randomNum <= 0.9) {new VeganObj(context,space); return true;}
			if (essen == 3 && randomNum <= 0.2) {new SaladObj(context,space); return true;}
			if (essen == 4 && randomNum <= 0.1) {new PommesObj(context,space); return true;}
		}
		// MEAT
		else if (this.food_preference == 2 && consts.meatlover.contains(essen)) {
			if (essen == 0 && randomNum <= 0.2) {new VeggieObj(context,space); return true;}
			if (essen == 1 && randomNum <= 0.1) {new VeganObj(context,space); return true;}
			if (essen == 2 && randomNum <= 0.9) {new MeatObj(context,space); return true;}
			if (essen == 3 && randomNum <= 0.2) {new SaladObj(context,space); return true;}
			if (essen == 4 && randomNum <= 0.1) {new PommesObj(context,space); return true;}
		}
		// No Preference
		else if (this.food_preference == 3 && consts.noPref.contains(essen)) {
			if (essen == 0) {new VeggieObj(context,space);}
			if (essen == 1) {new VeganObj(context,space);}
			if (essen == 2) {new MeatObj(context,space);}
			if (essen == 3) {new SaladObj(context,space);}
			if (essen == 4) {new PommesObj(context,space);}
			return true;
		}
		return false;
	}

	public boolean chooseMeal(Ausgabe a) {
		boolean tmp = chooseMeal_2(a);
		if (tmp)
			System.out.println("Student #" + this.num + " hat sich ein Essen gesucht.");
		return tmp;
	}
	

	// der student geht zur Kasse
	public Object to_kasse() {
		Object closestkasse = get_closest(sharedstuff.kassen);
//		Vector2d distance = (Vector2d) closestkasse[1];
//		Kasse k = (Kasse) closestkasse[0];
		return closestkasse;
	}
		
	
	public Vector2d get_dist_to(Object obj) throws NullArgumentException {
		if (!(obj instanceof Ausgabe) && !(obj instanceof Kasse)) {
			throw new NullArgumentException();
		}
		double[] tmp = space.getDisplacement(space.getLocation(this), space.getLocation(obj));
		if (Double.isNaN(tmp[0]) || Double.isNaN(tmp[1])) {
			throw new ArithmeticException();
		}
		Vector2d tmpdist = new Vector2d(tmp[0], tmp[1]);
		return tmpdist;
	}
	
	// sucht die naechste kasse
	public Object get_closest(List lst) {
		Vector2d distXY = new Vector2d(999999,999999);
		Vector2d tmpdist = null;
		Object res = null;
		try {
			for (Object obj : lst) {
				tmpdist = get_dist_to(obj);
				if (tmpdist.length() < distXY.length()) {
					distXY = tmpdist;
					res = obj;
				}
			}
		} catch (ArithmeticException e) {
			return res;
		}


		return res;
	}
	
	
	
	
	
	public Vector2d walk_but_dont_bump(Object to_obj) {
		Vector2d distance = get_dist_to(to_obj);
		NdPoint mypos = space.getLocation(this);
		NdPoint thatpos = space.getLocation(to_obj);
		List<Integer> between = sharedstuff.grid.Bresenham((int)mypos.getX(), (int)mypos.getY(), (int)thatpos.getX(), (int)thatpos.getY());
		between = between.subList(1, between.size()-1);
		//between sind die grid-punkte die er crossen müsste um dahin zu kommen).
		if (between.get(0) > consts.GRID_STUDENT) {
			if ((keepZwischenziel.x != 0) || (keepZwischenziel.y != 0)) {
				distance = keepZwischenziel;
//				Vector2d tmp = new Vector2d((int)mypos.getX(), (int)mypos.getY());
//				if (tmp.equals(keepZwischenziel_mypos)) {
//					keepZwischenziel_stoodfor++;
//				} 
				keepZwischenziel_stoodfor++; //das hier weg, dafür das oben hin, und hier drunter stattdessen more like > 50
				if (keepZwischenziel_stoodfor > 200000) { //TODO statt das ne gewisse Zeit zu machen soll der gucken ob's erfolgreich ist
					keepZwischenziel = new Vector2d(0, 0);
					keepZwischenziel_mypos = new Vector2d(0, 0);
					keepZwischenziel_stoodfor = 0;
				}
			} else {
				if (Math.abs((int)mypos.getX()-(int)thatpos.getX()) > Math.abs((int)mypos.getY()-(int)thatpos.getY())) {
					//wenn also die x-differenz relevanter ist als die y-differenz -> mache schlenker in y-diff.
					//hard-coden dass wenn sie vor der aktionstheke stehen nicht gegen die Wand laufen sollen
					if (mypos.getY() < 18 && mypos.getY() > 12 && mypos.getX() < 60 && mypos.getX() > 50)
						distance.y = distance.getX()*10*(-0.5);
					else if (mypos.getY() < 18 && mypos.getY() > 12 && mypos.getX() < 50 && mypos.getX() > 40)
						distance.y = distance.getX()*10*(+0.5);
					else
						distance.y = distance.getX()*10*(RandomHelper.nextIntFromTo(0, 1)-0.5);
					//distance.x = 0;
				} else {
				//wenn also die y-differenz relevanter ist als die x-differenz -> mache schlenker in x-diff
					distance.x = distance.getY()*10*(RandomHelper.nextIntFromTo(0, 1)-0.5);
					//distance.y = 0;
				}
				//distance.normalize();
				keepZwischenziel = distance;
				keepZwischenziel_mypos = new Vector2d((int)mypos.getX(), (int)mypos.getY());
			}
		} else {
			keepZwischenziel = new Vector2d(0, 0);
			keepZwischenziel_mypos = new Vector2d(0, 0);
			keepZwischenziel_stoodfor = 0;
		}
		return distance;
	}
	
	
	
	
	

	// Hier wird geprueft ob wir vor einer Ausgabe stehen.
	public Ausgabe at_bar() {
		// pruefe ob du bereits nah genug bist um Essen zu nehmen
		ContinuousWithin ausgabeInRange = new ContinuousWithin(space, this, 4);
		for (Object b : ausgabeInRange.query()) {
			if (b instanceof Ausgabe && !visitedAusgaben.contains(b)) {
				visitedAusgaben.add((Ausgabe) b);
				//System.out.println("new bar");
				return (Ausgabe) b;
			}
		}
		return null;
	}

	// gehe anderen Studenten aus dem Weg
	public Vector2d avoid_others() {
		NdPoint lastPos = space.getLocation(this);
		ContinuousWithin query = new ContinuousWithin(space, this, aversionradius);
		Vector2d distXY = new Vector2d(0,0);
		double minStudDist = 99999;
		Student neigh;
		for (Object o : query.query()){
			if (o instanceof Student){
				neigh = (Student)o;
				NdPoint p = space.getLocation(neigh);
				double dist = space.getDistance(lastPos, p);
				if (dist < minStudDist){
					minStudDist = dist;
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


	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		
		//Priorität 1) Laufe nicht gegen andere
		Vector2d avoidance = avoid_others();
		if (avoidance != null) {
			velocity.setX(-avoidance.x);
			velocity.setY(-avoidance.y);
			return;
		} 
		
		//Priorität 2) Laufe zu Ausgaben (returns null wenn er gerade was zu essen gefunden hat, nicht mehr hungrig ist, oder schon alle Theken besucht hat)
		Vector2d movement = move(); //move ist überschrieben für die 3 Tochterklassen
		if (movement != null) {
			velocity.setX(movement.x);
			velocity.setY(movement.y);
			return;
		} 
			
		if (tempBar != null && tempBar.pay(this)) {
			System.out.println("Student #" + this.num + " hat die Mensa verlassen.");
			context.remove(this);
		} else {
			// waehle Kasse
//					System.out.println("choose Kassa");
			// gibt Location und Objekt zurueck
			try {
				this.tempBar = (Kasse) to_kasse();
				this.directlyToKassa = walk_but_dont_bump(this.tempBar);
				if (this.directlyToKassa != null) {
					velocity.setX(this.directlyToKassa.x);
					velocity.setY(this.directlyToKassa.y);
				}
			} catch (NullArgumentException e) {
				//dont set velocity 
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
		velocity = scaleAndNormalize(velocity);		
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
		int durchlauf = 0;
		while ((potential_grid_pos > consts.GRID_STUDENT) || ((velocity.x == 0) && (velocity.y == 0))) { //theken, kassen, accesspoints
			if (triedkeepwalking) {
				keepWalkingdirection = new Vector2d(0, 0);
			}
			if (durchlauf++ > 50) {
				do {
					velocity.x = RandomHelper.nextIntFromTo(-1, 1);
					velocity.y = RandomHelper.nextIntFromTo(-1, 1);
					velocity = scaleAndNormalize(velocity);
				} while ((Double.isNaN(velocity.x)) || (Double.isNaN(velocity.y)) || ((velocity.x == 0) && (velocity.y == 0)) );
			} else {
				if (velocity.x < 00.1*walking_speed && velocity.y < 00.1*walking_speed) {
					if (((keepWalkingdirection.x == 0) && (keepWalkingdirection.y == 0))) { //wenn er also nicht vorher schon gegen 'ne Wand gelaufen wäre
						do {
							velocity.x = RandomHelper.nextIntFromTo(-1, 1);
							velocity.y = RandomHelper.nextIntFromTo(-1, 1);
							velocity = scaleAndNormalize(velocity);
						} while ((Double.isNaN(velocity.x)) || (Double.isNaN(velocity.y)) || ((velocity.x == 0) && (velocity.y == 0)) );
						keepWalkingdirection = velocity;
					} else {
						velocity = keepWalkingdirection;
						velocity = scaleAndNormalize(velocity);
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
			}
			velocity = scaleAndNormalize(velocity);
			potentialcoords = new Vector2d(pos.getX()+velocity.x, pos.getY()+velocity.y);
			potential_grid_pos = sharedstuff.grid.get((int)potentialcoords.x, (int)potentialcoords.y);
		}
		sharedstuff.grid.set((int)potentialcoords.x, (int)potentialcoords.y, 3);
		space.moveByDisplacement(this, velocity.x, velocity.y);
	}

	
	
	public Vector2d scaleAndNormalize(Vector2d vel) {
		vel.normalize();
		if (Double.isNaN(vel.x))
			vel = new Vector2d(0, vel.y);
		if (Double.isNaN(vel.y))
			vel = new Vector2d(vel.x, 0);
		vel.scale(walking_speed);
		return vel;
	}
} // END of Class.
