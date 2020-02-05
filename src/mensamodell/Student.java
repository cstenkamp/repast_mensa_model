package mensamodell;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

import org.apache.commons.math3.exception.NullArgumentException;

import food_objs.MeatObj;
import food_objs.PommesObj;
import food_objs.SaladObj;
import food_objs.VeganObj;
import food_objs.VeggieObj;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Student {
	
	//TODO den walking-studenten hiervon erben lassen bzw irgendwie magically dafür sorgen dass das walken woanders steht

	// Class variables
	ContinuousSpace space;	// Der kontinuierliche Raum wird in dieser Variablen gespeichert.
	int food_preference; 			// 0=veggie, 1=vegan, 2=meat, 3=no_preference
	protected Vector2d velocity;	// Geschwindigkeits- und Ausrichtungsvektor
	float walking_speed = 0.002f;
	float aversionradius = 1.2f;
	Context<Object> context;
	List<Ausgabe> visitedAusgaben; 		// Liste der Besuchten Ausgaben
	SharedStuff sharedstuff; //among others: list of all kassen & theken for faster access
	public int num; //number of the student
	public Vector2d directlyToKassa = new Vector2d(-1.0,-1.0); // Speichert die ausgewaehlte Kasse
	protected Kasse tempKasse = null;
	protected Ausgabe tempDestination;
	protected Object closestkasse;
	protected Boolean hungry;
	ISchedulableAction scheduledStep;

	int waitticks;
	private int tickCount = 0;
	
//wenn er gegen wände läuft läuft er in eine zufällige richtung. damit die nicht jigglet muss er sie speichern.
	private Vector2d keepWalkingdirection =  new Vector2d(0, 0); 
	private Vector2d keepZwischenziel = new Vector2d(0, 0);
	private Vector2d keepZwischenziel_mypos = new Vector2d(0, 0);
	private int keepZwischenziel_stoodfor = 0;

// um sich das beste-essen-so-far zu zwischenspeichern
	protected List<Ausgabe> best_food_so_far;
	protected int best_food_so_war_was = 999;
	
	
// im grid	
	Grid<Object> grid;
	int nextLocX;
	int nextLocY;
	boolean inQueue;
	boolean waiting;
	boolean ThefoodIsOkay;
	boolean wantSalad;
	ArrayList<Ausgabe> barList;
	Ausgabe current;
	int waitTicks;
	int spentTicks;	 // DATA
	
	
	
	// SPACE
	public Student(int num, SharedStuff sharedstuff, int fp, Context<Object> context, ContinuousSpace<Object> s) {  
		this.food_preference = fp;
		this.visitedAusgaben = new ArrayList<Ausgabe>();
		this.context = context;
		this.waitticks = 0;
		this.sharedstuff = sharedstuff;
		this.num = num;
		this.hungry = true;
		this.best_food_so_far = new ArrayList<Ausgabe>();

		this.space = s;
		this.velocity = new Vector2d(0,0);
		this.tempDestination = null; // stellt sicher dass der student bis zur Ausgabe laeuft
		
		context.add(this);	
		sharedstuff.studierende.add(this);
		float x = RandomHelper.nextIntFromTo(consts.SIZE_X*2/5, consts.SIZE_X*3/5);
		float y = consts.SIZE_Y-5;
		space.moveTo(this, x, y); // add students to space or grid

		scheduledStep = sharedstuff.schedule.schedule(ScheduleParameters.createRepeating(sharedstuff.schedule.getTickCount()+1, 1), this, "step");
    //TODO Es gibt priorities in den schedules!! https://stackoverflow.com/a/57774003
	}

	// GRID
	public Student(int num, SharedStuff sharedstuff, int fp, Context<Object> context, Grid<Object> g, int x, int y) {
		this.food_preference = fp;
		this.visitedAusgaben = new ArrayList<Ausgabe>();
		this.context = context;
		this.waitticks = 0;
		this.sharedstuff = sharedstuff;
		this.num = num;
		this.hungry = true;
		this.best_food_so_far = new ArrayList<Ausgabe>();
		barList = new ArrayList<Ausgabe>();
		setBarList(); 											// Erstellt individuelle liste an Ausgaben f�r jeden Studenten
		this.grid = g;
		this.inQueue = false;
		this.waiting = false;
		this.current = null;
		this.nextLocX = x;
		this.nextLocY = y;
		this.ThefoodIsOkay = false;
		this.wantSalad = false;
		this.spentTicks = 0;							// DATA
		DoYouWantSalad();								// Entscheide zu beginn ob du Salat willst.
		
		context.add(this);	
		sharedstuff.studierende.add(this);
		grid.moveTo(this, (int)x, (int)y);

		scheduledStep = sharedstuff.schedule.schedule(ScheduleParameters.createRepeating(sharedstuff.schedule.getTickCount()+1, 1), this, "step_grid");
	}
	
	
	
	
	
	// waehle dein Essen
	public boolean chooseMeal_inner(Ausgabe currentBar) {
		// warte vor der Ausgabe (nur im space)
		this.waitticks = currentBar.getWaitTicks();

		//Essen funktioniert so: wenn das Essen vom vegetarismus-Grad zu ihnen passt, nehmen sie es zu einem gewissem Prozentsatz sofort. Wenn sie am Ende
		//alle Ausgaben abgelaufen sind, ohne was zu nehmen, nehmen sie das ein zufälliges von denen die "am besten" zu ihnen passen.
		int essen = currentBar.getEssen(); 	
		
		if (essen >= food_preference) { //foodpreference and food are sorted by priority: a person of type meat will like the lowest one most. A person of type vegan will like the lowest one >= itself (=2) most.
			if ((best_food_so_war_was > essen) && (food_preference != consts.NOPREFERENCE)) { //die mit no preference fügen einfach alles zu ihrer best_so_far liste hinzu, alle anderen nur die die sie am meisten mochten. 
				best_food_so_war_was = essen;
				best_food_so_far = new ArrayList<Ausgabe>();
			}
			best_food_so_far.add(currentBar);				
		}
		
		//System.out.println(visitedAusgaben.size() + "/" + sharedstuff.ausgaben.size());
		
		if (best_food_so_war_was == -1) { //das ist der Fal wenn du schon alle durchprobiert hast
			if (essen == consts.ESSEN_VEGGIE) {new VeggieObj(sharedstuff.foodContext);}
			if (essen == consts.ESSEN_VEGAN) {new VeganObj(sharedstuff.foodContext);}
			if (essen == consts.ESSEN_MEAT) {new MeatObj(sharedstuff.foodContext);}
			if (essen == consts.ESSEN_SALAD) {new SaladObj(sharedstuff.foodContext);}
			if (essen == consts.ESSEN_POMMES) {new PommesObj(sharedstuff.foodContext);}
			return true;
		}
		
		double randomNum = RandomHelper.nextDoubleFromTo(0, 1);
		// VEGGIE
		if (food_preference == consts.VEGGIE && consts.vegetarian.contains(essen)) {
			if (essen == consts.ESSEN_VEGGIE && randomNum <= sharedstuff.foodParam[0]) {new VeggieObj(sharedstuff.foodContext); return true;}
			if (essen == consts.ESSEN_VEGAN && randomNum <= sharedstuff.foodParam[1]) {new VeganObj(sharedstuff.foodContext); return true;}
			if (essen == consts.ESSEN_SALAD && randomNum <= sharedstuff.foodParam[2]) {new SaladObj(sharedstuff.foodContext); return true;}
			if (essen == consts.ESSEN_POMMES && randomNum <= sharedstuff.foodParam[3]) {new PommesObj(sharedstuff.foodContext); return true;}
		}
		// VEGAN
		else if (food_preference == consts.VEGANER && consts.vegan.contains(essen)) {
			if (essen == consts.ESSEN_VEGAN && randomNum <= sharedstuff.foodParam[4]) {new VeganObj(sharedstuff.foodContext); return true;}
			if (essen == consts.ESSEN_SALAD && randomNum <= sharedstuff.foodParam[5]) {new SaladObj(sharedstuff.foodContext); return true;}
			if (essen == consts.ESSEN_POMMES && randomNum <= sharedstuff.foodParam[6]) {new PommesObj(sharedstuff.foodContext); return true;}
		}
		// MEAT
		else if (food_preference == consts.MEAT && consts.meatlover.contains(essen)) {
			if (essen == consts.ESSEN_VEGGIE && randomNum <= sharedstuff.foodParam[7]) {new VeggieObj(sharedstuff.foodContext); return true;}
			if (essen == consts.ESSEN_VEGAN && randomNum <= sharedstuff.foodParam[8]) {new VeganObj(sharedstuff.foodContext); return true;}
			if (essen == consts.ESSEN_MEAT && randomNum <= sharedstuff.foodParam[9]) {new MeatObj(sharedstuff.foodContext); return true;}
			if (essen == consts.ESSEN_SALAD && randomNum <= sharedstuff.foodParam[10]) {new SaladObj(sharedstuff.foodContext); return true;}
			if (essen == consts.ESSEN_POMMES && randomNum <= sharedstuff.foodParam[11]) {new PommesObj(sharedstuff.foodContext); return true;}
		}
		// No Preference
		else if (food_preference == consts.NOPREFERENCE && consts.noPref.contains(essen)) {
			if (essen == consts.ESSEN_VEGGIE) {new VeggieObj(sharedstuff.foodContext);}
			if (essen == consts.ESSEN_VEGAN) {new VeganObj(sharedstuff.foodContext);}
			if (essen == consts.ESSEN_MEAT) {new MeatObj(sharedstuff.foodContext);}
			if (essen == consts.ESSEN_SALAD) {new SaladObj(sharedstuff.foodContext);}
			if (essen == consts.ESSEN_POMMES) {new PommesObj(sharedstuff.foodContext);}
			return true;
		}
		return false;
	}

	public boolean chooseMeal(Ausgabe a) {
		boolean tmp = chooseMeal_inner(a);
		if (tmp)
			System.out.println("Student #" + this.num + " hat sich ein Essen gesucht.");
		return tmp;
	}
	

		
	
	
	
	public int getTickCount() {
		return this.tickCount;
	}

	// ==================================== Grid methods ====================================

	//to be overridden	
	public Ausgabe next_ausgabe() {
		return null;
	}

	/**
	 * Methode wird jede Runde ausgefuehrt 
	 */
	public void step_grid(){	
		if (!inQueue) {
				Ausgabe nextBar;
				if (this.ThefoodIsOkay) { 					// Wenn das essen gut ist gehe zur Kasse ansonsten hole dir was neues
					nextBar = sharedstuff.ausgaben.get(0); //TODO war Share.saladList.get(0); 
					if (this.wantSalad && nextBar.getStudentsInQueue() < 15) { // wenn du salat willst & Schlange kleiner 15 gehe zur salatbar 
						this.current = nextBar;
						this.wantSalad = false;
					} else {								// warst du bereits an der salatbar oder willst du keinen Salat gehe zur kasse
						nextBar = toKasse();
						this.current = nextBar;
					}					
				} else {									// Such dir deinen Weg wenn du noch kein Essen gefunden hast
					nextBar = next_ausgabe(); //Kann auch zu ner Kasse gehen
					if (nextBar != null) {
						this.visitedAusgaben.add(nextBar);
						this.current = nextBar;
						DoYouWantThatFood();				// Willst du das Essen von dieser Bar?
						// TODO wenn du das Essen nicht nimmst gehe zu einer anderen Bar Do-While Schleife
						// Jedoch geht man dann direkt zur Ziel Bar somit sind alle goal Oriented weil keiner mehr unn�tig ansteht
					}  else {								// Falls alle Bars gesehen gehe zur Kasse
						nextBar = toKasse();
						this.current = nextBar;
					}
				}
				int[] lastQueuePos = nextBar.getLastQueuePos(this); // Gehe zum ende der Queue
				this.inQueue = true;
				this.nextLocX = lastQueuePos[0];
				this.nextLocY = lastQueuePos[1];
				this.waitTicks = nextBar.getWaitTicks();					// Get waitTicks from current Bar
	//			System.out.println("#"+this.num + " [" +nextLocX+ " , " +nextLocY+ "]");
		} else if (current.firstInQueue(this)){					// DU bist der ERSTE in einer Queue	
//			System.out.println("Warte: " + waitTicks);
				if (this.waitTicks > 0) {							// Warte vor der Ausgabe
					this.waitTicks--;
					this.waiting = true;
				} else {
					this.waiting = false;							
	//				System.out.println("First in Queue: #"+this.num + " [" +this.nextLocX+ " , " +this.nextLocY+ "] " + current.kind);
					this.inQueue = false;
					current.removeFromQueue();
					// Leave the Mensa
					if (current instanceof Kasse) {
						remove_me();
						return;
					} else {										// Ansonsten suche dir im naechsten Schritt eine neue Ausgabe 
						System.out.println("#"+this.num + " Next Bar");
						this.current = null;
					}
				}
		} else {												// Du stehst in einer Schlange
			int[] nextPos = this.current.moveForwardInQueue(this);	// Sieh nach ob du eine Position weiter aufruecken kannst
			if (nextPos == null) {								// Falls du nicht nachruecken kannst warte einen Zeitschritt
				this.waiting = true;
			} else {											// Du kannst nachruecken
				this.waiting = false;
				this.nextLocX = nextPos[0];
				this.nextLocY = nextPos[1];
			}
		}	
		update();
	} // End Of Step.
	
	

	/**
	 * Status "updaten" immer nach dem step
	 */
	public void update(){
		this.spentTicks++;									// DATA
		if (this.waiting) return;							// return wenn du warten musst
		if (this.current == null) return;					// return wenn du an erster Stelle standest und erst im naechsten Schritt eine neue Loc bekommst
		grid.moveTo(this, this.nextLocX, this.nextLocY);	// Bewege dich zu deinem naechsten Ziel
	}	
	
	public Kasse toKasse() {
		int index = sharedstuff.kassen.size();
		Kasse randomCheckout = sharedstuff.kassen.get(RandomHelper.nextIntFromTo(0, index-1)); // zufaellige Wahl der Kasse
		return randomCheckout;
	}	
	
	public void DoYouWantThatFood() {
		if (RandomHelper.nextIntFromTo(0, 3) == 0) this.ThefoodIsOkay = true;	// Entscheide dich mit 1/4 ob du das Essen nimmst.
		else this.ThefoodIsOkay = false;
	}
	
	// TODO Wenn die schlange an der Salatbar zu lang ist setze wantSalad auf false
	public void DoYouWantSalad() {
		if (RandomHelper.nextIntFromTo(0, 1) == 0) this.wantSalad = true; 		// Entscheide dich mit 1/2 ob du Salat willst
		else this.wantSalad = false;
	}
	
	public void setBarList() {
		List<Integer> temp = new ArrayList<Integer>();
		if (this.food_preference == consts.MEAT) temp = consts.meatlover;
		else if (this.food_preference == consts.VEGGIE) temp = consts.vegetarian;
		else if (this.food_preference == consts.VEGANER) temp = consts.vegan;
		else temp = consts.noPref;
		for (Ausgabe bar : sharedstuff.ausgaben) {
			if (temp.contains(bar.essen)) this.barList.add(bar);
		}
	}
	
	// DATA
	public double calcMeanSpentTicks() {
		int sum = 0;
		for (Student s : sharedstuff.studierende) {
			sum = sum + s.spentTicks;
		}
		return sum / sharedstuff.studierende.size();		// Returns Mean 
	}
	
	

	// ==================================== Walking methods ==================================== 
	
	

	//to be overridden
	public Vector2d move_spatial() {
		return new Vector2d(0,0);
	}
	
	
	// der student geht zur Kasse
	public Kasse to_kasse() {
		return get_closest(sharedstuff.kassen);
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
	public <T> T get_closest(List<T> lst) {
		Vector2d distXY = new Vector2d(999999,999999);
		Vector2d tmpdist = null;
		T res = null;
		try {
			for (T obj : lst) {
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
		List<Integer> between = sharedstuff.mgrid.Bresenham((int)mypos.getX(), (int)mypos.getY(), (int)thatpos.getX(), (int)thatpos.getY());
		between = between.subList(1, between.size()-1);
		if (between.size() == 0) return distance;
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
					if (mypos.getY() < 19 && mypos.getY() > 11 && mypos.getX() < 62 && mypos.getX() > 48)
						distance.y = distance.getX()*10*(-0.5);
					else if (mypos.getY() < 19 && mypos.getY() > 11 && mypos.getX() < 48 && mypos.getX() > 38)
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
		ContinuousWithin ausgabeInRange = new ContinuousWithin(space, this, 3);  //TODO das soll 'ne funktion von der Ausgabe sein, dann wird sie seltener ausgeführt!! (die ausgabe kann ja ne liste führen wer an ihr ist, und der student muss das nur abfragen)
		for (Object b : ausgabeInRange.query()) {
			if (b instanceof Ausgabe && (!visitedAusgaben.contains(b)) || this.tempDestination == b) {
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


  //@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		this.tickCount++;
		
		//Priorität 1) Laufe nicht gegen andere
		Vector2d avoidance = avoid_others();
		if (avoidance != null) {
			velocity.setX(-avoidance.x);
			velocity.setY(-avoidance.y);
			do_move();
			return;
		} 
		
		//Priorität 2) Laufe zu Ausgaben (returns null wenn er gerade was zu essen gefunden hat, nicht mehr hungrig ist, oder schon alle Theken besucht hat)
		Vector2d movement = move_spatial(); //move ist überschrieben für die 3 Tochterklassen
		if (movement != null) {
			velocity.setX(movement.x);
			velocity.setY(movement.y);
			do_move();
			return;
		} 
		
		//Wenn wir jetzt noch da sind sollten wir zur Kasse gehen
		
		
		if (tempKasse != null && tempKasse.pay(this)) {
			remove_me();
			return;
		} else {
			this.tempKasse = to_kasse();
			this.directlyToKassa = walk_but_dont_bump(this.tempKasse);
			if (this.directlyToKassa != null) {
				velocity.setX(this.directlyToKassa.x);
				velocity.setY(this.directlyToKassa.y);
			}
		}
		do_move();
		
	}
	
	public void remove_me() {
		System.out.println("Student #" + this.num + " hat die Mensa verlassen.");
		if (space != null && sharedstuff.mgrid != null) {
			NdPoint mypos = space.getLocation(this);
			sharedstuff.mgrid.set((int)mypos.getX(), (int)mypos.getY(), 0);
		}
		context.remove(this);
		//sharedstuff.schedule.removeAction(scheduledStep);
		sharedstuff.remove_these.add(this);
		sharedstuff.schedule.schedule(ScheduleParameters.createOneTime(sharedstuff.schedule.getTickCount()+0.01, 1), sharedstuff.builder, "remove_studs"); //priority 1
	}


	/*
	 * Eigentliche Bewegung zwischen den Zeitschritten.
	 */
	public void do_move(){
		if (waitticks > 0) {
			waitticks --;
			return;
		}
		velocity = scaleAndNormalize(velocity);		
		NdPoint pos = space.getLocation(this);
		sharedstuff.mgrid.set((int)pos.getX(), (int)pos.getY(), 0);
		Vector2d potentialcoords = new Vector2d(pos.getX()+velocity.x, pos.getY()+velocity.y);
		//System.out.println("Student #"+num+" did something"+pos.getX()+" "+pos.getY()+" velocity "+velocity.x+" "+velocity.y);
		if (potentialcoords.x <= 0 || potentialcoords.x >= consts.SIZE_X || potentialcoords.y <= 0 || potentialcoords.y >= consts.SIZE_Y){
			throw new java.lang.RuntimeException("Student ausserhalb der Mensa.");
		}
		int potential_grid_pos = sharedstuff.mgrid.get((int)potentialcoords.x, (int)potentialcoords.y);
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
					int leftofthat  = sharedstuff.mgrid.get((int)potentialcoords.x-1, (int)potentialcoords.y);
					int rightofthat = sharedstuff.mgrid.get((int)potentialcoords.x+1, (int)potentialcoords.y);
					int topofthat    = sharedstuff.mgrid.get((int)potentialcoords.x, (int)potentialcoords.y-1);
					int bottomofthat = sharedstuff.mgrid.get((int)potentialcoords.x, (int)potentialcoords.y+1); //TODO fehler fangen
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
			potential_grid_pos = sharedstuff.mgrid.get((int)potentialcoords.x, (int)potentialcoords.y);
		}
		sharedstuff.mgrid.set((int)potentialcoords.x, (int)potentialcoords.y, consts.GRID_STUDENT);
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
