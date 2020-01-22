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
	double vision;					// Sichtradius
	protected Vector2d velocity;	// Geschwindigkeits- und Ausrichtungsvektor
	float walking_speed = 0.002f;
	int aversionradius = 2;
	Context<Object> context;
	List<Ausgabe> visitedAusgaben; 		// Liste der Besuchten Ausgaben
	int waitticks;
	SharedStuff sharedstuff; //among others: list of all kassen & theken for faster access
	public int num; //number of the student
	public Vector2d directlyToKassa = new Vector2d(-1.0,-1.0); // Speichert die ausgewaehlte Kasse
	public Vector2d check = new Vector2d(-1.0,-1.0);
	private Kasse tempBar = null;
	protected Ausgabe tempDestination;
	private Object[] closestkasse;
	
	// choose randomly
	public Student(ContinuousSpace s, Context c, int num, SharedStuff sharedstuff) {
		this.space = s;
		this.food_preference = RandomHelper.nextIntFromTo(0, 3);
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
		// VEGGIE
		if (this.food_preference == 0 && consts.vegetarian.contains(essen)) return true;
		// VEGAN
		else if (this.food_preference == 0 && consts.vegan.contains(essen)) return true;
		// MEAT
		else if (this.food_preference == 0 && consts.meatlover.contains(essen)) return true;
		// No Preference
		else if (this.food_preference == 0 && consts.noPref.contains(essen)) return true;
		
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

		int potential_grid_pos = sharedstuff.grid.get((int)potentialcoords.x, (int)potentialcoords.y) ;
		if (potential_grid_pos > 2) {//studenten sind +2, also ist dann shcon wer da
			//throw new java.lang.RuntimeException("Student läuft auf anderen Studenten!");
		}

		if ((potential_grid_pos == 1) || (potential_grid_pos == 2) || (potential_grid_pos == 4)) { //theken, kassen, accesspoints
			sharedstuff.grid.set((int)pos.getX(), (int)pos.getY(), 3);
			return;
		}

		sharedstuff.grid.set((int)potentialcoords.x, (int)potentialcoords.y, 3);
		space.moveByDisplacement(this, velocity.x, velocity.y);
	}
	

	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		Vector2d avoidance = avoid_others();
		if (avoidance != null) {
			velocity.setX(-avoidance.x);
			velocity.setY(-avoidance.y);
		} else {
			Vector2d movement = move();
			if (movement != null) {
				// Du bist auf dem Weg.
				velocity.setX(movement.x);
				velocity.setY(movement.y);
			} else if (movement == null) {
//				System.out.println(this.directlyToKassa);
				if (!this.directlyToKassa.equals(check) && this.directlyToKassa != null) {
					if (tempBar != null && tempBar.pay(this)) {
						System.out.println("Student #" + this.num + " hat die Mensa verlassen.");
						context.remove(this);
					}
					// nimm die bereits gewaehlte Kasse
					velocity.setX(this.directlyToKassa.x);
					velocity.setY(this.directlyToKassa.y);
				} else {
					// waehle Kasse
					//System.out.println("choose Kassa");
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
		return new Vector2d();
	}


} // END of Class.
