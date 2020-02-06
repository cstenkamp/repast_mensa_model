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
	List<ISchedulableAction> scheduledSteps;

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
	List<Ausgabe> consideredBarsList;
	Ausgabe current;
	int waitTicks;
	int spentTicks;	 // DATA
	int additionallywants = -1; //entweder -1, dann will er nur ne Hauptmahlzeit, oder consts.SALAD oder consts.POMMES
	int additionallywants_orig = -1;

	private void global_construct(int num, SharedStuff sharedstuff, int fp, Context<Object> context) {
		this.food_preference = fp;
		this.visitedAusgaben = new ArrayList<Ausgabe>();
		this.context = context;
		this.waitticks = 0;
		this.sharedstuff = sharedstuff;
		this.num = num;
		this.best_food_so_far = new ArrayList<Ausgabe>();
		context.add(this);
		sharedstuff.studierende.add(this);
		additionallywants = want_salad_or_fries();	// Entscheide zu beginn ob du Salat/Pommes willst.
		additionallywants_orig = additionallywants; //zum printen
		consideredBarsList = setConsideredBars(additionallywants); //wenn er Salat/Pommes ZUSÄTZLICH will will er das nicht mehr als Hauptmahlzeit.
	}


	//constructor fuer Grid
	public Student(int num, SharedStuff sharedstuff, int fp, Context<Object> context, Grid<Object> g, int x, int y) {
		global_construct(num, sharedstuff, fp, context); //viel ist gleich ob fuer grid oder für space

		this.grid = g;
		this.inQueue = false;
		this.waiting = false;
		this.current = null;
		this.nextLocX = x;
		this.nextLocY = y;
		this.spentTicks = 0;							// DATA

		grid.moveTo(this, (int)x, (int)y);

		scheduledSteps = new ArrayList<ISchedulableAction>();
		scheduledSteps.add(sharedstuff.schedule.schedule(ScheduleParameters.createRepeating(sharedstuff.schedule.getTickCount()+1, 1), this, "step_grid"));
	}

	
	protected String get_pref_string() {
		String foodprefstring;
		switch (food_preference) {
			case consts.MEAT: foodprefstring = "Meateater"; break;
			case consts.VEGGIE: foodprefstring = "Vegetarier"; break;
			case consts.VEGANER: foodprefstring = "Veganer"; break;
			default: foodprefstring = "NoPrefEater"; break;
		}
		if (additionallywants_orig == consts.ESSEN_SALAD) foodprefstring += "+Salad";
		if (additionallywants_orig == consts.ESSEN_POMMES) foodprefstring += "+Pommes";
		return foodprefstring;
	}
	


  //Überschrieben im StudentGoalOriented: der mags in jedem Fall da er eh nur hingeht wo er's mag
	public boolean chooseMeal(Ausgabe currentBar) {
		// warte vor der Ausgabe (nur im space)
		if (sharedstuff.space != null)
			this.waitticks = currentBar.getWaitTicks();

		//Essen funktioniert so: wenn das Essen vom vegetarismus-Grad zu ihnen passt, nehmen sie es zu einem gewissem Prozentsatz sofort. Wenn sie am Ende
		//alle Ausgaben abgelaufen sind, ohne was zu nehmen, nehmen sie das ein zufälliges von denen die "am besten" zu ihnen passen.
		int essen = currentBar.getEssen();
	
		if (essen == additionallywants_orig) //wenn er zusätzlich salat/pommes will nimmt ers in jedem fall (anderenfalls gehet er nicht an diese schlnage) 
			return true;

		if (essen >= food_preference) { //foodpreference and food are sorted by priority: a person of type meat will like the lowest one most. A person of type vegan will like the lowest one >= itself (=2) most.
			if ((best_food_so_war_was > essen) && (food_preference != consts.NOPREFERENCE)) { //die mit no preference fügen einfach alles zu ihrer best_so_far liste hinzu, alle anderen nur die die sie am meisten mochten.
				best_food_so_war_was = essen;
				best_food_so_far = new ArrayList<Ausgabe>();
			}
			best_food_so_far.add(currentBar);
		}

		if (best_food_so_war_was == -1) { //das ist der Fall wenn du schon alle durchprobiert hast
			return true;
		}

		double randomNum = RandomHelper.nextDoubleFromTo(0, 1);
		if (food_preference == consts.VEGGIE && consts.vegetarian.contains(essen)) {           // VEGGIE
			if (essen == consts.ESSEN_VEGGIE && randomNum <= sharedstuff.foodParam[0]) return true;
			if (essen == consts.ESSEN_VEGAN && randomNum <= sharedstuff.foodParam[1]) return true;
			if (essen == consts.ESSEN_SALAD && randomNum <= sharedstuff.foodParam[2]) return true;
			if (essen == consts.ESSEN_POMMES && randomNum <= sharedstuff.foodParam[3]) return true;
		}
		else if (food_preference == consts.VEGANER && consts.vegan.contains(essen)) {           // VEGAN
			if (essen == consts.ESSEN_VEGAN && randomNum <= sharedstuff.foodParam[4]) return true;
			if (essen == consts.ESSEN_SALAD && randomNum <= sharedstuff.foodParam[5]) return true;
			if (essen == consts.ESSEN_POMMES && randomNum <= sharedstuff.foodParam[6]) return true;
		}
		else if (food_preference == consts.MEAT && consts.meatlover.contains(essen)) {		     // MEAT
			if (essen == consts.ESSEN_VEGGIE && randomNum <= sharedstuff.foodParam[7]) return true;
			if (essen == consts.ESSEN_VEGAN && randomNum <= sharedstuff.foodParam[8]) return true;
			if (essen == consts.ESSEN_MEAT && randomNum <= sharedstuff.foodParam[9]) return true;
			if (essen == consts.ESSEN_SALAD && randomNum <= sharedstuff.foodParam[10]) return true;
			if (essen == consts.ESSEN_POMMES && randomNum <= sharedstuff.foodParam[11]) return true;
		}
		else if (food_preference == consts.NOPREFERENCE && consts.noPref.contains(essen)) {	  	// No Preference
			return true;
		}
		return false;
	}
	
	
	public void create_food_obj(Ausgabe currentBar) {
		int essen = currentBar.getEssen();
		if (essen == consts.ESSEN_VEGGIE) new VeggieObj(sharedstuff.foodContext);
		if (essen == consts.ESSEN_VEGAN) new VeganObj(sharedstuff.foodContext);
		if (essen == consts.ESSEN_MEAT) new MeatObj(sharedstuff.foodContext);
		if (essen == consts.ESSEN_SALAD) new SaladObj(sharedstuff.foodContext);
		if (essen == consts.ESSEN_POMMES) new PommesObj(sharedstuff.foodContext);
	}


	public int want_salad_or_fries() {
		if (RandomHelper.nextDoubleFromTo(0, 1) < consts.WANTS_SALADFRIES_PROB) {
			if (RandomHelper.nextDoubleFromTo(0, 1) < consts.IFWANTS_WANTS_FRIES) 
				return consts.ESSEN_POMMES;
			else
				return consts.ESSEN_SALAD;
		}
		return -1;
	}
	

	public int getTickCount() {
		return this.tickCount;
	}


	public void remove_me() {
		consts.print(this + " hat die Mensa verlassen.");
		if (space != null && sharedstuff.mgrid != null) {
			NdPoint mypos = space.getLocation(this);
			sharedstuff.mgrid.set((int)mypos.getX(), (int)mypos.getY(), 0);
		}
		context.remove(this);
		//sharedstuff.schedule.removeAction(scheduledStep);
		sharedstuff.remove_these.add(this);
		sharedstuff.schedule.schedule(ScheduleParameters.createOneTime(sharedstuff.schedule.getTickCount()+0.01, 1), sharedstuff.builder, "remove_studs"); //priority 1

  	if (!sharedstuff.students_that_left.contains(this))
  			sharedstuff.students_that_left.add(this);
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
		if (!inQueue) {													//WENN NICHT IN SCHLANGE
				Ausgabe nextBar = next_ausgabe();
				if (nextBar != null) {
					this.visitedAusgaben.add(nextBar);
					this.current = nextBar; 
				}	else { //dann bist du alle durchgegangen
					nextBar = toKasse();
					this.current = nextBar;
				}
				getInQueue();
		} else if (current.firstInQueue(this)) { //WENN ERSTER IN SCHLANGE
				if (this.waitTicks > 0) { // Warte vor der Ausgabe
					this.waitTicks--;
					this.waiting = true;
				} else {  //Du bist dran!!
					this.waiting = false; 
					getOutOfQueue();
					if (current instanceof Kasse) { //Wenn du an einer Kasse stehst
						remove_me();
						return;
					} else {							
						if (chooseMeal(this.current)) {
							consts.print(this+" found a meal at "+this.current);
							create_food_obj(this.current);
							if (additionallywants != -1) {
								Ausgabe tmp = null;
								if (additionallywants == consts.ESSEN_POMMES) tmp = sharedstuff.pommesbar;
								if (additionallywants == consts.ESSEN_SALAD) tmp = sharedstuff.salatbar;
								if (tmp.getStudentsInQueue() < consts.SIZE_Y-5)
									current = tmp;
								else
									throw new IndexOutOfBoundsException(this+": Salat/Pommesbar zu voll! Weiß nicht was er tun soll!");
								additionallywants = -1;
							} else {
								this.current = toKasse();
							}
							getInQueue();
						} else {
							this.current = null;							
						}
					}
			  }
		} else {																//WENN NICHT ERSTER IN SCHLANGE
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
	

	public void getInQueue() {
		int[] lastQueuePos = this.current.getLastQueuePos(this); // Gehe zum ende der Queue
		this.nextLocX = lastQueuePos[0];
		this.nextLocY = lastQueuePos[1];
		this.waitTicks = this.current.getWaitTicks();	
		
		inQueue = true;
		//wenn du in der queue bist sollst du nicht mehr eigenständiges movement machen sondern kriegst von der schlange immer aufs neue gesagt dass du dich 1x bewegen darst.
		//warum? damit keine lücken in der schlange entstehen, per prioritäten bewegt sich der erste zuerst, ...
		sharedstuff.remove_these.add(this);
		sharedstuff.schedule.schedule(ScheduleParameters.createOneTime(sharedstuff.schedule.getTickCount()+0.01, 1), sharedstuff.builder, "remove_studs"); //priority 1
	}

	public void getOutOfQueue() {
		current.removeFromQueue();
		inQueue = false;
		if (!(current instanceof Kasse)) {
			scheduledSteps.add(sharedstuff.schedule.schedule(ScheduleParameters.createRepeating(sharedstuff.schedule.getTickCount()+1, 1), this, "step_grid"));
			//dann laufe wieder eigenständig
		}
	}


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
		//nehme eine zufällige Kasse dessen Schlange kurz genug ist
		List<Integer> allowed_indices = new ArrayList<Integer>();
		for(int i = 0; i < sharedstuff.kassen.size(); i++) 
			if (sharedstuff.kassen.get(i).getStudentsInQueue() < consts.SIZE_Y-5)
				allowed_indices.add(i);
		
		if (allowed_indices.isEmpty())
			throw new IndexOutOfBoundsException();
		
		Kasse randomCheckout = sharedstuff.kassen.get(allowed_indices.get(RandomHelper.nextIntFromTo(0, allowed_indices.size()-1)));
		return randomCheckout;
	}

	public List<Ausgabe> setConsideredBars(int wants_salad_or_fries) {
		List<Ausgabe> res = new ArrayList<Ausgabe>();
		List<Integer> temp = new ArrayList<Integer>();
		if (this.food_preference == consts.MEAT) temp = consts.meatlover;
		else if (this.food_preference == consts.VEGGIE) temp = consts.vegetarian;
		else if (this.food_preference == consts.VEGANER) temp = consts.vegan;
		else temp = consts.noPref;
		for (Ausgabe bar : sharedstuff.ausgaben) {
			if (temp.contains(bar.essen)) 
				if (bar.essen != wants_salad_or_fries)  //wenn er ZUSÄTZLICH salad or fries will will er's nicht als Hauptmahlzeit
					res.add(bar);
		}
		return res;
	}

	// DATA
	public double calcMeanSpentTicks() {
		List<Student> temp = new ArrayList<Student>();
		if (this instanceof StudentShortestQueue) temp = sharedstuff.studierendeQueue;
		else if (this instanceof StudentChaotic) temp = sharedstuff.studierendeChaotic;
		else if (this instanceof StudentGoalOriented) temp = sharedstuff.studierendeGoal;
		else temp = sharedstuff.studierende;
		int sum = 0;
		for (Student s : temp) {
			if (!sharedstuff.students_that_left.contains(s))
				sum = sum + s.spentTicks;
		}
		return sum / temp.size();		// Returns Mean
//		return spentTicks;
	}



	// ==================================== Walking methods ====================================


	//constructor für walking
	public Student(int num, SharedStuff sharedstuff, int fp, Context<Object> context, ContinuousSpace<Object> s) {
		global_construct(num, sharedstuff, fp, context);

		this.hungry = true;
		this.space = s;
		this.velocity = new Vector2d(0,0);
		this.tempDestination = null; // stellt sicher dass der student bis zur Ausgabe laeuft

		float x = RandomHelper.nextIntFromTo(consts.SIZE_X*2/5, consts.SIZE_X*3/5);
		float y = consts.SIZE_Y-5;
		space.moveTo(this, x, y); // add students to space or grid

		scheduledSteps = new ArrayList<ISchedulableAction>();
		scheduledSteps.add(sharedstuff.schedule.schedule(ScheduleParameters.createRepeating(sharedstuff.schedule.getTickCount()+1, 1), this, "step"));
	}


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
