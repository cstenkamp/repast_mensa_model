package mensamodell;

import java.util.ArrayList;

import javax.vecmath.Vector2d;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

//student laeuft rum, sieht theke, called enqueueInTheke, ab dann geht er in jedem zeitschritt ggf nen schlangenplatz vor
//wenn er die theke sieht sieht er aber auch die länge der schlange und reiht sich nur ggf ein

//TODO kasse erbt von ausgabe

public class Ausgabe {
	int x;
	int y;
	public int kind;
	public int essen;
	
	//relevant if grid
	private Grid<Object> grid;
	ArrayList<Student> studentsInQueue;
	int[] shift = null; //TODO den von der position berechnen
		
	//relevant if space
	protected ContinuousSpace space;
	private double barRange = 5;
	
	
	public int getStudentsInQueue() {
		return studentsInQueue.size(); 
	}
	
	public Ausgabe(int x, int y, int kind, int food_here, Context<Object> context, ContinuousSpace s) {
		this.x = x;
		this.y = y;
		space = s;
		this.kind = kind;
		this.studentsInQueue = new ArrayList<Student>();
		this.shift = null;
		context.add(this);
		space.moveTo(this, x, y);
	}
	
	//fuer Grid
	public Ausgabe(int x, int y, int kind, int food_here, Context<Object> context, Grid<Object> grid){
		this.x = x;
		this.y = y;
		this.kind = kind;
		this.essen = food_here;
		this.grid = grid;
		this.studentsInQueue = new ArrayList<Student>();
		this.shift = getShift();  
		context.add(this);
		grid.moveTo(this, x, y);
	}
	
  @Override
  public String toString() { 
  	  String ausgaben_name;
  	  switch(kind) {
	  	case consts.AKTIONSTHEKE: ausgaben_name = "Aktionstheke"; break;
	  	case consts.FLEISCHTHEKE: ausgaben_name = "Fleischtheke"; break;
	  	case consts.POMMES: ausgaben_name = "Pommestheke"; break;
	  	case consts.VEGGIETHEKE: ausgaben_name = "Veggietheke"; break;
	  	case consts.EINTOPF: ausgaben_name = "Eintopftheke"; break;
	  	case consts.SCHNELLERTELLER: ausgaben_name = "SchnellerTeller"; break;
	  	case consts.SALATBAR: ausgaben_name = "Salatbar"; break; 
	  	case consts.VEGANTHEKE: ausgaben_name = "Vegantheke"; break; 
	  	default: ausgaben_name = "Unknown";
  	  }
  	  String essens_name;
  	  switch(essen) {
	  	case consts.ESSEN_MEAT: essens_name = "Fleisch"; break;
	  	case consts.ESSEN_VEGGIE: essens_name = "Veggie"; break;
	  	case consts.ESSEN_VEGAN: essens_name = "Vegan"; break;
	  	case consts.ESSEN_SALAD: essens_name = "Salat"; break;
	  	case consts.ESSEN_POMMES: essens_name = "Pommes"; break;
	  	default: essens_name = "Unknown";
  	  }
      return "Ausgabe("+ausgaben_name+" mit "+essens_name+")";
  } 
	
	
	//TODO die muessen mal like 10.000 falls nicht mit grid
	int getWaitTicks() { 
		int wait_time = 0;
		switch (kind) {
		case consts.SALATBAR:
			wait_time = consts.waitSalad;
			break;
		case consts.AKTIONSTHEKE:
			wait_time = consts.waitAktion;
			break;
		case consts.FLEISCHTHEKE:
			wait_time = consts.waitMeat;
			break;
		case consts.VEGGIETHEKE:
			wait_time = consts.waitVeggie;
			break;
		case consts.VEGANTHEKE:
			wait_time = consts.waitVegan;
			break;
		case consts.EINTOPF:
			wait_time = consts.waitEintopf;
			break;
		case consts.SCHNELLERTELLER:
			wait_time = consts.waitSchnellerTeller;
			break;
		case consts.POMMES:
			wait_time = consts.waitPommes;
			break;
		default:
			wait_time = 3;
		}
		return wait_time;	
	}
	
	
	
	public boolean isLeft() {
		return x > consts.SIZE_X/2;
	}
	
	
	public boolean isEmpty() {
		ContinuousWithin StudentInBarRange = new ContinuousWithin(space, this, barRange);
		for (Object s : StudentInBarRange.query()) {
			if (s instanceof Student) return true;
		}
		return false;
	}
	
	public int getEssen() {
		return this.essen;
	}
	
	
	// In welche Richtung ausgehend von der Position der Ausgabe geht die Schlange
	protected int[] getShift() {
		return new int[] {0,1}; 
	}


	//TODO alle diese funktionen anschauen!!
	
	public int[] getLastQueuePos(Student s) {
		int[] lastQueuePos = null;
		if (this.studentsInQueue.isEmpty()) {
			lastQueuePos = getFirstPos();						// First Queue Location
//			System.out.println("First in Queue: #"+s.num + " [" +lastQueuePos[0]+ " , " +lastQueuePos[1]+ "] " + this.kind);
		} else {												// get last in queue
			Student z = this.studentsInQueue.get(studentsInQueue.size()-1);
			lastQueuePos = new int[] {z.nextLocX + shift[0], z.nextLocY + shift[1]};
//			System.out.println("Next in Queue: #"+s.num + " [" +lastQueuePos[0]+ " , " +lastQueuePos[1]+ "] " + this.kind);
		}
		this.studentsInQueue.add(s);
		return lastQueuePos;
	}
	
	public int[] moveForwardInQueue(Student s) {
		int[] nextPos = new int[] {s.nextLocX - shift[0], s.nextLocY - shift[1]}; 
		for (Student z : this.studentsInQueue) {
			if (z.nextLocX == nextPos[0] && z.nextLocY == nextPos[1]) return null; // Ueberpruefe ob das Feld vor dir in der Schlange frei ist
			//TODO hier kann man denen Reihenfolge für's scheduling geben
		}
		return nextPos;
	}
		
	
	public int[] getFirstPos() {
		return new int[] {this.x + shift[0], this.y + shift[1]};
	}
	
	public void removeFromQueue() {
		this.studentsInQueue.remove(0);
		ArrayList<Student> temQueue = new ArrayList<Student>();
		for (Student c : this.studentsInQueue) {
			if (c != null) temQueue.add(c);
		}
//		System.out.println(this.studentsInQueue.size() + " = " + temQueue.size());
		this.studentsInQueue = temQueue;
	}
	
	public boolean firstInQueue(Student s) {
		if (s.equals(getFirstInQueue())) return true;
		else return false;
	}
	
	public Student getFirstInQueue() {
//		Student firstInQueue = (Student) grid.getObjectAt(this.x + shift[0], this.y + shift[1]);
		Student firstInQueue = null;
		for (Student z : this.studentsInQueue) {
			if (z.nextLocX == this.x + shift[0] && z.nextLocY == this.y + shift[1]) firstInQueue = z;
		}
		return firstInQueue;
	}
	
	
	@ScheduledMethod(start = 0, interval = 1)
	public void step() {
		int num_in_queue = 2; //priorität 1 ist aus context entfernen, schlangen starten ab 2
		//wenn du studierende in deiner schlange hast, lasse sie alle einen schritt machen, vom ersten in der schlange zum letzten!
		for (Student s : this.studentsInQueue) {
			s.scheduledSteps.add(s.sharedstuff.schedule.schedule(ScheduleParameters.createOneTime(s.sharedstuff.schedule.getTickCount()+1, num_in_queue++), s, "step_grid")); 
		}
	}
	
	
	
} // END of Class
