package mensamodell;


import repast.simphony.context.Context;
import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Kasse extends Ausgabe {

	//variable nur relevant für space-Modell
	private double payRange = 3;
	
	
	//space-variante
	public Kasse(int x, int y, Context<Object> context, ContinuousSpace s) {
		super(x, y, -1, -1, context, s);
	}

	//grid-variante
	public Kasse(int x, int y, Context<Object> context, Grid<Object> g) {
		super(x, y, -1, -1, context, g);
	}
	
  // Prueft ob studenten entfernt werden koennen
  public boolean pay(Student s) {
	  ContinuousWithin StudentPayRange = new ContinuousWithin(space, this, payRange);
		for (Object k : StudentPayRange.query()) {
			// Wenn student s in pay Range 
			if (k instanceof Student && k == s) {
				return true;
			}
		}
	  return false;
  }
  
  int getWaitTicks() {
  	return consts.waitKasse;
  }
  
	// In welche Richtung ausgehend von der Position der Ausgabe geht die Schlange
  protected int[] getShift() {
	  return new int[] {0,-1}; 
	}
  
  
}
