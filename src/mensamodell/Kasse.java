package mensamodell;

import javax.media.j3d.Shape3D;

import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.visualization.visualization3D.ShapeFactory;

public class Kasse { //exends Ausgabe
	int x;
	int y;
	private ContinuousSpace space;
	private double payRange = 3;
	
	
	public Kasse(int x, int y, ContinuousSpace s) {
		this.x = x;
		this.y = y;
		this.space = s;
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
  
  private int getWaitTicks() {
  	return consts.waitKasse;
  }
  
	// In welche Richtung ausgehend von der Position der Ausgabe geht die Schlange
	private int[] getShift(int kind) {
		return new int[] {0, -1};
	}
  
  
}
